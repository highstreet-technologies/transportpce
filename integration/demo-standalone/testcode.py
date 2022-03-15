import xml.etree.ElementTree as ET
from xml.dom.minidom import parse, Node
import xml.dom.minidom
import copy
from lxml import etree
from xml.parsers import expat
# DOMTree = xml.dom.minidom.parse("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml")
# collection = DOMTree.documentElement
# info= collection.getElementsByTagName("info").item(0)
#
# print ("childNodes : " , DOMTree.documentElement.childNodes)
# node_id=info.getElementsByTagName('node-id').item(0)
# node_id.firstChild.data ='ROAdM-TEST'
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
def register_all_namespaces(filename):
    namespaces = dict([node for _, node in ET.iterparse(filename, events=['start-ns'])])
    for ns in namespaces:
        ET.register_namespace(ns, namespaces[ns])
        print('ns {}'.format(ns))
        print('namespaces {}'.format(namespaces[ns]))
#DOMTree.writexml('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/ROADMs/output.xml','wb')

#register_all_namespaces('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml')
ET.register_namespace('', 'http://org/openroadm/device')
ET.register_namespace('interfaces','http://org/openroadm/ethernet-interfaces')
ET.register_namespace('otinterfaces','http://org/openroadm/optical-transport-interfaces')
#ET.register_namespace('x','http://org/openroadm/port/types')
#roadm_tree = ET.parse('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml')
""" roadm_tree = parse_xmlns('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml')

roadm_root = roadm_tree.getroot() """
with open("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml", "r") as f:
    xmltest=f.read()
    f.close
roadm_root=ET.fromstring(xmltest)    
#print(roadm_root.tag)
ns ={'device':'http://org/openroadm/device',
     'x':'http://org/openroadm/port/types'}
custom_namespace = ('xmlns:x', 'http://org/openroadm/port/types')

# for child in roadm_root:
#     print(child.tag, child.attrib)
# class Device_ROADM():
#     def __init__(self):
#         self.xmltree = roadm_tree
#         self.root = roadm_root
#         self.ns = {'device': 'http://org/openroadm/device'}
#         self.node_info=self.root.findall('device:info',ns)
#         self.degreelist=self.root.findall('device:degree', ns)
#         self.degree_info = self.root.find('device:degree', ns)
#         self.degree_no = self.degree_info.find('device:degree-number', ns)
#         self.parent_cp = self.degree_info.findall('device:circuit-packs', ns)
#
#     def modify_info(self, degree):
#         x=1

def create_info(alist, ns,deg):
    node_id= alist.find('device:node-id',ns)
    node_id.text='ROADM-TEST'
    degree = alist.find('device:max-degrees',ns)
    degree.text=deg
    srg=alist.find('device:max-srgs',ns)
    srg.text=deg
    ipAddress = alist.find('device:ipAddress', ns)
    ipAddress.text='127.0.0.19'
    current_ipAddress=alist.find('device:current-ipAddress',ns)
    current_ipAddress.text ='127.0.0.19'
    return alist


def create_degree(deg_info, ns,deg):
    degree_info=deg_info
    degree_no=degree_info.find('device:degree-number', ns)
    degree_no.text=deg
    circuit_packs=degree_info.findall('.//device:circuit-pack-name', ns)
    for cp in circuit_packs:
      #  print(cp.text)
        cp.text=cp.text.replace('1/0', deg + '/0')
       # print(cp.text)

    # print('degree_no {}'.format(degree_no.text))
    return degree_info

#
# node_info = create_info(roadm.node_info[0],ns,'3')
# degrees= []
# deg_info=roadm.degree_info
    # print('After {}'.format(degree.find('device:degree-number', ns).text))


deg=[]
srg=[]
cp_name_list=[]
cp_list_body=[]
cp_body_list=[]
parent_cp_list=[]
eth_cp_list=[]
osc_cp_list=[]
srg_list=[]
srg_cp_body_list=[]
interface_list=[]
sel=roadm_root.find('device:degree', ns)
# create degrees
for i in range(1,5):
    d=copy.deepcopy(sel)
    degree_no = d.find('device:degree-number', ns)
    degree_no.text = str(i)
   # print( degree_no.text)
    circuit_packs = d.findall('.//device:circuit-pack-name', ns)
    for cp in circuit_packs:
        #  print(cp.text)
        cp.text = cp.text.replace('1/0', str(i) + '/0')
        #print(cp.text)
    deg.append(d)
    #roadm.degree_info.append(d)


# create cp bodies
for d in deg:

    #print(d)
    #print(d.find('device:degree-number', ns).text)
    degree_no = d.find('device:degree-number', ns)

    #print('degree_no {}'.format(degree_no))
    circuit_packs = d.findall('.//device:circuit-pack-name', ns)
    for c in circuit_packs:
        #print(c.text)
        cp_name_list.append(c.text)
cp_list= list( dict.fromkeys(cp_name_list) )
del(cp_list[:4])
#print(cp_list)

cp_body_list=roadm_root.findall('device:circuit-packs', ns)
for ckt in cp_body_list:
   # print(ckt.find('device:circuit-pack-name', ns).text)
    if ckt.find('device:circuit-pack-name', ns).text=='2/0':
        parent_cp =copy.deepcopy(ckt)
    elif ckt.find('device:circuit-pack-name', ns).text=='1/0/ETH-PLUG':
        eth_cp = copy.deepcopy(ckt)
    elif ckt.find('device:circuit-pack-name', ns).text=='1/0/OSC-PLUG':
        osc_cp=copy.deepcopy(ckt)
    elif ckt.find('device:circuit-pack-name', ns).text=='5/0':
        srg_cp=copy.deepcopy(ckt)
        roadm_root.remove(ckt)
    elif ckt.find('device:circuit-pack-name', ns).text=='3/0':
        roadm_root.remove(ckt)
# create interface copies

for g in roadm_root.findall('device:interface', ns):
    if g.find('device:name', ns).text=='1GE-interface-1':
        ge_interface=copy.deepcopy(g)
    elif g.find('device:name', ns).text=='OTS-DEG2-TTP-TXRX':
        ots_interface=copy.deepcopy(g)
    elif g.find('device:name', ns).text=='OMS-DEG2-TTP-TXRX':
        oms_interface=copy.deepcopy(g)



# string = 'Python'
# position = 0
# new_character = 'X'
#
# string = string[:position] + new_character + string[position+1:]
# print(string)

for c in cp_list:
    if 'ETH-PLUG' in c:
        eth_cp_modify=copy.deepcopy(eth_cp)
        cp_name=eth_cp_modify.find('device:circuit-pack-name', ns)
        cp_name.text = c
        #print(cp_name.text)
        parent_cp_xml= eth_cp_modify.find('device:parent-circuit-pack',ns)
        p_cp_name=parent_cp_xml.find('device:circuit-pack-name',ns)
        p_cp_name.text= c[0]+'/0'
        eth_cp_list.append(eth_cp_modify)
        cp_list_body.append(eth_cp_modify)
    else:
        parent_cp_modify=copy.deepcopy(parent_cp)
        cp_name=parent_cp_modify.find('device:circuit-pack-name', ns)
        cp_name.text=c[0]+'/0'
        cp_slots= parent_cp_modify.findall('device:cp-slots',ns)
        for slot in cp_slots:
            #print(slot.tag)
            pcp_name=slot.find('device:provisioned-circuit-pack', ns)
            #print(pcp_name.text)
            pcp_name.text=pcp_name.text[:0] + c[0] + pcp_name.text[1:]
        cp_ports=parent_cp_modify.findall('device:ports', ns)
        for ports in cp_ports:
            lcp_name=ports.find('device:logical-connection-point', ns)
            lcp_name.text=lcp_name.text[:3] + c[0] + lcp_name.text[4:]
            if (ports.find('device:label', ns)) is not None:
                port_label=ports.find('device:label', ns)
                port_label.text=port_label.text[:3] + c[0] + port_label.text[4:]
                #print(port_label.text)

            if (ports.find('device:interfaces', ns)) is not None:
                interfaces=ports.findall('device:interfaces', ns)
                for ifc in interfaces:
                    ifc_name= ifc.find('device:interface-name', ns)
                    ifc_name.text=ifc_name.text[:7] + '3' +  ifc_name.text[8:]

                   # print(ifc_name.text)

        parent_cp_list.append(parent_cp_modify)
        cp_list_body.append(parent_cp_modify)
        # create the OSC circuit-packs
        osc_cp_modify = copy.deepcopy(osc_cp)
        cp_name = osc_cp_modify.find('device:circuit-pack-name', ns)
        cp_name.text=cp_name.text[:0] + c[0] + cp_name.text[1:]
        osc_parent= osc_cp_modify.find('device:parent-circuit-pack', ns)
        p_name=osc_parent.find('device:circuit-pack-name', ns)
        p_name.text=p_name.text[:0] + c[0] + p_name.text[1:]
        osc_cp_list.append(osc_cp_modify)
        cp_list_body.append(osc_cp_modify)

for elem in cp_list_body:
    #print(elem.find('device:circuit-pack-name', ns).text)
    roadm_root.append(elem)



       # print(cp_name.text)

    # for cp in circuit_packs:
    #     print('cp.text {}'.format(cp.find('.//device:circuit-pack-name', ns).text))

for elem in roadm_root.findall('device:degree',ns):
    roadm_root.remove(elem)
for d in deg:
    roadm_root.append(d)
    #roadm.root.append(deg)

def create_srg_deg_ports(srg_ports, num):
    ad_srg_ports=[]
    for p in srg_ports:
        srgp_name= p.find("device:port-name", ns)
        if "AD-DEG" in srgp_name.text:
            deg_port_body=copy.deepcopy(p)
            break
    for i in range(3, num+1):
        deg_port_body_modify=copy.deepcopy(deg_port_body)
        deg_port_name=deg_port_body_modify.find('device:port-name', ns)
        deg_port_name.text=deg_port_name.text[:6] + str(i)
        ad_srg_ports.append(deg_port_body_modify)
    return ad_srg_ports
    
# create srg
srgs=roadm_root.find('device:shared-risk-group', ns)
# First append the AD-DEG ports to the SRG circuit-pack
# TODO: IF conditionn should be implemented later on
srg_ports=srg_cp.findall('device:ports', ns)
deg_port= create_srg_deg_ports(srg_ports,5)
for d in deg_port:
    srg_cp.append(d)
for i in range(1,5):
    s = copy.deepcopy(srgs)
    srg_num= s.find('device:srg-number', ns)
    srg_num.text=str(i)
    srg_cp_name=s.find('.//device:circuit-pack-name', ns)
    srg_cp_name.text=str(int(cp_list[len(cp_list)-1][0])+i) + '/0'
    #print(srg_cp_name.text)
    srg_list.append(srg_cp_name.text)
    srg.append(s)
for n in srg_list:
    srg_cp_modify=copy.deepcopy(srg_cp)

    cp_name=srg_cp_modify.find('device:circuit-pack-name',ns)
    cp_name.text=n
    srg_ports= srg_cp_modify.findall('device:ports', ns)
    for p in srg_ports:
        slcp_name=p.find('device:logical-connection-point', ns)
        slcp_name.text=slcp_name.text[:3] + str(srg_list.index(n)+1) + slcp_name.text[4:]
    srg_cp_body_list.append(srg_cp_modify)


for elem in roadm_root.findall('device:shared-risk-group',ns):
    roadm_root.remove(elem)
for s in srg:
    roadm_root.append(s)

for srg in srg_cp_body_list:
    roadm_root.append(srg)


# create the interface bodies and append
for i in range(1,5):
    ge_interface_modify=copy.deepcopy(ge_interface)
    g_name=ge_interface_modify.find('device:name', ns)
    g_name.text=str(i) + g_name.text[1:-1] + str(i)
    sg_cp=ge_interface_modify.find('device:supporting-circuit-pack-name', ns)
    sg_cp.text=str(i) + sg_cp.text[1:]
    interface_list.append(ge_interface_modify)
    
    ots_interface_modify= copy.deepcopy(ots_interface)
    ots_name= ots_interface_modify.find('device:name', ns)
    ots_name.text=ots_name.text[:7] + str(i) + ots_name.text[8:]
    ots_scp=ots_interface_modify.find('device:supporting-circuit-pack-name', ns)
    ots_scp.text= str(i) + ots_scp.text[1:]
    interface_list.append(ots_interface_modify)

    oms_interface_modify=copy.deepcopy(oms_interface)
    oms_name=oms_interface_modify.find('device:name', ns)
    oms_name.text=oms_name.text[:7] + str(i) + oms_name.text[8:]
    oms_scp=oms_interface_modify.find('device:supporting-circuit-pack-name', ns)
    oms_scp.text= str(i) + oms_scp.text[1:]
    oms_sifc=oms_interface_modify.find('device:supporting-interface', ns)
    oms_sifc.text=ots_name.text
    interface_list.append(oms_interface_modify)

for i in roadm_root.findall('device:interface',ns):
    roadm_root.remove(i)

for i in interface_list:
    roadm_root.append(i)

info=roadm_root.find("device:info",ns)
node_id= info.find('device:node-id',ns)
node_id.text="ROADM-TEST"
print(node_id.text)
tree1= ET.ElementTree(roadm_root)
tree1.write('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/ROADMs/ROADM-TEST.xml',  encoding="utf-8", xml_declaration=True)

with open("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-XPDRA.xml", 'r') as x:
    xmlxpdr=x.read()
    x.close()
xpdr_root=ET.fromstring(xmlxpdr)
xpdr_info=xpdr_root.find('device:info', ns)
xpdr_id=xpdr_info.find('device:node-id', ns)
xpdr_id.text= xpdr_id.text[:5] + 'Berlin'
print(xpdr_id.text)