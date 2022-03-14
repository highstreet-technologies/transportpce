import xml.etree.ElementTree as ET
import copy

def create_info(roadm, ns,deg):
    info=roadm.find('device:info', ns)
    node_id= info.find('device:node-id',ns)
    print(node_id)
    node_id.text='ROADM-TEST'
    degree = info.find('device:max-degrees',ns)
    degree.text=deg
    srg=info.find('device:max-srgs',ns)
    srg.text=deg
    ipAddress = info.find('device:ipAddress', ns)
    ipAddress.text='127.0.0.19'
    current_ipAddress=info.find('device:current-ipAddress',ns)
    current_ipAddress.text ='127.0.0.19'
    return roadm


def create_degree(sel, ns,deg):
    degree_list=[]
    for i in range(1, deg+1):
        d = copy.deepcopy(sel)
        degree_no = d.find('device:degree-number', ns)
        degree_no.text = str(i)
        print(degree_no.text)
        circuit_packs = d.findall('.//device:circuit-pack-name', ns)
        for cp in circuit_packs:
            #  print(cp.text)
            cp.text = cp.text.replace('1/0', str(i) + '/0')
            print(cp.text)
        degree_list.append(d)
    return degree_list


def create_srgs(srg,ns, srg_no, cp_list):
    srg_list=[]
    srg_cp_name_list=[]
    for i in range(1,srg_no + 1):
        s = copy.deepcopy(srg)
        srg_num= s.find('device:srg-number', ns)
        srg_num.text=str(i)
        srg_cp_name=s.find('.//device:circuit-pack-name', ns)
        srg_cp_name.text=str(int(cp_list[len(cp_list)-1][0])+i) + '/0'
        #print(srg_cp_name.text)
        srg_cp_name_list.append(srg_cp_name.text)
        srg_list.append(s)
    return srg_list,srg_cp_name_list



def cp_copies(roadm,ns):
    cp_body_list = roadm.findall('device:circuit-packs', ns)
    for ckt in cp_body_list:
        # print(ckt.find('device:circuit-pack-name', ns).text)
        if ckt.find('device:circuit-pack-name', ns).text == '2/0':
            parent_cp = copy.deepcopy(ckt)
        elif ckt.find('device:circuit-pack-name', ns).text == '1/0/ETH-PLUG':
            eth_cp = copy.deepcopy(ckt)
        elif ckt.find('device:circuit-pack-name', ns).text == '1/0/OSC-PLUG':
            osc_cp = copy.deepcopy(ckt)
        elif ckt.find('device:circuit-pack-name', ns).text == '5/0':
            srg_cp = copy.deepcopy(ckt)
    roadm=del_circuit_packs(roadm,ns)
    return roadm, parent_cp, eth_cp, osc_cp, srg_cp

def create_circuit_pack(cp_list,eth_cp,osc_cp, parent_cp, ns):
    cp_list_body=[]
    for c in cp_list:
        if 'ETH-PLUG' in c:
            eth_cp_modify = copy.deepcopy(eth_cp)
            cp_name = eth_cp_modify.find('device:circuit-pack-name', ns)
            cp_name.text = c
            # print(cp_name.text)
            parent_cp_xml = eth_cp_modify.find('device:parent-circuit-pack', ns)
            p_cp_name = parent_cp_xml.find('device:circuit-pack-name', ns)
            p_cp_name.text = c[0] + '/0'
            cp_list_body.append(eth_cp_modify)
        else:
            parent_cp_modify = copy.deepcopy(parent_cp)
            cp_name = parent_cp_modify.find('device:circuit-pack-name', ns)
            cp_name.text = c[0] + '/0'
            cp_slots = parent_cp_modify.findall('device:cp-slots', ns)
            for slot in cp_slots:
                # print(slot.tag)
                pcp_name = slot.find('device:provisioned-circuit-pack', ns)
                print(pcp_name.text)
                pcp_name.text = pcp_name.text[:0] + c[0] + pcp_name.text[1:]
            cp_ports = parent_cp_modify.findall('device:ports', ns)
            for ports in cp_ports:
                lcp_name = ports.find('device:logical-connection-point', ns)
                lcp_name.text = lcp_name.text[:3] + c[0] + lcp_name.text[4:]
                if (ports.find('device:label', ns)) is not None:
                    port_label = ports.find('device:label', ns)
                    port_label.text = port_label.text[:3] + c[0] + port_label.text[4:]
                    print(port_label.text)
                print(lcp_name.text)

            cp_list_body.append(parent_cp_modify)
            osc_cp_modify = copy.deepcopy(osc_cp)
            cp_name = osc_cp_modify.find('device:circuit-pack-name', ns)
            cp_name.text = cp_name.text[:0] + c[0] + cp_name.text[1:]
            osc_parent = osc_cp_modify.find('device:parent-circuit-pack', ns)
            p_name = osc_parent.find('device:circuit-pack-name', ns)
            p_name.text = p_name.text[:0] + c[0] + p_name.text[1:]
            cp_list_body.append(osc_cp_modify)
    return cp_list_body
# create copies of SRG ports of type AD-DEG
def create_srg_deg_ports(srg_ports, num, ns):
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
# Create new list of SRG circuit-packs
def create_srg_cp(srg_list, srg_cp_body, ns):
    srg_cp_body_list=[]
    for n in srg_list:
        srg_cp_modify=copy.deepcopy(srg_cp_body)
        cp_name=srg_cp_modify.find('device:circuit-pack-name',ns)
        cp_name.text=n
        srg_ports= srg_cp_modify.findall('device:ports', ns)
        for p in srg_ports:
            slcp_name=p.find('device:logical-connection-point', ns)
            slcp_name.text=slcp_name.text[:3] + str(srg_list.index(n)+1) + slcp_name.text[4:]
        srg_cp_body_list.append(srg_cp_modify)
    return srg_cp_body_list

# Remove Circuit-packs that creates conflicts with newly created circuit-packs
def del_circuit_packs(roadm, ns):
    cps= roadm.findall('device:circuit-packs', ns)
    for cp in cps:
        #if cp.find('device:circuit-pack-name', ns).text == '1/0' or cp.find('device:circuit-pack-name', ns).text == '3/0' or cp.find('device:circuit-pack-name', ns).text == '5/0' :
        roadm.remove(cp)
    return roadm   

def edit_tags(filename):
    fin = open(filename, "rt")
    #read file contents to string
    data = fin.read()
    #replace all occurrences of the required string
    data = data.replace('<supported-interface-capability>', '<supported-interface-capability xmlns:x="http://org/openroadm/port/types">')
    data=data.replace('<type>openROADM-if:ethernetCsmacd','<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:ethernetCsmacd')
    data=data.replace('<type>openROADM-if:opticalTransport','<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:opticalTransport')
    data=data.replace('<type>openROADM-if:openROADMOpticalMultiplex','<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:openROADMOpticalMultiplex')
    #close the input file
    fin.close()
    #open the input file in write mode
    fin = open(filename, "wt")
    #overrite the input file with the resulting data
    fin.write(data)
    #close the file
    fin.close()
def main():
    #register_all_namespaces('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml')
    ET.register_namespace('', 'http://org/openroadm/device')
    ET.register_namespace('interfaces','http://org/openroadm/ethernet-interfaces')
    ET.register_namespace('otinterfaces','http://org/openroadm/optical-transport-interfaces')
    with open("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-ROADMA.xml", "r") as f:
        xmltest=f.read()
        f.close
    roadm_root=ET.fromstring(xmltest)  
    deg_number=4
    #print(roadm_root.tag)
    ns ={'device':'http://org/openroadm/device'}
    cp_name_list=[]
    roadm_root=create_info(roadm_root, ns, str(deg_number))
    # for elem in roadm_root.findall('device:info',ns):
    #     roadm_root.remove(elem)
    # roadm_root.append(roadm_info)

    sel=roadm_root.find('device:degree', ns)
    degrees=create_degree(sel,ns,deg_number)

    print(degrees)
    for elem in roadm_root.findall('device:degree',ns):
        roadm_root.remove(elem)
    for d in degrees:
        roadm_root.append(d)
        circuit_packs = d.findall('.//device:circuit-pack-name', ns)
        for c in circuit_packs:
            # print(c.text)
            cp_name_list.append(c.text)
    cp_name_list = list(dict.fromkeys(cp_name_list))
   
    print(cp_name_list)

    # Create a deep copy of the 4 circuit-packs namely parent, eth, osc and srg
    roadm_root,parent_cp, eth_cp, osc_cp, srg_cp= cp_copies(roadm_root, ns)
    cp_list=create_circuit_pack(cp_name_list,eth_cp,osc_cp, parent_cp, ns)
    for elem in cp_list:
        roadm_root.append(elem)
    
    
    # Create the SRG bodies
    srg=roadm_root.find('device:shared-risk-group', ns)
    srgs, srg_cp_name_list=create_srgs(srg, ns, deg_number, cp_name_list)
    for elem in roadm_root.findall('device:shared-risk-group',ns):
        roadm_root.remove(elem)
    for s in srgs:
        roadm_root.append(s)
    
    # create srg cp and append then to root
    srgs=roadm_root.find('device:shared-risk-group', ns)
    # First append the AD-DEG ports to the SRG circuit-pack
    # TODO: IF conditionn should be implemented later on
    srg_ports=srg_cp.findall('device:ports', ns)
    deg_port= create_srg_deg_ports(srg_ports,deg_number, ns)
    for d in deg_port:
        srg_cp.append(d)
    srg_cp_body_list= create_srg_cp(srg_list=srg_cp_name_list, srg_cp_body=srg_cp, ns=ns)
    for s in srg_cp_body_list:
        roadm_root.append(s)
    tree1= ET.ElementTree(roadm_root)
    tree1.write('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/ROADMs/ROADM-TEST.xml',  encoding="utf-8", xml_declaration=True)
    edit_tags("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/ROADMs/ROADM-TEST.xml")

if __name__ == "__main__":
    main()

