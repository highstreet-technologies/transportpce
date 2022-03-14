
import networkx as nx
import json
import XMLParser 

with open("../topology-info/Nodes_Germany_17.json") as node_file:
    nodes = json.load(node_file)
with open("../topology-info/Links_Germany_17.json") as edge_file:
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

