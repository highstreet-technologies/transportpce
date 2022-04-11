
import networkx as nx
import json
import os
from lib.xmlParser import OpenRoadmXmlParser
from lib.ntsngDeployGenerator import OpenroamdNtsNgDeployGenerator
import time
import yaml

TRPCEPATH=os.path.abspath(os.path.dirname(os.path.realpath(__file__))+'/../../')
# Variable to be provided w.r.t to different  environments
ip = '192.168.178.27'
with open(TRPCEPATH+"/integration/topology-info/Nodes_Germany_17.json") as node_file:
    nodes = json.load(node_file)
with open(TRPCEPATH+"/integration/topology-info/Links_Germany_17.json") as edge_file:
    edges = json.load(edge_file)

def create_topology(n,e):
    graph = nx.Graph()
    for n in nodes:
        graph.add_node(nodes[n][0], lon=nodes[n][1], lat=nodes[n][2], pos=(nodes[n][1], nodes[n][2]),
                            num_of_IXPs=nodes[n][3],
                            num_of_DCs=nodes[n][4])
    for e in edges:
        graph.add_edge(edges[e]['startNode'], edges[e]['endNode'], linkDist=round(edges[e]['linkDist'], 2),
                            noChannels=edges[e]['noChannels'], noSpans=edges[e]['noSpans'],
                            spanList=edges[e]['spanList'])
    return graph


def find_files(path, name):
    text_files = [f for f in os.listdir(path) if f.endswith(name)]
    return text_files


def find_remote_port(topology, actualNode):
    remote_ports_list = []
    ngbrs = list(topology.neighbors(actualNode))
    # print("ngbrs are {}".format(ngbrs))
    for n in ngbrs:
        remote_ngbrs = list(topology.neighbors(n))
        remote_port = remote_ngbrs.index(actualNode)
        remote_ports_list.append(remote_port + 1)
    return remote_ports_list


def create_compose_data():
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


def create_nts(rname, ip, port_num):
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
        volumes=[TRPCEPATH+'/integration/demo-standalone/conf/ntsim_configuration.json:/opt/dev/scripts/configuration.json',
                 TRPCEPATH+'/integration/demo-standalone/conf-generated/' + rname + '.xml:/opt/dev/scripts/startup-load.xml'],
        ports=[str(port_num) + ':${SIMPORT}']
    )
    return dev_body


def create_profile_sim(dev_name):
    profile_content_sim = dict([("node-id", dev_name),
                                ("host", ""),
                                ("port", 830),
                                ("username", "netconf"),
                                ("password", "netconf"),
                                ("container", dev_name.replace('-', '').lower())])

    return profile_content_sim


def create_profile_controller():
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
topo=create_topology(nodes,edges)
i=1
xpdr_num = 1
port_number = 50001
rdmConfiguration = {}
profile_data = []
COMPOSITION = {'version': '3', 'services': {}}
COMPOSITION['services']['transportpce'] = create_compose_data()

xmlParser = OpenRoadmXmlParser(TRPCEPATH, outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
deployGen = OpenroamdNtsNgDeployGenerator(TRPCEPATH,outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
for node in topo.nodes():
    interface_dict = {}
    node_edges = topo.edges(node)
    degree = topo.degree(node)
    neighbours = list(topo.neighbors(node))
    remote_ports_list = find_remote_port(topo, node)
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
device_list = find_files(TRPCEPATH+'/integration/demo-standalone/conf-generated', 'xml')
device_list.sort()

for device in device_list:
    dev_name = device.split(".")[0]
    dev_body = create_nts(dev_name, ip, port_number)
    COMPOSITION['services'][dev_name.replace('-', '').lower()] = dev_body

    # Create the json data for profile.json file for the Germany-17 node network
    profile_data.append(create_profile_sim(dev_name))
    port_number = port_number + 1
   # time.sleep(0.01)

with open(TRPCEPATH+'/integration/demo-standalone/docker-compose-germany17.yml', 'w') as outfile:
    yaml.dump(COMPOSITION, outfile, default_flow_style=False, sort_keys=False, indent=4)
outfile.close()
# print(json.dumps(profile_data, indent=4))
# Create profile.json file for the Germany-17 node network
with open(TRPCEPATH+'/integration/configs/sims/germany-17.json', 'w') as f:
    json.dump(profile_data, f, indent=4)
f.close()
profile_control = json.dumps(create_profile_controller(), indent=4)
with open(TRPCEPATH+'/integration/configs/controllers/germany-17.json', 'w') as f:
    f.write(profile_control)
f.close()
rdmConfig= json.dumps(rdmConfiguration, indent=4)
with open(TRPCEPATH+'/integration/configs/sims/rdmConfiguration.json', 'w') as f:
    f.write(rdmConfig)
f.close()

#deployGen.createArchive('ROADM-Berlin.zip','integration/demo-standalone/conf-generated/ROADM-Berlin.xml')
