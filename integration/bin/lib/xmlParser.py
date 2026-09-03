import xml.etree.ElementTree as ET
import copy
import os

class OpenRoadmXmlParser:

    def __init__(self, basePath: str, roadmTemplateFile: str, xpdrTemplateFile: str, outputPath: str) -> None:
        self.basePath = basePath.rstrip('/')
        self.roadmTemplateFile = roadmTemplateFile
        self.xpdrTemplateFile = xpdrTemplateFile
        self.outputPath = outputPath
        self.ns = {'device': 'http://org/openroadm/device',
              'lldp': 'http://org/openroadm/lldp'}
        if not os.path.isdir(self.outputPath):
            os.makedirs(self.outputPath)


    def create_info(self, device, ns:dict, device_name:str, deg:str, deviceType:str, node_num:int):
        info = device.find('device:info', ns)
        node_id = info.find('device:node-id', ns)
        node_number = info.find('device:node-number', ns)
        node_number.text = str(node_num)
        # print(node_id)
        if deviceType == 'roadm':
            node_id.text = 'ROADM-' + device_name
            degree = info.find('device:max-degrees', ns)
            degree.text = deg
            srg = info.find('device:max-srgs', ns)
            srg.text = deg
            ipAddress = info.find('device:ipAddress', ns)
            ipAddress.text = '127.0.0.' + str(node_num)
            current_ipAddress = info.find('device:current-ipAddress', ns)
            current_ipAddress.text = '127.0.0.' + str(node_num)
        else:
            node_id.text = 'XPDR-' + device_name
            ipAddress = info.find('device:ipAddress', ns)
            ipAddress.text = '1.2.3.' + str(node_num)
            current_ipAddress = info.find('device:current-ipAddress', ns)
            current_ipAddress.text = '1.2.3.' + str(node_num)
        clli = info.find('device:clli', ns)
        clli.text = 'Node' + device_name
        return device

    def create_degree(self,sel:ET.Element, ns:dict, deg:int):
        degree_list = []
        for i in range(1, deg + 1):
            d = copy.deepcopy(sel)
            degree_no = d.find('device:degree-number', ns)
            degree_no.text = str(i)
            # print(degree_no.text)
            circuit_packs = d.findall('.//device:circuit-pack-name', ns)
            for cp in circuit_packs:
                #  print(cp.text)
                cp.text = cp.text.replace('1/0', str(i) + '-0')
                cp.text = cp.text.replace('1-0', str(i) + '-0')
                # print(cp.text)
            degree_list.append(d)
        return degree_list

    def create_srgs(self, srg:ET.Element, ns:dict, srg_no:int, cp_list:list):
        srg_list = []
        srg_cp_name_list = []
        for i in range(1, srg_no + 1):
            s = copy.deepcopy(srg)
            srg_num = s.find('device:srg-number', ns)
            srg_num.text = str(i)
            srg_cp_name = s.find('.//device:circuit-pack-name', ns)
            srg_cp_name.text = str(int(cp_list[len(cp_list) - 1][0]) + i) + '-0'
            # print(srg_cp_name.text)
            srg_cp_name_list.append(srg_cp_name.text)
            srg_list.append(s)
        return srg_list, srg_cp_name_list

    def cp_copies(self, roadm, ns:dict):
        parent_cp, eth_cp, osc_cp, srg_cp = None, None, None, None

        cp_body_list = roadm.findall('device:circuit-packs', ns)
        for ckt in cp_body_list:
            # print(ckt.find('device:circuit-pack-name', ns).text)
            cp_name = ckt.find('device:circuit-pack-name', ns).text
            if cp_name in ('2/0', '2-0'):
                parent_cp = copy.deepcopy(ckt)
            elif cp_name in ('1/0/ETH-PLUG', '1-0-ETH-PLUG'):
                eth_cp = copy.deepcopy(ckt)
            elif cp_name in ('1/0/OSC-PLUG', '1-0-OSC-PLUG'):
                osc_cp = copy.deepcopy(ckt)
            elif cp_name in ('5/0', '5-0'):
                srg_cp = copy.deepcopy(ckt)

        roadm_root = self.del_circuit_packs(roadm, ns)
        return roadm_root, parent_cp, eth_cp, osc_cp, srg_cp

    def create_circuit_pack(self, cp_list:list, eth_cp:ET.Element, osc_cp:ET.Element, parent_cp:ET.Element, ns:dict):
        cp_list_body = []
        for c in cp_list:
            if 'ETH-PLUG' in c:
                eth_cp_modify = copy.deepcopy(eth_cp)
                cp_name = eth_cp_modify.find('device:circuit-pack-name', ns)
                cp_name.text = c
                # print(cp_name.text)
                parent_cp_xml = eth_cp_modify.find('device:parent-circuit-pack', ns)
                p_cp_name = parent_cp_xml.find('device:circuit-pack-name', ns)
                p_cp_name.text = c[0] + '-0'
                cp_list_body.append(eth_cp_modify)
            else:
                parent_cp_modify = copy.deepcopy(parent_cp)
                cp_name = parent_cp_modify.find('device:circuit-pack-name', ns)
                cp_name.text = c[0] + '-0'
                cp_slots = parent_cp_modify.findall('device:cp-slots', ns)
                for slot in cp_slots:
                    # print(slot.tag)
                    pcp_name = slot.find('device:provisioned-circuit-pack', ns)
                    # print(pcp_name.text)
                    pcp_name.text = pcp_name.text[:0] + c[0] + pcp_name.text[1:]
                cp_ports = parent_cp_modify.findall('device:ports', ns)
                for ports in cp_ports:
                    lcp_name = ports.find('device:logical-connection-point', ns)
                    lcp_name.text = lcp_name.text[:3] + c[0] + lcp_name.text[4:]
                    if (ports.find('device:label', ns)) is not None:
                        port_label = ports.find('device:label', ns)
                        port_label.text = port_label.text[:3] + c[0] + port_label.text[4:]
                        # print(port_label.text)
                    # print(lcp_name.text)

                    if (ports.find('device:interfaces', ns)) is not None:
                        interfaces = ports.findall('device:interfaces', ns)
                        for ifc in interfaces:
                            ifc_name = ifc.find('device:interface-name', ns)
                            ifc_name.text = ifc_name.text[:7] + c[0] + ifc_name.text[8:]

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
    def create_srg_deg_ports(self, srg_ports:ET.Element, num:int, ns:dict):
        ad_srg_ports = []
        for p in srg_ports:
            srgp_name = p.find("device:port-name", ns)
            if "AD-DEG" in srgp_name.text:
                deg_port_body = copy.deepcopy(p)
                break
        for i in range(3, num + 1):
            deg_port_body_modify = copy.deepcopy(deg_port_body)
            deg_port_name = deg_port_body_modify.find('device:port-name', ns)
            deg_port_name.text = deg_port_name.text[:6] + str(i)
            ad_srg_ports.append(deg_port_body_modify)
        return ad_srg_ports

    # Create new list of SRG circuit-packs
    def create_srg_cp(self, srg_list:list, srg_cp_body, ns:dict):
        srg_cp_body_list = []
        for n in srg_list:
            srg_cp_modify = copy.deepcopy(srg_cp_body)
            cp_name = srg_cp_modify.find('device:circuit-pack-name', ns)
            cp_name.text = n
            srg_ports = srg_cp_modify.findall('device:ports', ns)
            for p in srg_ports:
                slcp_name = p.find('device:logical-connection-point', ns)
                slcp_name.text = slcp_name.text[:3] + str(srg_list.index(n) + 1) + slcp_name.text[4:]
            srg_cp_body_list.append(srg_cp_modify)
        return srg_cp_body_list

    def create_interface_copies(self, roadm, ns:dict):
        for g in roadm.findall('device:interface', ns):
            if g.find('device:name', ns).text == '1GE-interface-1':
                ge_interface = copy.deepcopy(g)
            elif g.find('device:name', ns).text == 'OTS-DEG1-TTP-TXRX':
                ots_interface = copy.deepcopy(g)
            elif g.find('device:name', ns).text == 'OMS-DEG1-TTP-TXRX':
                oms_interface = copy.deepcopy(g)
        return ge_interface, ots_interface, oms_interface

    # create protocol-interface copies

    def create_protocol_copies(self, roadm, ns:dict):

        prctl = roadm.find('device:protocols', ns)
        lldp = prctl.find('lldp:lldp', ns)
        nbr_list = lldp.find('lldp:nbr-list', ns)
        nbr_list_copy = copy.deepcopy(nbr_list)
        if_list = nbr_list.findall('lldp:if-name', ns)
        for e in if_list:
            if e.find('lldp:ifName', ns).text == '1GE-interface-1':
                nbr_list_interface = copy.deepcopy(e)
        p_if_list = lldp.findall('lldp:port-config', ns)
        p_if_list_copy = copy.deepcopy(p_if_list)
        for p in p_if_list:
            if p.find('lldp:ifName', ns).text == '1GE-interface-1':
                # print(p.find('lldp:ifName', ns).text)
                port_config_interface = copy.deepcopy(p)
        return nbr_list_copy, nbr_list_interface, p_if_list_copy, port_config_interface

    # Remove Circuit-packs that creates conflicts with newly created circuit-packs
    def del_circuit_packs(self, roadm, ns:dict):
        cps = roadm.findall('device:circuit-packs', ns)
        for cp in cps:
            # if cp.find('device:circuit-pack-name', ns).text == '1/0' or cp.find('device:circuit-pack-name', ns).text == '3/0' or cp.find('device:circuit-pack-name', ns).text == '5/0' :
            roadm.remove(cp)
        return roadm
        # create protocol-interface copies

    def create_protocol_copies(self,roadm_root, ns:dict):

        prctl = roadm_root.find('device:protocols', ns)
        lldp = prctl.find('lldp:lldp', ns)
        if lldp is not None:
            lldp_copy = copy.deepcopy(lldp)
            nbr_list = lldp.find('lldp:nbr-list', ns)
            nbr_list_copy = copy.deepcopy(nbr_list)
            if_list = nbr_list.findall('lldp:if-name', ns)
            for e in if_list:
                if e.find('lldp:ifName', ns).text == '1GE-interface-1':
                    nbr_list_interface = copy.deepcopy(e)
            port_config_interface = lldp.find('lldp:port-config', ns)
            port_config_interface_copy = copy.deepcopy(port_config_interface)
            return lldp_copy, nbr_list_copy, nbr_list_interface, port_config_interface_copy
        else:
            return None, None, None, None


    def edit_tags(self, filename:str):
        fin = open(filename, "rt")
        # read file contents to string
        data = fin.read()
        # replace all occurrences of the required string
        data = data.replace('<supported-interface-capability>',
                            '<supported-interface-capability xmlns:x="http://org/openroadm/port/types">')
        data = data.replace('<type>openROADM-if:ethernetCsmacd',
                            '<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:ethernetCsmacd')
        data = data.replace('<type>openROADM-if:opticalTransport',
                            '<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:opticalTransport')
        data = data.replace('<type>openROADM-if:openROADMOpticalMultiplex',
                            '<type xmlns:openROADM-if="http://org/openroadm/interfaces">openROADM-if:openROADMOpticalMultiplex')
        data=data.replace('</ns1:nbr-list></ns1:lldp>', '</ns1:nbr-list>\n</ns1:lldp>')
        # close the input file
        fin.close()
        # open the input file in write mode
        fin = open(filename, "wt")
        # overrite the input file with the resulting data
        fin.write(data)
        # close the file
        fin.close()

    def edit_tags_xpdr(self, filename:str):
        fin = open(filename, "rt")
        # read file contents to string
        data = fin.read()
        # replace all occurrences of the required string
        data = data.replace('<supported-interface-capability>',
                            '<supported-interface-capability xmlns:org-openroadm-port-types="http://org/openroadm/port/types">')
        # close the input file
        fin.close()
        # open the input file in write mode
        fin = open(filename, "wt")
        # overrite the input file with the resulting data
        fin.write(data)
        # close the file
        fin.close()

    def create_data_models(self, dev_name:str, deg_number:int, device_type:str, node_num:int, neighbours:list, remote_port_Ids:list):

        # deg_number=4
        # print(roadm_root.tag)
        ET.register_namespace('', 'http://org/openroadm/device')
        ET.register_namespace('interfaces', 'http://org/openroadm/ethernet-interfaces')
        ET.register_namespace('otinterfaces', 'http://org/openroadm/optical-transport-interfaces')
        with open(self.roadmTemplateFile, "r") as f:
            xmltest = f.read()
            f.close
        roadm_root = ET.fromstring(xmltest)
        cp_name_list = []
        interface_list = []
        roadm_root = self.create_info(roadm_root, self.ns, dev_name, str(deg_number), device_type, node_num)
        # for elem in roadm_root.findall('device:info',ns):
        #     roadm_root.remove(elem)
        # roadm_root.append(roadm_info)

        sel = roadm_root.find('device:degree', self.ns)
        degrees = self.create_degree(sel, self.ns, deg_number)

        # print(degrees)
        for elem in roadm_root.findall('device:degree', self.ns):
            roadm_root.remove(elem)
        for d in degrees:
            roadm_root.append(d)
            circuit_packs = d.findall('.//device:circuit-pack-name', self.ns)
            for c in circuit_packs:
                # print(c.text)
                cp_name_list.append(c.text)
        cp_name_list = list(dict.fromkeys(cp_name_list))

        # print(cp_name_list)

        # Create a deep copy of the 4 circuit-packs namely parent, eth, osc and srg
        roadm_root, parent_cp, eth_cp, osc_cp, srg_cp = self.cp_copies(roadm_root, self.ns)
        cp_list = self.create_circuit_pack(cp_name_list, eth_cp, osc_cp, parent_cp, self.ns)
        for elem in cp_list:
            roadm_root.append(elem)

        # Create the SRG bodies
        srg = roadm_root.find('device:shared-risk-group', self.ns)
        srgs, srg_cp_name_list = self.create_srgs(srg, self.ns, deg_number, cp_name_list)
        for elem in roadm_root.findall('device:shared-risk-group', self.ns):
            roadm_root.remove(elem)
        for s in srgs:
            roadm_root.append(s)

        # create srg cp and append then to root
        srgs = roadm_root.find('device:shared-risk-group', self.ns)
        # First append the AD-DEG ports to the SRG circuit-pack
        # TODO: IF conditionn should be implemented later on
        srg_ports = srg_cp.findall('device:ports', self.ns)
        deg_port = self.create_srg_deg_ports(srg_ports, deg_number, self.ns)
        for d in deg_port:
            srg_cp.append(d)
        srg_cp_body_list = self.create_srg_cp(srg_list=srg_cp_name_list, srg_cp_body=srg_cp, ns=self.ns)
        for s in srg_cp_body_list:
            roadm_root.append(s)

        # create the interface bodies and append
        ge_interface, ots_interface, oms_interface = self.create_interface_copies(roadm_root, self.ns)
        for i in range(1, deg_number + 1):
            ge_interface_modify = copy.deepcopy(ge_interface)
            g_name = ge_interface_modify.find('device:name', self.ns)
            g_name.text = g_name.text[:-1] + str(i)
            sg_cp = ge_interface_modify.find('device:supporting-circuit-pack-name', self.ns)
            sg_cp.text = str(i) + sg_cp.text[1:]
            interface_list.append(ge_interface_modify)

            ots_interface_modify = copy.deepcopy(ots_interface)
            ots_name = ots_interface_modify.find('device:name', self.ns)
            ots_name.text = ots_name.text[:7] + str(i) + ots_name.text[8:]
            ots_scp = ots_interface_modify.find('device:supporting-circuit-pack-name', self.ns)
            ots_scp.text = str(i) + ots_scp.text[1:]
            interface_list.append(ots_interface_modify)

            oms_interface_modify = copy.deepcopy(oms_interface)
            oms_name = oms_interface_modify.find('device:name', self.ns)
            oms_name.text = oms_name.text[:7] + str(i) + oms_name.text[8:]
            oms_scp = oms_interface_modify.find('device:supporting-circuit-pack-name', self.ns)
            oms_scp.text = str(i) + oms_scp.text[1:]
            oms_sifc = oms_interface_modify.find('device:supporting-interface', self.ns)
            if oms_sifc is not None:
                oms_sifc.text = ots_name.text
            interface_list.append(oms_interface_modify)

        for i in roadm_root.findall('device:interface', self.ns):
            roadm_root.remove(i)

        for i in interface_list:
            roadm_root.append(i)

        lldp_copy, nbr_list_copy, nbr_list_interface, port_config_interface_copy = self.create_protocol_copies(roadm_root,
                                                                                                               self.ns)
        nbr_new_list = []
        port_config_ifc_new_list = []
        if lldp_copy is not None and nbr_list_copy is not None and nbr_list_interface is not None and port_config_interface_copy is not None:
            for i in range(1, deg_number + 1):
                nbr_list_interface_modify = copy.deepcopy(nbr_list_interface)
                nbr_if_name = nbr_list_interface_modify.find('lldp:ifName', self.ns)
                nbr_if_name.text = nbr_if_name.text[:-1] + str(i)
                remote_sys = nbr_list_interface_modify.find('lldp:remoteSysName', self.ns)
                remote_sys.text = 'ROADM-' + str(neighbours[i - 1])
                remote_port = nbr_list_interface_modify.find('lldp:remotePortId', self.ns)
                remote_port.text = '1GE-interface-' + str(remote_port_Ids[i - 1])
                nbr_new_list.append(nbr_list_interface_modify)
                pc_ifc_modify = copy.deepcopy(port_config_interface_copy)
                pc_ifc_name = pc_ifc_modify.find('lldp:ifName', self.ns)
                pc_ifc_name.text = pc_ifc_name.text[:-1] + str(i)
                port_config_ifc_new_list.append(pc_ifc_modify)

            nbr_list_copy.clear()

            for i in nbr_new_list:
                nbr_list_copy.append(i)

            for i in nbr_list_copy:
                print(i.find('lldp:ifName', self.ns).text)
                print(i.find('lldp:remoteSysName', self.ns).text)
                print(i.tag)
            for i in lldp_copy.findall('lldp:port-config', self.ns):
                lldp_copy.remove(i)
            for i in port_config_ifc_new_list:
                lldp_copy.append(i)

            for i in lldp_copy.findall('lldp:nbr-list', self.ns):
                lldp_copy.remove(i)

            lldp_copy.append(nbr_list_copy)
            prctl = roadm_root.find('device:protocols', self.ns)
            lldp = prctl.find('lldp:lldp', self.ns)
            prctl.remove(lldp)
            prctl.append(lldp_copy)
        tree1 = ET.ElementTree(roadm_root)
        tree1.write(self.outputPath+'/ROADM-' + dev_name + '.xml',  encoding="utf-8", xml_declaration=True)
        self.edit_tags(self.outputPath+"/ROADM-"+ dev_name+".xml")
        interface_list.clear()
        cp_list.clear()
        #del roadm_root, parent_cp, eth_cp, osc_cp, srg_cp, lldp_copy, nbr_list_copy,port_config_ifc_new_list
        return "Roadm model for node {} has been created".format(dev_name)

    def create_xpdr_data_models(self, dev_name:str, device_type:str, node_num:int):
        ET.register_namespace('', 'http://org/openroadm/device')
        with open(self.xpdrTemplateFile, "r") as f:
            xmltest = f.read()
            f.close
        xpdr_root = ET.fromstring(xmltest)
        xpdr_root=self.create_info(xpdr_root, self.ns, dev_name, None,device_type, node_num)
        tree2=ET.ElementTree(xpdr_root)
        tree2.write(self.outputPath+'/XPDR-' + dev_name + '.xml',  encoding="utf-8", xml_declaration=True)
        self.edit_tags_xpdr(self.outputPath+'/XPDR-' + dev_name + '.xml')
        return "Xponder model for node {} has been created".format(dev_name)


""" def main():
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
    interface_list =[]
    roadm_root=create_info(roadm_root, ns, 'ROADM-TEST', str(deg_number), 'roadm', 1)
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

    # create the interface bodies and append
    ge_interface, ots_interface, oms_interface=create_interface_copies(roadm_root, ns)
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

    tree1= ET.ElementTree(roadm_root)
    tree1.write('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf-generated/ROADM-TEST.xml',  encoding="utf-8", xml_declaration=True)
    edit_tags("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf-generated/ROADM-TEST.xml")
    with open("/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf/oper-XPDRA.xml", 'r') as x:
        xmlxpdr=x.read()
        x.close()
    xpdr_root=ET.fromstring(xmlxpdr)
    xpdr_root=create_info(xpdr_root, ns, 'XPDR-TEST', None,'xpdr', 1)
    tree2=ET.ElementTree(xpdr_root)
    tree2.write('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf-generated/XPDR-TEST.xml',  encoding="utf-8", xml_declaration=True)
    edit_tags_xpdr('/home/shabnam/TransportPCE/transportpce/integration/demo-standalone/conf-generated/XPDR-TEST.xml')
if __name__ == "__main__":
    main()

 """
