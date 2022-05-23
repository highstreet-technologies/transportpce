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



class NTSDeviceModelCreator:

    def __init__(self, nodesSourceFile=TRPCEPATH+"/integration/topology-info/Nodes_Germany_17.json",
        linksSourceFile=TRPCEPATH+"/integration/topology-info/Links_Germany_17.json",
        outputProfile='germany-17',
        outputFolder=TRPCEPATH+'/integration/demo-standalone/conf-generated',
        outputDockerComposeFile=CURRENT_PATH+'/docker-compose-generated.yml') -> None:
        self.linksSourceFile = linksSourceFile
        self.nodesSourceFile = nodesSourceFile
        self.outputProfile = outputProfile
        self.outputFolder = outputFolder
        self.outputDockerComposeFile = outputDockerComposeFile
        self.edges=None
        self.nodes=None

    def run(self):
        # Variable to be provided w.r.t to different  environments
        ip = '192.168.178.27'
        with open(self.nodesSourceFile) as node_file:
            self.nodes = json.load(node_file)
        with open(self.linksSourceFile) as edge_file:
            self.edges = json.load(edge_file)

        topo=self.create_topology()
        i=1
        xpdr_num = 1
        port_number = 50001
        rdmConfiguration = {}
        simProfile = IntegrationTestSimProfile()
        controllerProfile = IntegrationTestControllerProfile()
        COMPOSITION = {'version': '3', 'services': {}}
        COMPOSITION['services']['transportpce'] = self.create_compose_data()

        xmlParser = OpenRoadmXmlParser(TRPCEPATH, outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
        deployGen = OpenroamdNtsNgDeployGenerator(TRPCEPATH,outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
        for node in topo.nodes():
            interface_dict = {}
            node_edges = topo.edges(node)
            degree = topo.degree(node)
            neighbours = list(topo.neighbors(node))
            remote_ports_list = self.find_remote_port(topo, node)
            node_edges= topo.edges(node)

            degree= topo.degree(node)
            rdm_resp=xmlParser.create_data_models(str(node), degree, 'roadm', i,neighbours,remote_ports_list)
            time.sleep(0.01)
            print(rdm_resp)
            for i in range(0, len(neighbours)):
                xpdr_resp = xmlParser.create_xpdr_data_models(str(node[:4]) + str(neighbours[i][:4]), 'xpdr', xpdr_num)
                xpdr_num = xpdr_num + 1
                print(xpdr_resp)
                interface_dict[i + 1] = str(neighbours[i])
                time.sleep(0.01)
            print("Node {} has {} degrees and links {}".format(str(node), degree, node_edges))
            rdmConfiguration[str(node)] = interface_dict
            i = i + 1
            print(rdmConfiguration)
            print(len(rdmConfiguration))
            #remote_ports_list.clear()
            #interface_dict.clear()
            #neighbours.clear()
        device_list = self.find_files(TRPCEPATH+'/integration/demo-standalone/conf-generated', 'xml')
        device_list.sort()

        for device in device_list:
            dev_name = device.split(".")[0]
            dev_body = self.create_nts_for_docker_compose(dev_name, ip, port_number)
            COMPOSITION['services'][dev_name.replace('-', '').lower()] = dev_body

            # Create the json data for profile.json file for the Germany-17 node network
            simProfile.addLocalContainerSim(nodeId=dev_name,port=830, username='netconf', password='netconf',containerName=dev_name.replace('-', '').lower())
            port_number = port_number + 1
        # time.sleep(0.01)

        with open(self.outputDockerComposeFile, 'w') as outfile:
            yaml.dump(COMPOSITION, outfile, default_flow_style=False, sort_keys=False, indent=4)
        outfile.close()
        # print(json.dumps(profile_data, indent=4))
        # Create profile.json file for the Germany-17 node network
        simProfile.save(PROFILES_SIM_FOLDER+'/'+self.outputProfile+'.json')

        profile_control = json.dumps(self.create_profile_controller(), indent=4)
        with open(PROFILES_CONTROLLER_FOLDER+'/'+self.outputProfile+'.json', 'w') as f:
            f.write(profile_control)
        f.close()
        rdmConfig= json.dumps(rdmConfiguration, indent=4)
        with open(PROFILES_SIM_FOLDER+'/rdmConfiguration.json', 'w') as f:
            f.write(rdmConfig)
        f.close()

    def create_topology(self):
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


    def find_files(self, path, name):
        text_files = [f for f in os.listdir(path) if f.endswith(name)]
        return text_files


    def find_remote_port(self, topology, actualNode):
        remote_ports_list = []
        ngbrs = list(topology.neighbors(actualNode))
        # print("ngbrs are {}".format(ngbrs))
        for n in ngbrs:
            remote_ngbrs = list(topology.neighbors(n))
            remote_port = remote_ngbrs.index(actualNode)
            remote_ports_list.append(remote_port + 1)
        return remote_ports_list


    def create_compose_data(self):
        env_var_service = ['REMOTE_SDNRURL=${REMOTE_SDNR_URL}', 'REMOTE_WSURL=${REMOTE_SDNR_WSURL}',
                        'REMOTE_ODL_USERNAME=${SDNR_USERNAME}', 'REMOTE_ODL_PASSWORD=${SDNR_PASSWORD}',
                        'REMOTE_ODL_ENABLED=${REMOTE_ODL_ENABLED}', 'REMOTE_ODL_TRUSTALL=${TRUSTALL}',
                        'TRANSPORTPCE_SIMULATOR_MODE=true']
        volume_service = [TRPCEPATH+"/integration/demo-standalone/conf/org.ops4j.pax.logging.cfg:/opt/opendaylight/etc/org.ops4j.pax.logging.cfg"]
        ports_service = ["18181:8181"]
        transportpce = dict(
            image='odl/transportpce',
            container_name='transportpce',
            environment=env_var_service,
            volumes=volume_service,
            ports=ports_service

        )

        return transportpce


    def create_nts_for_docker_compose(self, rname, ip, port_num):
        port_map = {}
        port_map[port_num] = '${SIMPORT}'
        dev_body = dict(
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
            ports=[str(port_num) + ':${SIMPORT}']
        )
        return dev_body



    def create_profile_controller(self):
        profile_content_controller = {
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
        return profile_content_controller
    

parser = argparse.ArgumentParser(description='Process some integers.')

parser.add_argument('--nodes', required=True, help='filename of the topology nodes file')
parser.add_argument('--links', required=True, help='filename of the topology links file')
parser.add_argument('--output-profile', required=True, help='filename to put the integration test profile in')
parser.add_argument('--output-folder', required=False, default=CURRENT_PATH+'/conf-generated', help='folder to put the generated xml modesl in')
parser.add_argument('--output-dc', required=False, default=CURRENT_PATH+'/docker-compose-generated.yml', help='folder to put the generated xml modesl in')

args = parser.parse_args()

creator = NTSDeviceModelCreator(args.nodes, args.links, 
    args.output_profile, args.output_folder, args.output_dc)
creator.run()
