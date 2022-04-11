
import networkx as nx
import json
import os
from lib.xmlParser import OpenRoadmXmlParser
from lib.ntsngDeployGenerator import OpenroamdNtsNgDeployGenerator

TRPCEPATH=os.path.abspath(os.path.dirname(os.path.realpath(__file__))+'/../../')

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


topo=create_topology(nodes,edges)
i=1
xmlParser = OpenRoadmXmlParser(TRPCEPATH, outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
deployGen = OpenroamdNtsNgDeployGenerator(TRPCEPATH,outputPath=TRPCEPATH+'/integration/demo-standalone/conf-generated')
for node in topo.nodes():
    node_edges= topo.edges(node)

    degree= topo.degree(node)
    rdm_resp=xmlParser.create_data_models(str(node), degree, 'roadm', i)
    print(rdm_resp)
    xpdr_resp=xmlParser.create_xpdr_data_models(str(node), 'xpdr', i)
    print(xpdr_resp)
    print("Node {} has {} degrees and links {}".format(str(node), degree, node_edges))
    i = i + 1


#deployGen.createArchive('ROADM-Berlin.zip','integration/demo-standalone/conf-generated/ROADM-Berlin.xml')