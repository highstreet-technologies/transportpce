#!/usr/bin/env python3

import os
import sys
import json
import argparse
import time
import yaml
import glob
from typing import List
from constants import *
import networkx as nx
from lib.xmlParser import OpenRoadmXmlParser
from lib.ntsngDeployGenerator import OpenroamdNtsNgDeployGenerator
from splitRoadmModel import RoadmModelSplitter
from lib.inttestProfiles import IntegrationTestSimProfile, IntegrationTestControllerProfile
import subprocess


class NTSDeviceModelCreator:

    def __init__(self, nodesSourceFile, linksSourceFile, templateFolder, outputProfile,
                 outputFolder, outputDockerComposeFile, yangPath):
        for name, val in (("nodesSourceFile", nodesSourceFile),
                         ("linksSourceFile", linksSourceFile),
                         ("outputProfile", outputProfile)):
            if not val:
                raise ValueError(f"Missing required argument: {name}")
        self.linksSourceFile = linksSourceFile
        self.nodesSourceFile = nodesSourceFile
        self.templateFolder = templateFolder
        self.outputProfile = outputProfile
        self.outputFolder = outputFolder
        self.outputDockerComposeFile = outputDockerComposeFile
        self.edges=None
        self.nodes=None
        self.roadmTemplateFile = f"{templateFolder}/oper-ROADMA.xml"
        self.xpdrTemplateFile = f"{templateFolder}/oper-XPDRA.xml"
        self.yangPath = yangPath or f"{TRPCEPATH}/integration/yang"

    def run(self):
        # Variable to be provided w.r.t to different environments
        ip = os.getenv('NTS_NG_IP', '127.0.0.1')  # Default to localhost if not set
        for path in (self.nodesSourceFile, self.linksSourceFile):
            if not os.path.isfile(path):
                print(f"ERROR: required input file not found: {path}", file=sys.stderr)
                sys.exit(1)
        with open(self.nodesSourceFile) as node_file:
            self.nodes = json.load(node_file)
        with open(self.linksSourceFile) as edge_file:
            self.edges = json.load(edge_file)

        topo=self.createTopology()
        i=1
        xpdrNum = 1
        portNumber = 50001
        rdmConfiguration = {}
        simProfile = IntegrationTestSimProfile()
        COMPOSITION = {'services': {}, 'networks': {NETWORK_NAME:{'external': 'true'}}}
        COMPOSITION['services']['transportpce'] = self.createComposeData()

        xmlParser = OpenRoadmXmlParser(TRPCEPATH, roadmTemplateFile=self.roadmTemplateFile, xpdrTemplateFile=self.xpdrTemplateFile, outputPath=self.outputFolder)
        for node in topo.nodes():
            interfaceDict = {}
            degree = topo.degree(node)
            neighbours = list(topo.neighbors(node))
            remotePortsList = self.findRemotePort(topo, node)
            degree= topo.degree(node)
            rdmResp=xmlParser.create_data_models(str(node), degree, 'roadm', i,neighbours,remotePortsList)
            time.sleep(0.01)
            print(rdmResp)
            for i in range(0, len(neighbours)):
                xpdrResp = xmlParser.create_xpdr_data_models(str(node[:4]) + str(neighbours[i][:4]), 'xpdr', xpdrNum)
                xpdrNum = xpdrNum + 1
               # print(xpdrResp)
                interfaceDict[i + 1] = str(neighbours[i])
                time.sleep(0.01)
           # print("Node {} has {} degrees and links {}".format(str(node), degree, node_edges))
            rdmConfiguration[str(node)] = interfaceDict
            i = i + 1
           # print(rdmConfiguration)
            #print(len(rdmConfiguration))
            #remote_ports_list.clear()
            #interfaceDict.clear()
            #neighbours.clear()
        deviceList = self.findFiles(self.outputFolder, 'xml')
        deviceList=[i for i in deviceList if 'operational' not in i and 'running' not in i]
        deviceList.sort()


        try:
            treeFile = self.findFiles(self.templateFolder, 'org-openroadm-device*.tree')
            if len(treeFile) > 0:
                tree_path = treeFile[0]
                print(f"DEBUG: Found tree file: {tree_path}")  # Use the first found tree file
                for device in deviceList:
                    print(deviceList)
                    splitter = RoadmModelSplitter()
                    splitter.makeSplit(
                        xmlFilename=os.path.join(self.outputFolder, device),
                        treeFilename=tree_path
                    )
                    devName = device.split(".")[0]
                    devBody = self.createNtsNGDockerCompose(devName, ip, portNumber)
                    COMPOSITION['services'][devName] = devBody
                    # Create the json data for profile.json file for the Germany-17 node network
                    simProfile.addLocalContainerSim(nodeId=devName, port=830, username='netconf',
                                                    password='netconf!',
                                                    containerName=devName)
                    portNumber=portNumber+1
        except Exception as e:
            print(f"Error processing tree file: {e}")
        try:
            with open(self.outputDockerComposeFile, 'w') as outfile:
                yaml.dump(COMPOSITION, outfile, default_flow_style=False, sort_keys=False, indent=4)
            outfile.close()
        except FileNotFoundError:
            print('Error fetching docker compose file')


        # time.sleep(0.01)

        # print(json.dumps(profile_data, indent=4))
        # Create profile.json file for the Germany-17 node network
        simProfile.save(PROFILES_SIM_FOLDER+'/'+self.outputProfile+'.json')

        profileControl = json.dumps(self.createProfileController(), indent=4)
        with open(PROFILES_CONTROLLER_FOLDER+'/'+self.outputProfile+'.json', 'w') as f:
            f.write(profileControl)
        f.close()
        rdmConfig= json.dumps(rdmConfiguration, indent=4)
        with open(PROFILES_SIM_FOLDER+'/rdmConfiguration.json', 'w') as f:
            f.write(rdmConfig)
        f.close()

    def createTopology(self):
        graph = nx.Graph()
        for k,node in self.nodes.items():
            graph.add_node(node[0], lon=node[1], lat=node[2], pos=(node[1], node[2]),
                                num_of_IXPs=node[3],
                                num_of_DCs=node[4])
        for k,edge in self.edges.items():
            graph.add_edge(edge['startNode'], edge['endNode'], linkDist=round(edge['linkDist'], 2),
                                noChannels=edge['noChannels'], noSpans=edge['noSpans'],
                                spanList=edge['spanList'])
        return graph


    def findFiles(self, path, name):
        if '*' in name or '?' in name:
            return glob.glob(os.path.join(path, name))
        return [f for f in os.listdir(path) if f.endswith(name)]


    def findRemotePort(self, topology, actualNode):
        remotePortsList = []
        ngbrs = list(topology.neighbors(actualNode))
        # print("ngbrs are {}".format(ngbrs))
        for n in ngbrs:
            remote_ngbrs = list(topology.neighbors(n))
            remote_port = remote_ngbrs.index(actualNode)
            remotePortsList.append(remote_port + 1)
        return remotePortsList


    def createComposeData(self):
        envVarService = ['REMOTE_SDNRURL=${REMOTE_SDNR_URL}', 'REMOTE_WSURL=${REMOTE_SDNR_WSURL}',
                        'REMOTE_ODL_USERNAME=${SDNR_USERNAME}', 'REMOTE_ODL_PASSWORD=${SDNR_PASSWORD}',
                        'REMOTE_ODL_ENABLED=${REMOTE_ODL_ENABLED}', 'REMOTE_ODL_TRUSTALL=${TRUSTALL}',
                        'TRANSPORTPCE_SIMULATOR_MODE=true']
        volumeService = ["./conf_v7.1/org.ops4j.pax.logging.cfg:/opt/opendaylight/etc/org.ops4j.pax.logging.cfg"]
        portService = ["18181:8181"]
        networks=[NETWORK_NAME]
        transportpce = dict(
            image='${TRANSPORT_PCE_IMAGE}',
            container_name='transportpce',
            environment=envVarService,
            volumes=volumeService,
            ports=portService,
            networks=networks

        )

        return transportpce

    def createProfileController(self):
        profileContentController = {
            "sdnr": [
                {
                    "scheme": "http",
                    "host": "",
                    "port": 8181,
                    "username": "${SDNR_USERNAME}",
                    "password": "${SDNR_PASSWORD}",
                    "container": "sdnr",
                    "primary": True
                }
            ],
            "transportpce": {
                "scheme": "http",
                "host": "",
                "port": 8181,
                "username": "admin",
                "password": "admin",
                "container": "transportpce",
                "primary": True
            }
        }
        return profileContentController

    def createDatastores(self, treePath):
        fileFound = False
        try:
            treeFile = self.findFiles(treePath, '-device.tree')
            if len(treeFile)> 0:
                print(treeFile)
                # splitter = RoadmModelSplitter()
                # deviceFiles = self.findFiles(self.outputFolder, 'xml')
                # for deviceFile in deviceFiles:
                #     splitter.run(args=['--src ' + self.outputFolder + '/' + deviceFile,
                #                        '--tree ' + treePath + 'org-openroadm-device.tree'])
                return True
        except FileNotFoundError:
            print('Tree File does not exist..Generate the tree first')
        return False

    def createNtsNGDockerCompose(self, rName, ip, portNum):
        # sdnr is an external container on oam-network, so depends_on cannot be
        # used (Docker Compose validation fails on undefined services). Instead,
        # an inline command waits for sdnr's health endpoint to respond before
        # starting supervisord (the image's default cmd).
        waitForSdnr = (
            '/bin/sh -c "until wget -q -O /dev/null http://sdnr:8181/ready 2>/dev/null; '
            'do echo \'Waiting for sdnr...\'; sleep 5; done; echo \'sdnr is ready\'; '
            'exec /usr/bin/supervisord -c /etc/supervisor/conf.d/supervisord.conf"'
        )
        devBody = dict(
            image='${PYNTS_IMAGE}',
            container_name=rName,
            hostname=rName,
            environment={
                'NETCONF_USERNAME': '${SIM_NETCONF_USERNAME}',
                'NETCONF_PASSWORD': '${SIM_NETCONF_PASSWORD}',
                'SDNR_RESTCONF_URL': 'http://sdnr:8181',
                'SDNR_USERNAME': '${SDNR_USERNAME}',
                'SDNR_PASSWORD': '${SDNR_PASSWORD}'
            },
            volumes=[
                     './conf-generated/' + rName + '-operational.xml:/data/org-openroadm-device-operational.xml',
                     './conf-generated/' + rName + '-running.xml:/data/org-openroadm-device-running.xml'],
            command=waitForSdnr,
            networks=[NETWORK_NAME]

        )
        return devBody

parser = argparse.ArgumentParser(description='Generate NTS device models and docker-compose files for TransportPCE integration.')

parser.add_argument('--nodes', required=True, help='filename of the topology nodes file')
parser.add_argument('--links', required=True, help='filename of the topology links file')
parser.add_argument('--template-folder', required=False, default=CURRENT_PATH +'/demo-standalone/conf_v7.1', help='folder containing the device template files')
parser.add_argument('--output-profile', required=True, help='filename to put the integration test profile in')
parser.add_argument('--output-folder', required=False, default=CURRENT_PATH+'/demo-standalone/conf-generated', help='folder to put the generated xml models in')
parser.add_argument('--output-dc', required=False, default=CURRENT_PATH+'/demo-standalone/docker-compose-generated.yml', help='path to the generated docker-compose file')
parser.add_argument('--yang-path', required=False, default=None, help='Path to YANG files directory (default: <basePath>/integration/yang)')
args = parser.parse_args()

creator = NTSDeviceModelCreator(
        nodesSourceFile=args.nodes,
        linksSourceFile=args.links,
        templateFolder=args.template_folder,
        outputProfile=args.output_profile,
        outputFolder=args.output_folder,
        outputDockerComposeFile=args.output_dc,
        yangPath=args.yang_path)
creator.run()
