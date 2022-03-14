
from heapq import nsmallest
import xml.etree.ElementTree as ET

def parse_xmlns(file):

    events = "start", "start-ns"

    root = None
    ns_map = []

    for event, elem in ET.iterparse(file, events):

        if event == "start-ns":
            print(event)
            print(elem)
            ns_map.append(elem)


        elif event == "start":
            if root is None:
                root = elem
                #print('ns_map: {}'.format(ns_map))
            for prefix, uri in ns_map:
                #print("prefix{}".format(prefix))
                #print("uri {}".format(uri))
                elem.set("xmlns:" + prefix, uri)

            ns_map = []

    return ET.ElementTree(root)



 
tree = parse_xmlns('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml')


tree.write('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/ROADMs/output.xml',
           xml_declaration = True,
           encoding = 'utf-8',
           method = 'xml')