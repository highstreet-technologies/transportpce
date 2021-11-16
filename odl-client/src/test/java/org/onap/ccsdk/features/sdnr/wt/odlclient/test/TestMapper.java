/*
 * Copyright (C) 2019 highstreet technologies GmbH Intellectual Property.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.onap.ccsdk.features.sdnr.wt.odlclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.custommonkey.xmlunit.XMLUnit;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.Ignore;
import org.junit.Test;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlJsonSerializer;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlObjectMapperXml;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlRpcObjectMapperXml;
import org.onap.ccsdk.features.sdnr.wt.odlclient.data.OdlXmlSerializer;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.Direction;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.EquipmentTypeEnum;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.FrequencyTHz;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.ModulationFormat;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.OpticalControlMode;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.PortQual;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.PowerDBm;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.R100G;
import org.opendaylight.yang.gen.v1.http.org.openroadm.common.types.rev181019.State;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.LedControlInputBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.CircuitPackCategoryBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.ParentCircuitPackBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.Ports;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.PortsBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.ports.OtdrPort;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.ports.TransponderPort;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.pack.ports.TransponderPortBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.packs.CircuitPacks;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.circuit.packs.CircuitPacksBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.connection.DestinationBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.connection.SourceBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.Interface;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.InterfaceBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.interfaces.grp.InterfaceKey;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.led.control.input.equipment.entity.ShelfBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.OrgOpenroadmDevice;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.OrgOpenroadmDeviceBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.Info;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnections;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.RoadmConnectionsBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.device.rev181019.org.openroadm.device.container.org.openroadm.device.UsersBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.equipment.states.types.rev171215.AdminStates;
import org.opendaylight.yang.gen.v1.http.org.openroadm.equipment.states.types.rev171215.States;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.NetworkMediaChannelConnectionTerminationPoint;
import org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpenROADMOpticalMultiplex;
import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.Protocols1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.lldp.rev181019.lldp.container.lldp.PortConfig;
import org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1Builder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.och.container.OchBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.optical.transport.interfaces.rev181019.Interface1;
import org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.odu.container.OduBuilder;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.IfOCH;
import org.opendaylight.yang.gen.v1.http.org.openroadm.port.types.rev181019.PortWavelengthTypes;
import org.opendaylight.yang.gen.v1.http.org.openroadm.user.mgmt.rev171215.PasswordType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.user.mgmt.rev171215.UsernameType;
import org.opendaylight.yang.gen.v1.http.org.openroadm.user.mgmt.rev171215.user.profile.User.Group;
import org.opendaylight.yang.gen.v1.http.org.openroadm.user.mgmt.rev171215.user.profile.UserBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.NetconfNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.netconf.node.topology.rev150114.netconf.node.connection.status.available.capabilities.AvailableCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class TestMapper {

    private static final Logger LOG = LoggerFactory.getLogger(TestMapper.class);

    // @Test
    public void testMapRoadmInfo()
            throws ClassNotFoundException, JsonParseException, JsonMappingException, IOException {
        OdlObjectMapperXml xmlMapper = new OdlObjectMapperXml(true);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-info.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("outputxml={}", xmlMapper.readValue(fileContent, Info.class));
    }

    //    @Test
    public void testNodeInfo() throws JsonParseException, JsonMappingException, IOException {
        OdlObjectMapperXml xmlMapper = new OdlObjectMapperXml(true);

        // LOG.info("outputjson={}", jsonMapper.readValue(NODEINFO, NetconfNode.class, "network-topology:node"));
        LOG.info("outputxml={}", xmlMapper.readValue(this.getTrimmedFileContent("/xml/roadm-device3.xml"), Info.class));

    }

    //@Test
    public void testRpcSerializer() {
        OdlXmlSerializer mapper = new OdlXmlSerializer();
        final LedControlInputBuilder builder = new LedControlInputBuilder();
        builder.setEnabled(true).setEquipmentEntity(new ShelfBuilder().setShelfName("1/0").build());
        String inputPayload = mapper.writeValueAsString(builder.build(), "input");
        LOG.info(inputPayload);
        try {
            assertTrue(XMLUnit.compareXML(this.getTrimmedFileContent("/xml/roadm-rpc-input1.xml"), inputPayload)
                    .similar());
        } catch (SAXException | IOException e) {
            fail(e.getMessage());
        }
    }

    //FIXME: problem with multiline resource loading
    //    @Test
    public void testLeafListSerializing() throws ParserConfigurationException, TransformerException {
        OrgOpenroadmDeviceBuilder builder = new OrgOpenroadmDeviceBuilder();
        builder.setUsers(new UsersBuilder()
                .setUser(Arrays.asList(
                    new UserBuilder()
                        //.setName(new UsernameType("openroadm"))
                        .setName(new UsernameType("openroadm2"))
                        .setPassword(new PasswordType("openroadm"))
                        .setGroup(Group.Sudo)
                        .build()))
                .build());

        OdlXmlSerializer mapper = new OdlXmlSerializer();
        OdlJsonSerializer mapper2 = new OdlJsonSerializer();

        String inputPayload = mapper.writeValueAsString(builder.build(), "org-openroadm-device");
        try {
            LOG.info(inputPayload);
            assertTrue(
                    XMLUnit.compareXML(this.getTrimmedFileContent("/xml/roadm-device2.xml"), inputPayload).similar());
        } catch (SAXException | IOException e) {
            fail(e.getMessage());
        }
        inputPayload = mapper2.writeValueAsString(builder.build(), "org-openroadm-device");
        LOG.info(inputPayload);
    }

    @Test
    public void testInterfaceConfigSerialization() throws JsonProcessingException {
        //<interface xmlns="http://org/openroadm/device">
        //  <administrative-state >inService</administrative-state>
        //  <circuit-id >   TBD    </circuit-id>
        //  <description >  TBD   </description>
        //  <name >XPDR1-NETWORK1-1</name>
        //  <operational-state >null</operational-state>
        //  <supporting-circuit-pack-name >1/0/1-PLUG-NET</supporting-circuit-pack-name>
        //  <supporting-interface >null</supporting-interface>
        //  <supporting-port >1</supporting-port>
        //  <type >interface org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalChannel</type>
        //</interface>

        InterfaceBuilder builder = new InterfaceBuilder();
        builder.setAdministrativeState(AdminStates.InService);
        builder.setCircuitId("TBD");
        builder.setDescription("TBD");
        builder.setName("XPDR1-NETWORK1-1");
        builder.setOperationalState(null);
        builder.setSupportingCircuitPackName("1/0/1-PLUG-NET");
        builder.setSupportingInterface(null);
        builder.setSupportingPort(1);
        builder.setType(org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalChannel.class);
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        LOG.info("ifoutput = {}", mapper.writeValueAsString(builder.build()));
    }

    @Test
    public void testTopologyNodeDeser() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadma-netconfnode.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        NetconfNode netconfNode = mapper.readValue(xml, NetconfNode.class);
        LOG.debug("{}", netconfNode);
        assertNotNull(netconfNode.getAvailableCapabilities());
        @Nullable
        List<AvailableCapability> caps = netconfNode.getAvailableCapabilities().getAvailableCapability();
        assertTrue(caps.size() > 0);
        assertTrue(caps.stream().filter(new Predicate<AvailableCapability>() {
            @Override
            public boolean test(AvailableCapability arg0) {
                return arg0.getCapability().contains("org-openroadm-device");
            }

            ;
        }).count() > 0);
    }

    //@Test
    public void testProtocolAugment() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadm-device-protocols.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Protocols1 data = mapper.readValue(xml, Protocols1.class);
        LOG.info("protocol={}", data);
        LOG.info("protocol lldp={}", data.getLldp());
        @Nullable
        Collection<PortConfig> cfgs = data.getLldp().getPortConfig().values();
        LOG.info("protocol lldp portconfig={}", cfgs);
        for (PortConfig portConfig : cfgs) {
            LOG.info("portconfig={} if={}", portConfig.getAdminStatus(), portConfig.getIfName());
        }
    }

    @Test
    public void testProtocolPortConfigDeser() throws JsonParseException, JsonMappingException, IOException {
        String xml = " <port-config>\n" + "          <ifName>1GE-interface-1</ifName>\n"
                + "          <adminStatus>txandrx</adminStatus>\n" + "        </port-config>";
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        PortConfig portConfig = mapper.readValue(xml, PortConfig.class);
        LOG.info("portconfig={} if={}", portConfig.getAdminStatus(), portConfig.getIfName());
    }

    @Test
    public void testInterfaceDeser() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadm-interfaces.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Interface intf = mapper.readValue(xml, Interface.class);
        LOG.info("interface={}", intf);
    }

    @Test
    public void testInterface2Deser() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadm-interfaces2.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Interface intf = mapper.readValue(xml, Interface.class);
        LOG.info("interface2={}", intf);
        LOG.info("interface2aug={}", intf.augmentation(Interface1.class));
    }

    @Test
    public void testPortsDeser() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadm-ports.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        OtdrPort port = mapper.readValue(xml, OtdrPort.class);
        LOG.info("port={}", port);
    }

    @Test
    public void testCompleteDeser() throws IOException {
        String xml = this.getTrimmedFileContent("/xml/roadm-device-complete.xml");
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        OrgOpenroadmDevice data = mapper.readValue(xml, OrgOpenroadmDevice.class);
        LOG.info("complete={}", data);
    }

    private String getTrimmedFileContent(String filename) throws IOException {
        ImmutableList<String> lines =
                Files.asCharSource(new File(TestMapper.class.getResource(filename).getFile()), StandardCharsets.UTF_8)
                        .readLines();
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line.trim());
        }
        LOG.info("input={}", sb.toString());
        return sb.toString();
    }

    private Reader getFileReader(String filename) throws FileNotFoundException {
        return new FileReader(new File(TestMapper.class.getResource(filename).getFile()));
    }

    @Test
    public void testNetconfNodeDeserializer() throws JsonParseException, JsonMappingException, IOException {
        OdlObjectMapperXml xmlMapper = new OdlObjectMapperXml(true);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/node.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("res={}", xmlMapper.readValue(fileContent, NetconfNode.class));
    }

    @Test
    public void testRoadmConnectionsSerializer() {
        final String connectionNumber = "DEG2-TTP-TXRX-SRG1-PP1-TXRX-1";
        final String srcTp = "DEG2-TTP-TXRX";
        final String waveNumber = "1";
        final String destTp = "SRG1-PP1-TXRX";
        RoadmConnectionsBuilder rdmConnBldr = new RoadmConnectionsBuilder().setConnectionName(connectionNumber)
                .setOpticalControlMode(OpticalControlMode.Off)
                .setSource(new SourceBuilder().setSrcIf(srcTp + "-nmc-" + waveNumber).build())
                .setDestination(new DestinationBuilder().setDstIf(destTp + "-nmc-" + waveNumber).build());
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        String res = mapper.writeValueAsString(rdmConnBldr.build(), RoadmConnections.class);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-connections-put-request.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("src={}", fileContent);
        LOG.info("res={}", res);
        try {
            assertTrue(XMLUnit.compareXML(fileContent, res).similar());
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage());
            fail(e.getMessage());
        }

    }

    @Test
    public void testInterfaceSerializer() {
        InterfaceBuilder interfaceBuilder = new InterfaceBuilder().setDescription("  TBD   ").setCircuitId("   TBD    ")
                .setSupportingCircuitPackName("2/0").setSupportingPort("L1")
                .setAdministrativeState(AdminStates.InService)
                .setType(NetworkMediaChannelConnectionTerminationPoint.class).setName("DEG2-TTP-TXRX-nmc-1")
                .withKey(new InterfaceKey("DEG2-TTP-TXRX-nmc-1"));
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        String res = mapper.writeValueAsString(interfaceBuilder.build(), Interface.class);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-interface-put-request.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("src={}", fileContent);
        LOG.info("res={}", res);
        try {
            assertTrue(XMLUnit.compareXML(fileContent, res).similar());
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage());
            fail(e.getMessage());
        }

    }

    public String createOpenRoadmOchInterfaceName(String logicalConnectionPoint, Long waveNumber) {
        return logicalConnectionPoint + "-" + waveNumber;
    }

    @Test
    public void testInterfaceOCHSerializer() {
        InterfaceBuilder ifbuilder = new InterfaceBuilder()
                .setAdministrativeState(AdminStates.InService)
                .setCircuitId("TBD").setDescription("TBD")
                .setName("XPDR1-NETWORK1-761:768")
                .setSupportingCircuitPackName("1/0/1-PLUG-NET")
                .setSupportingPort(1)
                .setType(org.opendaylight.yang.gen.v1.http.org.openroadm.interfaces.rev170626.OpticalChannel.class);
        Interface1Builder ifOCHBuilder = new Interface1Builder()
                .setOch(new OchBuilder()
                    .setFrequency(FrequencyTHz.getDefaultInstance("196.1000"))
                    .setRate(R100G.class)
                    .setTransmitPower(PowerDBm.getDefaultInstance("-5"))
                    .setModulationFormat(ModulationFormat.DpQpsk)
                    .build());
        ifbuilder.addAugmentation(ifOCHBuilder.build());
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        String res = mapper.writeValueAsString(ifbuilder.build(), Interface.class);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-interface-och-put-request.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("src={}", fileContent);
        LOG.info("res={}", res);
        try {
            assertTrue(XMLUnit.compareXML(fileContent, res).similar());
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    public void testInterfaceOCHDeSerializer() {
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-interface-och-put-request2.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        Interface iface = null;
        try {
            iface = mapper.readValue(fileContent,Interface.class);
        } catch (JsonProcessingException e) {
            fail(e.getMessage());
        }
        LOG.info("deser={}",iface);
    }

    @Ignore
    @Test
    public void testPowerSerializer() {
        final String interfaceString = "<interface xmlns=\"http://org/openroadm/device\">"
                + "<name>SRG1-PP1-TXRX-nmc-1</name>"
                + "<supporting-circuit-pack-name>3/0</supporting-circuit-pack-name>" + "<circuit-id>TBD</circuit-id>"
                + "<administrative-state>inService</administrative-state>" + "<supporting-port>C1</supporting-port>"
                + "<type xmlns:x=\"http://org/openroadm/interfaces\">x:networkMediaChannelConnectionTerminationPoint</type>"
                + "<description>TBD</description>" + "</interface>";
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Interface interfaceObj = null;
        try {
            interfaceObj = mapper.readValue(interfaceString, Interface.class);
        } catch (IOException e) {
            LOG.error("caught IOexception when reading values in XML content {}", interfaceString, e);
        }

        InterfaceBuilder ochInterfaceBuilder = new InterfaceBuilder(interfaceObj);
        org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1 if1 =
            ochInterfaceBuilder.augmentation(
                org.opendaylight.yang.gen.v1.http.org.openroadm.optical.channel.interfaces.rev181019.Interface1.class);
        OchBuilder ochBuilder = new OchBuilder(if1.getOch());
        BigDecimal txPower = BigDecimal.valueOf(1);
        ochBuilder.setTransmitPower(new PowerDBm(txPower));
        ochInterfaceBuilder.addAugmentation(Interface1.class,
                new Interface1Builder().setOch(ochBuilder.build()).build());
    }

    @Test
    public void testAugmentDeserializerManual() {
        final String interfaceString = "<interface xmlns=\"http://org/openroadm/device\">\n"
                + "  <name>OTS-DEG2-TTP-TXRX</name>\n" + "  <operational-state>inService</operational-state>\n"
                + "  <supporting-circuit-pack-name>2/0</supporting-circuit-pack-name>\n"
                + "  <administrative-state>inService</administrative-state>\n"
                + "  <ots xmlns=\"http://org/openroadm/optical-transport-interfaces\">\n"
                + "    <ingress-span-loss-aging-margin>0.0</ingress-span-loss-aging-margin>\n"
                + "    <span-loss-receive>15.0</span-loss-receive>\n"
                + "    <span-loss-transmit>6.0</span-loss-transmit>\n" + "    <fiber-type>smf</fiber-type>\n"
                + "  </ots>\n" + "  <supporting-port>L1</supporting-port>\n"
                + "  <type xmlns:x=\"http://org/openroadm/interfaces\">x:opticalTransport</type>\n" + "</interface>";
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Interface interfaceObj = null;
        try {
            interfaceObj = mapper.readValue(interfaceString, Interface.class);
        } catch (IOException e) {
            LOG.error("caught IOexception when reading values in XML content {}", interfaceString, e);
        }
        assertNotNull(interfaceObj);
        try {
            InterfaceBuilder builder = new InterfaceBuilder(interfaceObj);
            builder.addAugmentation(Interface1.class, mapper.readValue(interfaceString, Interface1.class));
            interfaceObj = builder.build();
        } catch (IOException e) {
            LOG.error("caught IOexception when reading values in XML content {}", interfaceString, e);
        }

        LOG.info("if = {}", interfaceObj);
        LOG.info("if1 = {}", interfaceObj.augmentation(Interface1.class));

    }

    @Test
    public void testAugmentDeserializerAutomatic() {
        final String interfaceString = "<interface xmlns=\"http://org/openroadm/device\">\n"
                + "  <name>OTS-DEG2-TTP-TXRX</name>\n" + "  <operational-state>inService</operational-state>\n"
                + "  <supporting-circuit-pack-name>2/0</supporting-circuit-pack-name>\n"
                + "  <administrative-state>inService</administrative-state>\n"
                + "  <ots xmlns=\"http://org/openroadm/optical-transport-interfaces\">\n"
                + "    <ingress-span-loss-aging-margin>0.0</ingress-span-loss-aging-margin>\n"
                + "    <span-loss-receive>15.0</span-loss-receive>\n"
                + "    <span-loss-transmit>6.0</span-loss-transmit>\n" + "    <fiber-type>smf</fiber-type>\n"
                + "  </ots>\n" + "  <supporting-port>L1</supporting-port>\n"
                + "  <type xmlns:x=\"http://org/openroadm/interfaces\">x:opticalTransport</type>\n" + "</interface>";
        OdlObjectMapperXml mapper = new OdlObjectMapperXml();
        Interface interfaceObj = null;
        try {
            interfaceObj = mapper.readValue(interfaceString, Interface.class, Interface1.class);
        } catch (IOException e) {
            LOG.error("caught IOexception when reading values in XML content {}", interfaceString, e);
        }
        assertNotNull(interfaceObj);
        LOG.info("if = {}", interfaceObj);
        LOG.info("if1 = {}", interfaceObj.augmentation(Interface1.class));

    }

    @Test
    public void testInterfaceOduDeserializer() {
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        Interface interfaceObj = null;
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-interface-odu.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        try {
            interfaceObj = mapper.readValue(fileContent, Interface.class);
        } catch (IOException e) {
            LOG.error("caught IOexception when reading values in roadm-interface-odu.xml file", e);
        }
        assertNotNull(interfaceObj);
        LOG.info("if = {}", interfaceObj);
        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1Builder oduBuilder =
                new org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1Builder(
                    interfaceObj.augmentation(
                        org.opendaylight.yang.gen.v1.http.org.openroadm.otn.odu.interfaces.rev181019.Interface1.class));
        OduBuilder odu = new OduBuilder(oduBuilder.getOdu());
        LOG.info("odu = {}", odu.build());
    }

    @Test
    public void testCuircutPackSerializer() {
        CircuitPacksBuilder cpBldr = new CircuitPacksBuilder();
        cpBldr.setCircuitPackCategory(new CircuitPackCategoryBuilder().setType(EquipmentTypeEnum.CircuitPack).build())
        .setCircuitPackMode("NORMAL").setCircuitPackName("1/0/1-PLUG-NET")
        .setCircuitPackProductCode("Line_NW_P").setCircuitPackType("line_pluggable")
        .setEquipmentState(States.NotReservedInuse).setHardwareVersion("0.1")
        .setModel("CFP2").setOperationalState(State.InService)
        .setParentCircuitPack(new ParentCircuitPackBuilder().setCircuitPackName("1/0").setCpSlotName("1").build())
        .setPorts(Arrays.asList(new PortsBuilder()
                .setAdministrativeState(AdminStates.InService).setLabel("2")
                .setOperationalState(State.InService).setPortDirection(Direction.Bidirectional)
                .setPortName("1").setPortQual(PortQual.XpdrNetwork)
                .setPortType("CFP2").setPortWavelengthType(PortWavelengthTypes.Wavelength)
                .setSupportedInterfaceCapability(Arrays.asList(IfOCH.class))
                .setTransponderPort(new TransponderPortBuilder()
                        .setPortPowerCapabilityMaxRx(PowerDBm.getDefaultInstance("1.0"))
                        .setPortPowerCapabilityMaxTx(PowerDBm.getDefaultInstance("0.0"))
                        .setPortPowerCapabilityMinRx(PowerDBm.getDefaultInstance("22.0"))
                        .setPortPowerCapabilityMinTx(PowerDBm.getDefaultInstance("5.0"))
                        .build())
                .build()))
        .setProductCode("Line_NW_P").setSerialId("_1234_")
        .setShelf("1").setSlot("1").setSubSlot("1").setType("line/network pluggable")
        .setVendor("VendorA").setIsPluggableOptics(true).build();
        OdlRpcObjectMapperXml mapper = new OdlRpcObjectMapperXml();
        String res = mapper.writeValueAsString(cpBldr.build(), CircuitPacks.class);
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-circuitpacks.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        LOG.info("src={}", fileContent);
        LOG.info("res={}", res);
        try {
            assertTrue(XMLUnit.compareXML(fileContent, res).similar());
        } catch (SAXException | IOException e) {
            LOG.error(e.getMessage());
            fail(e.getMessage());
        }
    }

    @Test
    public void testNodeDeserializer() {
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/node-connected.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        try {
            LOG.info("node={}",mapper.readValue(fileContent,NetconfNode.class));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRoadmPortDeserializer() {
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-ports2.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        try {
            Ports port = mapper.readValue(fileContent,Ports.class);
            LOG.info("port={}",port);
            assertEquals("1",port.getPortName());
            LOG.info("portName: {}",port.getPortName());
            assertEquals(State.InService,port.getOperationalState());
            assertEquals(PortQual.XpdrNetwork,port.getPortQual());
            assertEquals(AdminStates.InService,port.getAdministrativeState());
            assertEquals(Direction.Bidirectional,port.getPortDirection());
            assertEquals("2",port.getLabel());
            assertEquals(Arrays.asList(IfOCH.class),port.getSupportedInterfaceCapability());
            assertEquals("CFP2",port.getPortType());
            assertEquals(PortWavelengthTypes.Wavelength,port.getPortWavelengthType());
            TransponderPort tport = port.getTransponderPort();
            assertNotNull(tport);
            assertEquals(PowerDBm.getDefaultInstance("-5.0"),tport.getPortPowerCapabilityMinTx());
            assertEquals(PowerDBm.getDefaultInstance("-22.0"),tport.getPortPowerCapabilityMinRx());
            assertEquals(PowerDBm.getDefaultInstance("1.0"),tport.getPortPowerCapabilityMaxRx());
            assertEquals(PowerDBm.getDefaultInstance("0.0"),tport.getPortPowerCapabilityMaxTx());


        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRoadmInterfaces3Deserializer() {
        String fileContent = null;
        try {
            fileContent = this.getTrimmedFileContent("/xml/roadm-interfaces3.xml");
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
        OdlObjectMapperXml mapper = new OdlObjectMapperXml(true);
        try {
            Interface if3 = mapper.readValue(fileContent,Interface.class);
            LOG.info("if3={}",if3);
            assertEquals("OMS-DEG2-TTP-TXRX",if3.getName());
            assertEquals("2/0",if3.getSupportingCircuitPackName());
            assertEquals(State.InService,if3.getOperationalState());
            assertEquals("OTS-DEG2-TTP-TXRX",if3.getSupportingInterface());
            assertEquals("TBD",if3.getCircuitId());
            assertEquals(AdminStates.InService,if3.getAdministrativeState());
            assertEquals("L1",if3.getSupportingPort());
            assertEquals(OpenROADMOpticalMultiplex.class,if3.getType());
            assertEquals("TBD",if3.getDescription());

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
