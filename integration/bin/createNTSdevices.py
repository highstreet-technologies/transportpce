#!/usr/bin/python3

import os
import sys
import json
import argparse
import time
import yaml
from typing import List
from constants import *
import networkx as nx
from lib.xmlParser import OpenRoadmXmlParser
from lib.ntsngDeployGenerator import OpenroamdNtsNgDeployGenerator
from lib.inttestProfiles import IntegrationTestSimProfile, IntegrationTestControllerProfile
import subprocess


class NTSDeviceModelCreator:

    def __init__(self, nodesSourceFile=TRPCEPATH+"/integration/topology-info/Nodes_Germany_17.json",
        linksSourceFile=TRPCEPATH+"/integration/topology-info/Links_Germany_17.json",
        outputProfile='germany-17',
        outputFolder=TRPCEPATH+'/integration/demo-standalone/conf-generated',
        outputDockerComposeFile=CURRENT_PATH+'/docker-compose-generated.yml',
        sim='',
        outputDockerComposeFileNg=CURRENT_PATH + '/docker-compose-generated-ng.yml') -> None:
        self.linksSourceFile = linksSourceFile
        self.nodesSourceFile = nodesSourceFile
        self.outputProfile = outputProfile
        self.outputFolder = outputFolder
        self.outputDockerComposeFile = outputDockerComposeFile
        self.edges=None
        self.nodes=None
        self.sim=sim
        self.outputDockerComposeFileNg = outputDockerComposeFileNg

    def run(self):
        # Variable to be provided w.r.t to different  environments
        ip = '192.168.178.27'
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
        controllerProfile = IntegrationTestControllerProfile()
        COMPOSITION = {'version': '3', 'services': {}}
        COMPOSITION['services']['transportpce'] = self.createComposeData()

        xmlParser = OpenRoadmXmlParser(TRPCEPATH, outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
        deployGen = OpenroamdNtsNgDeployGenerator(TRPCEPATH,outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
        for node in topo.nodes():
            interfaceDict = {}
            nodeEdges = topo.edges(node)
            degree = topo.degree(node)
            neighbours = list(topo.neighbors(node))
            remotePortsList = self.findRemotePort(topo, node)
            nodeEdges= topo.edges(node)

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
        deviceList = self.findFiles(TRPCEPATH + '/integration/demo-standalone/conf-generated', 'xml')
        deviceList=[i for i in deviceList if 'operational' not in i and 'running' not in i]
        deviceList.sort()


        for device in deviceList:
            devName = device.split(".")[0]
            devBody = self.createNtsForDockerCompose(devName, ip, portNumber)
            COMPOSITION['services'][devName.replace('-', '').lower()] = devBody

            # Create the json data for profile.json file for the Germany-17 node network
            simProfile.addLocalContainerSim(nodeId=devName,port=830, username='netconf', password='netconf',containerName=devName.replace('-', '').lower())
            portNumber = portNumber + 1
        # time.sleep(0.01)

        with open(self.outputDockerComposeFile, 'w') as outfile:
            yaml.dump(COMPOSITION, outfile, default_flow_style=False, sort_keys=False, indent=4)
        outfile.close()
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
        if self.sim == "nts-ng":
            portNumber = 51000
            simProfile.clear()
            try:
                treeFile = self.findFiles(TRPCEPATH + '/integration/demo-standalone/conf', '-device.tree')
                if len(treeFile) > 0:
                    for device in deviceList:
                        print(deviceList)
                        cliCmd="../bin/splitRoadmModel.py --src conf-generated/"+ device +" --tree conf/org-openroadm-device.tree"
                        subprocess.call(cliCmd, shell=True)
                        devName = device.split(".")[0]
                        devBody = self.createNtsNGDockerCompose(devName, ip, portNumber)
                        COMPOSITION['services'][devName.replace('-', '').lower()] = devBody
                        # Create the json data for profile.json file for the Germany-17 node network
                        simProfile.addLocalContainerSim(nodeId=devName, port=830, username='netconf',
                                                        password='netconf!',
                                                        containerName=devName.replace('-', '').lower())
                        portNumber=portNumber+1
                with open(self.outputDockerComposeFileNg, 'w') as outfile:
                    yaml.dump(COMPOSITION, outfile, default_flow_style=False, sort_keys=False, indent=4)
                outfile.close()
                # Create profile.json file for the Germany-17 node network
                simProfile.save(PROFILES_SIM_FOLDER + '/' + self.outputProfile + '-ng.json')
                with open(PROFILES_CONTROLLER_FOLDER + '/' + self.outputProfile + '-ng.json', 'w') as f:
                    f.write(profileControl)
                f.close()
            except FileNotFoundError:
                print('File does not exist..Generate the tree first')



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
        textFiles = [f for f in os.listdir(path) if f.endswith(name)]
        return textFiles


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
        volumeService = [TRPCEPATH+"/integration/demo-standalone/conf/org.ops4j.pax.logging.cfg:/opt/opendaylight/etc/org.ops4j.pax.logging.cfg"]
        portService = ["18181:8181"]
        transportpce = dict(
            image='odl/transportpce',
            container_name='transportpce',
            environment=envVarService,
            volumes=volumeService,
            ports=portService

        )

        return transportpce


    def createNtsForDockerCompose(self, rname, ip, portNum):
        devBody = dict(
            image='${NTSIM_ROADM_STANDALONE_IMAGE}',
            container_name=rname.replace('-', '').lower(),
            environment=['NTS_IP=' + ip,
                        'EXTERNAL_NTS_IP=' + ip,
                        'NETCONF_BASE=${SIMPORT}',
                        'SCRIPTS_DIR=/opt/dev/scripts',
                        'K8S_DEPLOYMENT=false',
                        'IPv6Enabled=false'],
            volumes=['./conf/ntsim_configuration.json:/opt/dev/scripts/configuration.json',
                    './conf-generated/' + rname + '.xml:/opt/dev/scripts/startup-load.xml'],
            ports=[str(portNum) + ':${SIMPORT}']
        )
        return devBody



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
        devBody = dict(
            image='${NTSNG_IMAGE}',
            container_name=rName.replace('-', '').lower(),
            environment=['NTS_NF_STANDALONE_START_FEATURES=datastore-populate',
                         'NTS_NF_MOUNT_POINT_ADDRESSING_METHOD=host-mapping',
                         'NTS_HOST_IP=' + ip,
                         'HOSTNAME=' + rName.replace('-', '').lower(),
                         'IPv6Enabled=false',
                         'SSH_CONNECTIONS=1',
                         'TLS_CONNECTIONS=0',
                         'NTS_HOST_NETCONF_SSH_BASE_PORT=${SIMPORT}',
                         'NTS_HOST_NETCONF_TLS_BASE_PORT=65500'],
            volumes=['./conf/ntsng.config.json:/opt/dev/ntsim-ng/config/config.json',
                     './conf-generated/' + rName + '-operational.xml:/opt/dev/deploy/data/org-openroadm-device-operational.xml',
                     './conf-generated/' + rName + '-running.xml:/opt/dev/deploy/data/org-openroadm-device-running.xml'],
            ports=[str(portNum) + ':${SIMPORT}']
        )
        return devBody

parser = argparse.ArgumentParser(description='Process some integers.')

parser.add_argument('--nodes', required=True, help='filename of the topology nodes file')
parser.add_argument('--links', required=True, help='filename of the topology links file')
parser.add_argument('--output-profile', required=True, help='filename to put the integration test profile in')
parser.add_argument('--output-folder', required=False, default=CURRENT_PATH+'/conf-generated', help='folder to put the generated xml modesl in')
parser.add_argument('--output-dc', required=False, default=CURRENT_PATH+'/docker-compose-generated.yml', help='folder to put the generated xml modesl in')
parser.add_argument('--sim', required=False, default='nts', help='support for nts-ng sims')
args = parser.parse_args()

creator = NTSDeviceModelCreator(args.nodes, args.links, 
    args.output_profile, args.output_folder, args.output_dc, args.sim)
creator.run()
