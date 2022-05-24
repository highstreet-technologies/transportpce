import time
import json
import base64
from .basetest import BaseTest
import os


class End2EndTestBBNet(BaseTest):

    def __init__(self, sdncClients, primarySdncClient, trpceClient, trpceContainer, sims, config, rdmInternalConfig):
        BaseTest.__init__(self, sdncClients, primarySdncClient, trpceClient, sims, config)
        self.WAITING = 10
        self.trpceContainer = trpceContainer
        self.rdmInternalConfig = rdmInternalConfig
        self.isAluminium = '2.3' in self.trpceContainer.getImageTag()

    def logError(self, message):
        print("ERROR: " + message)

    def test(self, args):
        self.waitForReadyState()
        print("e2e test args=" + str(args))
        if "--clean" in args:
            return self.clean()
        if "--skipmount" not in args:
            success = self.mountAll()
            if success:
                print("mounting simulators succeeded")
            else:
                print("problem mounting simulators")
                return False
            # stop if requested
            if '--mount' in args:
                return True
        else:
            print("skip mounting")
        success = self.waitForConnectedState(self.WAITING)
        if success:
            print("all devices are connected")
        else:
            print("problem with deviceconnection")
            return False
        success = self.checkAutocreatedNetworksAfterMount(30, self.WAITING)
        if success:
            print("autocreated networks are looking good")
        else:
            print("problem with autocreated networks")
            return False
        time.sleep(self.WAITING)
        success = self.createLinks()
        if success:
            print("creating links succeeded")
        else:
            print("problem creating links")
            return False
        time.sleep(self.WAITING)
        success = self.configROADMS(spanlossBase=5.7, spanlossCurrent=6, spanlossEngineered=6.1)
        if success:
            print("configure Roadms succeeded")
        else:
            print("problem configure Roadms")
            return False
        if '--prepare' in args:
            return True
        time.sleep(self.WAITING)
        success = self.createService(nodeidA='XPDR-UlmMuen', nodeidZ='XPDR-EsseDues')
        if success:
            print("creating service 1 succeeded")
        else:
            print("problem creating service 1")
            return False
        time.sleep(self.WAITING)
        success = self.getService(20, 15)
        if success:
            print("reading service information succeeded")
        else:
            print("problem reading service information")
            return False
        time.sleep(2)
        success = self.checkConnections()
        if success:
            print("roadm connections are valid")
        else:
            print("problem with roadm connections")
            return False
        time.sleep(5)
        success = self.checkTopology(xpdr="XPDR-UlmMuen", roadm="ROADM-Ulm", roadmDeg="1")
        if success:
            print("roadm topology is valid")
        else:
            print("problem with topology")
            return False
        if "test2" in args:
            time.sleep(self.WAITING)
            success = self.test2()
            if success:
                print("test2 passed with creation of ethservice2")
            else:
                print("test2 failed")
                return False
        if "test3" in args:
            time.sleep(10)
            success = self.test3()
            if success:
                print("test3 passed")
            else:
                print("test3 failed")
                return False
        return True

    def test2(self):
        success = self.checkAutocreatedNetworksAfterMount(2, self.WAITING)
        if success:
            print("autocreated networks are looking good")
        else:
            print("problem with autocreated networks")
            return False
        success = self.createService(serviceName='service2', serviceRate=100, nodeidA='XPDR-HambBrem',
                                     nodeidZ='XPDR-NordBrem')
        if success:
            print("successfully created service2")
        else:
            print("problem in creating service2")
            return False
        time.sleep(self.WAITING)
        success = self.getService(20, 50, service="service2")
        if success:
            print("successfully read service2 information")
        else:
            print("problem in reading info about service2")
            return False
        time.sleep(3)
        success = self.test2CheckConnections()
        if success:
            print("Roadm connections are valid")
        else:
            print("problem in Roadm connections")
            return False
        time.sleep(5)
        success = self.checkTopology(xpdr="XPDR-NordBrem", roadm="ROADM-Norden", roadmDeg="1")
        if success:
            print("Roadm topology is valid")
        else:
            print("problem in roadm topology")
            return False
        return True

    def test3(self):
        success = self.createService(serviceName='service23', serviceRate=100, nodeidA='XPDR-StutKarl',
                                     nodeidZ='XPDR-MannKarl')
        if success:
            print("successfully created service3")
        else:
            print("problem in creating service3")
            return False
        time.sleep(15)
        success = self.test3CheckConnections()
        if success:
            print("Roadm connections are valid")
        else:
            print("problem in Roadm connections")
            return False
        time.sleep(5)
        success = self.checkTopology(xpdr="XPDR-MannKarl", roadm="ROADM-Mannheim", roadmDeg="2")
        if success:
            print("Roadm topology is valid")
        else:
            print("problem in roadm topology")
            return False
        return True

    def clean(self):
        self.test3DeleteServices()

    def mountAll(self, delayBetweenMount=0):
        responses = []
        if self.config.isRemoteEnabled():
            idx = 0
            for sim in self.sims:
                client = self.getSdncClient(idx)
                response = client.mount(sim.name, sim.ip,
                                        sim.port, sim.username, sim.password)
                responses.append(response)
                idx += 1
                if delayBetweenMount > 0 and response.isSucceeded():
                    time.sleep(delayBetweenMount)
        else:
            for sim in self.sims:
                response = self.trpceClient.mount(sim.name, sim.ip,
                                                  sim.port, sim.username, sim.password)
                responses.append(response)
                if delayBetweenMount > 0 and response.isSucceeded():
                    time.sleep(delayBetweenMount)

        for r in responses:
            if not r.isSucceeded():
                return False
        return True

    def checkAutocreatedNetworksAfterMount(self, retries, delayForRetries):
        success = False
        xpdrs=[]
        rdms=[]

        for sim in self.sims:
            if 'XPDR-' in sim.name:
                xpdrs.append(sim.name)
            else:
                rdms.append(sim.name)
        otnTopology=[i + '-XPDR1' for i in xpdrs]
        print("total xpdrs {}".format(len(xpdrs)))
        print(print("total xpdrs in otnTopology {}".format(len(xpdrs))))
        openroadmNetwork=xpdrs+rdms
        openroadmTopology=[]
        for k in self.rdmInternalConfig.keys():
            for i in self.rdmInternalConfig[k]:
                openroadmTopology.append('ROADM-'+ k +'-DEG'+i )
                openroadmTopology.append('ROADM-' + k + '-SRG' + i)
        while retries >= 0:
            # connect_xprdA_N1_to_roadmA_PP1
            networkIds = self.trpceClient.getIetfNetworkIds()
            if networkIds is not None:
                success = (self.assertIn('otn-topology', networkIds) and
                           self.assertIn('clli-network', networkIds) and
                           self.assertIn('openroadm-topology', networkIds) and
                           self.assertIn('openroadm-network', networkIds))
            if success:
                success = (self.assertNodesInIetfNetwork('otn-topology',
                                                         otnTopology) and
                           self.assertNodesInIetfNetwork('openroadm-network',
                                                         openroadmNetwork) and
                           self.assertNodesInIetfNetwork('openroadm-topology',
                                                         openroadmTopology))
                if success:
                    break
            retries -= 1
            if retries >= 0:
                print("network autocreation not yet complete. waiting for retry...")
            else:
                break

            time.sleep(delayForRetries)
        return success

    def assertNodesInIetfNetwork(self, networkId, nodeIds):
        networkNodes = self.trpceClient.getIetfNetworkNodes(networkId)
        if networkNodes is None:
            print('unable to get network nodes for ' + networkId)
            return False

        for nodeId in nodeIds:
            if not self.assertIn(nodeId, networkNodes):
                print('node ' + nodeId + ' not found in network ' + networkId)
                return False

        return True

    def createLinks(self, retries=2, delayForRetries=10):
        success = False
        while retries >= 0:
            # connect_xprdA_N1_to_roadmA_PP1
            for k in self.rdmInternalConfig.keys():
                for i in self.rdmInternalConfig[k]:
                    response = self.trpceClient.linkXpdrToRoadm("XPDR-" + k[:4] + self.rdmInternalConfig[k][i][:4], "1", "1",
                                                                "ROADM-" + k, i, "SRG" + i + "-PP1-TXRX")
                    if response.isSucceeded():
                        # connect_roadmA_PP1_to_xpdrA_N1
                        response = self.trpceClient.linkRoadmTpXpdr("XPDR-" + k[:4] + self.rdmInternalConfig[k][i][:4], "1", "1",
                                                                    "ROADM-" + k, i, "SRG" + i + "-PP1-TXRX")
                        if response.isSucceeded():
                            continue


            retries -= 1
            success = response.isSucceeded()
            if success:
                break
            if retries > 0:
                print("creating links failed. waiting for retry...")
            else:
                break

            time.sleep(delayForRetries)

        return success

    def createRoadmLinks(self, retries=2, delayForRetries=10):
        success = False

        while retries >= 0:
            # connect_xprdA_N1_to_roadmA_PP1
            response = self.trpceClient.linkRoadmToRoadm("ROADM-TEST", "1", "DEG1-TTP-TXRX",
                                                         "ROADM-A1", "2", "DEG2-TTP-TXRX")
            if response.isSucceeded():
                # connect_roadmA_PP1_to_xpdrA_N1
                response = self.trpceClient.linkRoadmToRoadm("ROADM-A1", "2", "DEG2-TTP-TXRX",
                                                             "ROADM-TEST", "1", "DEG1-TTP-TXRX")
                if response.isSucceeded():
                    # connect_roadmA_PP1_to_xpdrA_N1
                    response = self.trpceClient.linkRoadmToRoadm("ROADM-TEST", "2", "DEG2-TTP-TXRX",
                                                                 "ROADM-C1", "1", "DEG1-TTP-TXRX")
                    if response.isSucceeded():
                        # connect_roadmA_PP1_to_xpdrA_N1
                        response = self.trpceClient.linkRoadmToRoadm("ROADM-C1", "1", "DEG1-TTP-TXRX",
                                                                     "ROADM-TEST", "2", "DEG2-TTP-TXRX")
                        if response.isSucceeded():
                            # connect_xprdC_N1_to_roadmC_PP1
                            response = self.trpceClient.linkRoadmToRoadm("ROADM-A1", "1", "DEG1-TTP-TXRX",
                                                                         "ROADM-B1", "1", "DEG1-TTP-TXRX")
                            if response.isSucceeded():
                                # connect_roadmC_PP1_to_xpdrC_N1
                                response = self.trpceClient.linkRoadmToRoadm("ROADM-B1", "1", "DEG1-TTP-TXRX",
                                                                             "ROADM-A1", "1", "DEG1-TTP-TXRX")
                                if response.isSucceeded():
                                    # connect_roadmC_PP1_to_xpdrC_N1
                                    response = self.trpceClient.linkRoadmToRoadm("ROADM-B1", "2", "DEG2-TTP-TXRX",
                                                                                 "ROADM-C1", "2", "DEG2-TTP-TXRX")
                                    if response.isSucceeded():
                                        # connect_roadmC_PP1_to_xpdrC_N1
                                        response = self.trpceClient.linkRoadmToRoadm("ROADM-C1", "2", "DEG2-TTP-TXRX",
                                                                                     "ROADM-B1", "2", "DEG2-TTP-TXRX")
            retries -= 1
            success = response.isSucceeded()
            if success:
                break
            if retries > 0:
                print("creating links failed. waiting for retry...")
            else:
                break

            time.sleep(delayForRetries)

        return success

    def configROADMS(self, spanlossBase=11.4, spanlossCurrent=12, spanlossEngineered=12.2):
        # add_omsAttributes_ROADMA_ROADMC
        # Config ROADMA-ROADMC oms-attributes
        data = {
            "span": {
                "auto-spanloss": "true",
                "spanloss-base": spanlossBase,
                "spanloss-current": spanlossCurrent,
                "engineered-spanloss": spanlossEngineered,
                "link-concatenation": [{
                    "SRLG-Id": 0,
                    "fiber-type": "smf",
                    "SRLG-length": 100000,
                    "pmd": 0.5
                }]
            }
        }
        for k in self.rdmInternalConfig.keys():
            for i in self.rdmInternalConfig[k]:
                response = self.trpceClient.addOmsAttributes(
                    "ROADM-" + k  + "-DEG" + i + "-DEG" + i + "-TTP-TXRXtoROADM-" + self.rdmInternalConfig[k][
                        i] + "-DEG" + list(self.rdmInternalConfig[self.rdmInternalConfig[k][i]].keys())[
                        list(self.rdmInternalConfig[self.rdmInternalConfig[k][i]].values()).index(
                            k)] + "-DEG" + list(self.rdmInternalConfig[self.rdmInternalConfig[k][i]].keys())[
                        list(self.rdmInternalConfig[self.rdmInternalConfig[k][i]].values()).index(
                            k)] + "-TTP-TXRX", data)
                if not response.isSucceeded():
                    return False
        return True

    def createService(self, serviceName='service1', serviceRate=100, nodeidA='XPDR-BerlHamb', nodeidZ='XPDR-HambBerl'):
        # create_eth_service1
        #create_eth_service1
        data = {
            "input": {
                "sdnc-request-header": {
                    "request-id":
                    "e3028bae-a90f-4ddd-a83f-cf224eba0e58",
                    "rpc-action":
                    "service-create",
                    "request-system-id":
                    "appname",
                    "notification-url":
                    "http://localhost:8585/NotificationServer/notify"
                },
                "service-name": serviceName,
                "common-id": "ASATT1234567",
                "connection-type": "service",
                "service-a-end": {
                    "service-rate": str(serviceRate),
                    "node-id": nodeidA,
                    "service-format": "Ethernet",
                    "clli": "SNJSCAMCJP8",
                    "tx-direction": {
                        "port": {
                            "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00" if self.isAluminium else nodeidA,
                            "port-type": "router",
                            "port-name": "Gigabit Ethernet_Tx.ge-5/0/0.0",
                            "port-rack": "000000.00",
                            "port-shelf": "00"
                        },
                        "lgx": {
                            "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
                            "lgx-port-name": "LGX Back.3",
                            "lgx-port-rack": "000000.00",
                            "lgx-port-shelf": "00"
                        }
                    },
                    "rx-direction": {
                        "port": {
                            "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00" if self.isAluminium else nodeidA,
                            "port-type": "router",
                            "port-name": "Gigabit Ethernet_Rx.ge-5/0/0.0",
                            "port-rack": "000000.00",
                            "port-shelf": "00"
                        },
                        "lgx": {
                            "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
                            "lgx-port-name": "LGX Back.4",
                            "lgx-port-rack": "000000.00",
                            "lgx-port-shelf": "00"
                        }
                    },
                    "optic-type": "gray"
                },
                "service-z-end": {
                    "service-rate": str(serviceRate),
                    "node-id": nodeidZ,
                    "service-format": "Ethernet",
                    "clli": "SNJSCAMCJT4",
                    "tx-direction": {
                        "port": {
                            "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00" if self.isAluminium else nodeidZ,
                            "port-type": "router",
                            "port-name": "Gigabit Ethernet_Tx.ge-1/0/0.0",
                            "port-rack": "000000.00",
                            "port-shelf": "00"
                        },
                        "lgx": {
                            "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
                            "lgx-port-name": "LGX Back.29",
                            "lgx-port-rack": "000000.00",
                            "lgx-port-shelf": "00"
                        }
                    },
                    "rx-direction": {
                        "port": {
                            "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00" if self.isAluminium else nodeidZ,
                            "port-type": "router",
                            "port-name": "Gigabit Ethernet_Rx.ge-1/0/0.0",
                            "port-rack": "000000.00",
                            "port-shelf": "00"
                        },
                        "lgx": {
                            "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
                            "lgx-port-name": "LGX Back.30",
                            "lgx-port-rack": "000000.00",
                            "lgx-port-shelf": "00"
                        }
                    },
                    "optic-type": "gray"
                },
                "due-date": "2016-11-28T00:00:01Z",
                "operator-contact": "pw1234"
                }
            }
        response = self.trpceClient.createService(data)
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False

        success = self.assertIn('PCE calculation in progress',
                                response.data['output']['configuration-response-common']['response-message'])

        return success

    def getService(self, retries=1, delayForRetries=10,service='service1'):

        while retries > 0:
            success = False
            response = self.trpceClient.getService(service)
            if response.isSucceeded():

                success = self.assertEqual(
                    response.data['services'][0]['administrative-state'], 'inService')
                if not success and retries > 0:
                    print("service still not with administrative-state inService (state=" +
                          response.data['services'][0]['administrative-state'] + "). waiting...")
                success &= self.assertEqual(
                    response.data['services'][0]['service-name'], service)
                success &= self.assertEqual(
                    response.data['services'][0]['connection-type'], 'service')
                success &= self.assertEqual(
                    response.data['services'][0]['lifecycle-state'], 'planned')
                if not success and retries > 0:
                    print("service still not with lifecycle-state inServerice (state=" + response.data['services'][0][
                        'lifecycle-state'] + "). waiting...")

                if success:
                    break
            else:
                print("service not available: responsecode: " + str(response.code))
            retries -= 1
            time.sleep(delayForRetries)
            if retries > 0:
                print("service still not with state inServerice. waiting...")

        if not success:
            self.logError(str(response.code) + " | " + response.content)
            return False
        return True

    def checkConnections(self):
        # check_xc1_ROADMA
        # check_xc1_ROADMA
        jsonNamespace = 'org-openroadm-device:'
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Ulm",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Ulm",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data['org-openroadm-device:org-openroadm-device']['roadm-connections']
        for c in a:
            if c["connection-name"].startswith("SRG1-PP1-TXRX-DEG1-TTP-TXRX"):
                connectionId = c["connection-name"]
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Ulm",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Ulm",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))
        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data['org-openroadm-device:roadm-connections'][0]),
            response.data['org-openroadm-device:roadm-connections'][0]
        )
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^SRG.-PP1-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not success:
            return False

        # check_xc1_ROADMC
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Duesseldorf",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Duesseldorf",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data[jsonNamespace + 'org-openroadm-device']['roadm-connections']
        for c in a:
            if c["connection-name"].startswith("DEG2-TTP-TXRX-DEG1-TTP-TXRX"):
                connectionId = c["connection-name"]

        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Duesseldorf",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                                   connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Duesseldorf",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))

        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data[jsonNamespace + 'roadm-connections'][0]),
            response.data[jsonNamespace + 'roadm-connections'][0]
        )

        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not response.isSucceeded():
            return False

        return True

    def checkTopology(self, xpdr, roadm, roadmDeg):
        # check_topo_XPDRA
        response = self.trpceClient.getOpenroadmTopology("node/" + xpdr + "-XPDR1")
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            print(ele)
            if ele['tp-id'] == 'XPDR1-NETWORK1':
                success = self.assertEqual({u'frequency': 196.1,
                                            u'width': 40},
                                           ele['org-openroadm-network-topology:xpdr-network-attributes']['wavelength'])
            if ele['tp-id'] == 'XPDR1-CLIENT2' or ele['tp-id'] == 'XPDR1-CLIENT1':
                success &= self.assertNotIn(
                    'org-openroadm-network-topology:xpdr-client-attributes', dict.keys(ele))
            if ele['tp-id'] == 'XPDR1-NETWORK2':
                success &= self.assertNotIn(
                    'org-openroadm-network-topology:xpdr-network-attributes', dict.keys(ele))
            if not success:
                self.logError("problem with tp elem in "+ xpdr + "-XPDR1: " + json.dumps(ele))
                return False
        time.sleep(1)
        # check_topo_ROADMA_SRG1
        response = self.trpceClient.getOpenroadmTopology("node/"+ roadm + "-SRG" + roadmDeg)
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False
        freq_map = base64.b64decode(
            response.data['node'][0]['org-openroadm-network-topology:srg-attributes']['avail-freq-maps'][0]['freq-map'])
        freq_map_array = [int(x) for x in freq_map]
        success = self.assertEqual(freq_map_array[95], 0, "Index 1 should not be available")
        if not success:
            return False
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            if ele['tp-id'] == 'SRG'+ roadmDeg + '-PP1-TXRX':
                freq_map = base64.b64decode(
                    ele['org-openroadm-network-topology:pp-attributes']['avail-freq-maps'][0]['freq-map'])
                freq_map_array = [int(x) for x in freq_map]
                success &= self.assertEqual(freq_map_array[95], 0, "Index 1 should not be available")
            if ele['tp-id'] == 'SRG'+ roadmDeg + '-PP2-TXRX':
                success &= self.assertNotIn('avail-freq-maps', dict.keys(ele))
            if not success:
                return False

        time.sleep(1)
        # check_topo_ROADMA_DEG1
        response = self.trpceClient.getOpenroadmTopology("node/" + roadm + "-DEG" + roadmDeg)
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False
        freq_map = base64.b64decode(
            response.data['node'][0]['org-openroadm-network-topology:degree-attributes']['avail-freq-maps'][0][
                'freq-map'])
        freq_map_array = [int(x) for x in freq_map]
        self.assertEqual(freq_map_array[95], 0, "Index 1 should not be available")
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            if ele['tp-id'] == 'DEG' + roadmDeg + '-CTP-TXRX':
                freq_map = base64.b64decode(
                    ele['org-openroadm-network-topology:ctp-attributes']['avail-freq-maps'][0]['freq-map'])
                freq_map_array = [int(x) for x in freq_map]
                success &= self.assertEqual(freq_map_array[95], 0, "Index 1 should not be available")
            if ele['tp-id'] == 'DEG' + roadmDeg + '-TTP-TXRX':
                freq_map = base64.b64decode(
                    ele['org-openroadm-network-topology:tx-ttp-attributes']['avail-freq-maps'][0]['freq-map'])
                freq_map_array = [int(x) for x in freq_map]
                success &= self.assertEqual(freq_map_array[95], 0, "Index 1 should not be available")
            if not success:
                return False

        return True

    # def test2CreateConnections(self, retries=2, delayForRetries=10):
    #     success = False
    #     while retries >= 0:
    #         # connect_xprdA_N2_to_roadmA_PP2
    #         response = self.trpceClient.linkXpdrToRoadm("XPDR-A1", "1", "2",
    #                                                     "ROADM-A1", "1", "SRG1-PP2-TXRX")
    #         if response.isSucceeded():
    #             # connect_roadmA_PP2_to_xpdrA_N2
    #             response = self.trpceClient.linkRoadmTpXpdr("XPDR-A1", "1", "2",
    #                                                         "ROADM-A1", "1", "SRG1-PP2-TXRX")
    #             if response.isSucceeded():
    #                 # connect_xprdC_N2_to_roadmC_PP2
    #                 response = self.trpceClient.linkXpdrToRoadm("XPDR-C1", "1", "2",
    #                                                             "ROADM-C1", "1", "SRG1-PP2-TXRX")
    #                 if response.isSucceeded():
    #                     # connect_roadmC_PP2_to_xpdrC_N2
    #                     response = self.trpceClient.linkRoadmTpXpdr("XPDR-C1", "1", "2",
    #                                                                 "ROADM-C1", "1", "SRG1-PP2-TXRX")
    #                     if response.isSucceeded():
    #                         # connect_xprdC_N2_to_roadmC_PP2
    #                         response = self.trpceClient.linkXpdrToRoadm("XPDR-B1", "1", "2",
    #                                                                     "ROADM-B1", "1", "SRG1-PP2-TXRX")
    #                         if response.isSucceeded():
    #                             # connect_roadmC_PP2_to_xpdrC_N2
    #                             response = self.trpceClient.linkRoadmTpXpdr("XPDR-B1", "1", "2",
    #                                                                         "ROADM-B1", "1", "SRG1-PP2-TXRX")
    #         retries -= 1
    #         success = response.isSucceeded()
    #         if success:
    #             break
    #         if retries > 0:
    #             print("creating links failed. waiting for retry...")
    #         else:
    #             break
    #
    #         time.sleep(delayForRetries)
    #
    #     return success
    #
    # def test2CreateService(self):
    #     # create_eth_service2
    #     data = {"input": {
    #         "sdnc-request-header": {
    #             "request-id": "e3028bae-a90f-4ddd-a83f-cf224eba0e58",
    #             "rpc-action": "service-create",
    #             "request-system-id": "appname",
    #             "notification-url": "http://localhost:8585/NotificationServer/notify"
    #         },
    #         "service-name": "service2",
    #         "common-id": "ASATT1234567",
    #         "connection-type": "service",
    #         "service-a-end": {
    #             "service-rate": "100",
    #             "node-id": "XPDR-A1",
    #             "service-format": "Ethernet",
    #             "clli": "SNJSCAMCJP8",
    #             "tx-direction": {
    #                 "port": {
    #                     "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00",
    #                     "port-type": "router",
    #                     "port-name": "Gigabit Ethernet_Tx.ge-5/0/0.0",
    #                     "port-rack": "000000.00",
    #                     "port-shelf": "00"
    #                 },
    #                 "lgx": {
    #                     "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
    #                     "lgx-port-name": "LGX Back.3",
    #                     "lgx-port-rack": "000000.00",
    #                     "lgx-port-shelf": "00"
    #                 }
    #             },
    #             "rx-direction": {
    #                 "port": {
    #                     "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00",
    #                     "port-type": "router",
    #                     "port-name": "Gigabit Ethernet_Rx.ge-5/0/0.0",
    #                     "port-rack": "000000.00",
    #                     "port-shelf": "00"
    #                 },
    #                 "lgx": {
    #                     "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
    #                     "lgx-port-name": "LGX Back.4",
    #                     "lgx-port-rack": "000000.00",
    #                     "lgx-port-shelf": "00"
    #                 }
    #             },
    #             "optic-type": "gray"
    #         },
    #         "service-z-end": {
    #             "service-rate": "100",
    #             "node-id": "XPDR-C1",
    #             "service-format": "Ethernet",
    #             "clli": "SNJSCAMCJT4",
    #             "tx-direction": {
    #                 "port": {
    #                     "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00",
    #                     "port-type": "router",
    #                     "port-name": "Gigabit Ethernet_Tx.ge-1/0/0.0",
    #                     "port-rack": "000000.00",
    #                     "port-shelf": "00"
    #                 },
    #                 "lgx": {
    #                     "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
    #                     "lgx-port-name": "LGX Back.29",
    #                     "lgx-port-rack": "000000.00",
    #                     "lgx-port-shelf": "00"
    #                 }
    #             },
    #             "rx-direction": {
    #                 "port": {
    #                     "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00",
    #                     "port-type": "router",
    #                     "port-name": "Gigabit Ethernet_Rx.ge-1/0/0.0",
    #                     "port-rack": "000000.00",
    #                     "port-shelf": "00"
    #                 },
    #                 "lgx": {
    #                     "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
    #                     "lgx-port-name": "LGX Back.30",
    #                     "lgx-port-rack": "000000.00",
    #                     "lgx-port-shelf": "00"
    #                 }
    #             },
    #             "optic-type": "gray"
    #         },
    #         "due-date": "2016-11-28T00:00:01Z",
    #         "operator-contact": "pw1234"
    #     }
    #     }
    #     response = self.trpceClient.createService(data)
    #     if not response.isSucceeded():
    #         self.logError(str(response.code) + " | " + response.content)
    #         return False
    #
    #     success = self.assertIn('PCE calculation in progress',
    #                             response.data['output']['configuration-response-common']['response-message'])
    #
    #     return success
    #
    # def test2GetService(self, retries=1, delayForRetries=10):
    #     # get_eth_service2
    #     while retries > 0:
    #         success = False
    #         response = self.trpceClient.getService('service2')
    #         if response.isSucceeded():
    #             print("getting service2")
    #             success = self.assertEqual(
    #                 response.data['services'][0]['administrative-state'], 'inService')
    #             if not success and retries > 0:
    #                 print("service still not with administrative-state inServerice (state=" +
    #                       response.data['services'][0]['administrative-state'] + "). waiting...")
    #             success &= self.assertEqual(
    #                 response.data['services'][0]['service-name'], 'service2')
    #             success &= self.assertEqual(
    #                 response.data['services'][0]['connection-type'], 'service')
    #             success &= self.assertEqual(
    #                 response.data['services'][0]['lifecycle-state'], 'planned')
    #             if not success and retries > 0:
    #                 print("service still not with lifecycle-state inServerice (state=" + response.data['services'][0][
    #                     'lifecycle-state'] + "). waiting...")
    #
    #             if success:
    #                 break
    #         else:
    #             print("service not available: responsecode: " + str(response.code))
    #         retries -= 1
    #         time.sleep(delayForRetries)
    #         if retries > 0:
    #             print("service still not with state inServerice. waiting...")
    #
    #     if not success:
    #         self.logError(str(response.code) + " | " + response.content)
    #         return False
    #     return True

    def test2CheckConnections(self):
        # check_xc2_ROADMA
        jsonNamespace = 'org-openroadm-device:'
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Norden",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Norden",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data['org-openroadm-device:org-openroadm-device']['roadm-connections']
        print(a)
        for c in a:
            if c["connection-name"].startswith("SRG1-PP1-TXRX-DEG1-TTP-TXRX"):
                connectionId = c["connection-name"]
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Norden",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                                   connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Norden",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))
        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data['org-openroadm-device:roadm-connections'][0]),
            response.data['org-openroadm-device:roadm-connections'][0]
        )
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^SRG.-PP1-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not success:
            return False

        # check_xc1_ROADMC
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Bremen",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Bremen",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data[jsonNamespace + 'org-openroadm-device']['roadm-connections']
        print(a)
        for c in a:
            if c["connection-name"].startswith("DEG1-TTP-TXRX-DEG3-TTP-TXRX"):
                connectionId = c["connection-name"]

        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Bremen",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                                   connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Bremen",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))

        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data[jsonNamespace + 'roadm-connections'][0]),
            response.data[jsonNamespace + 'roadm-connections'][0]
        )

        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not response.isSucceeded():
            return False

        return True

    def test3CheckConnections(self):
        # check_xc2_ROADMA
        jsonNamespace = 'org-openroadm-device:'
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Mannheim",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Mannheim",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data['org-openroadm-device:org-openroadm-device']['roadm-connections']
        print(a)
        for c in a:
            if c["connection-name"].startswith("SRG2-PP1-TXRX-DEG2-TTP-TXRX"):
                connectionId = c["connection-name"]
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Mannheim",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                                   connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Mannheim",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))
        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data['org-openroadm-device:roadm-connections'][0]),
            response.data['org-openroadm-device:roadm-connections'][0]
        )
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^SRG.-PP1-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not success:
            return False

        # check_xc1_ROADMC
        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Karlsruhe",
                                                               "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        else:
            response = self.trpceClient.getNodeData("ROADM-Karlsruhe",
                                                    "/org-openroadm-device:org-openroadm-device?fields=roadm-connections")
        if not response.isSucceeded():
            return False
        connectionId = None
        a = response.data[jsonNamespace + 'org-openroadm-device']['roadm-connections']
        print(a)
        for c in a:
            if c["connection-name"].startswith("DEG1-TTP-TXRX-DEG2-TTP-TXRX"):
                connectionId = c["connection-name"]

        if self.config.isRemoteEnabled():
            response = self.getSdncClient(0, True).getNodeData("ROADM-Karlsruhe",
                                                               "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                                   connectionId))
        else:
            response = self.trpceClient.getNodeData("ROADM-Karlsruhe",
                                                    "/org-openroadm-device:org-openroadm-device/roadm-connections=" + self.urlencode(
                                                        connectionId))

        if not response.isSucceeded():
            return False
        # the following statement replaces self.assertDictContainsSubset deprecated in python 3.2
        success = self.assertDictEqual(
            dict({
                'connection-name': connectionId,
                'opticalControlMode': 'gainLoss',
                'target-output-power': -3.0
            }, **response.data[jsonNamespace + 'roadm-connections'][0]),
            response.data[jsonNamespace + 'roadm-connections'][0]
        )

        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['source']['src-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')
        success &= self.testString(response.data[jsonNamespace + 'roadm-connections'][0]['destination']['dst-if'],
                                   r'^DEG.-TTP-TXRX-nmc.*')

        if not response.isSucceeded():
            return False

        return True

    # creation service test on a non-available resource
    def test3CreateSerice3(self):
        # create_eth_service3
        time.sleep(self.WAITING)
        # add a test that check the openroadm-service-list still only contains 2 elements
        # delete_eth_service3
        data = {"input": {
            "sdnc-request-header": {
                "request-id": "e3028bae-a90f-4ddd-a83f-cf224eba0e58",
                "rpc-action": "service-create",
                "request-system-id": "appname",
                "notification-url": "http://localhost:8585/NotificationServer/notify"
            },
            "service-name": "service3",
            "common-id": "ASATT1234567",
            "connection-type": "service",
            "service-a-end": {
                "service-rate": "100",
                "node-id": "XPDR-A1",
                "service-format": "Ethernet",
                "clli": "SNJSCAMCJP8",
                "tx-direction": {
                    "port": {
                        "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00",
                        "port-type": "router",
                        "port-name": "Gigabit Ethernet_Tx.ge-5/0/0.0",
                        "port-rack": "000000.00",
                        "port-shelf": "00"
                    },
                    "lgx": {
                        "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
                        "lgx-port-name": "LGX Back.3",
                        "lgx-port-rack": "000000.00",
                        "lgx-port-shelf": "00"
                    }
                },
                "rx-direction": {
                    "port": {
                        "port-device-name": "ROUTER_SNJSCAMCJP8_000000.00_00",
                        "port-type": "router",
                        "port-name": "Gigabit Ethernet_Rx.ge-5/0/0.0",
                        "port-rack": "000000.00",
                        "port-shelf": "00"
                    },
                    "lgx": {
                        "lgx-device-name": "LGX Panel_SNJSCAMCJP8_000000.00_00",
                        "lgx-port-name": "LGX Back.4",
                        "lgx-port-rack": "000000.00",
                        "lgx-port-shelf": "00"
                    }
                },
                "optic-type": "gray"
            },
            "service-z-end": {
                "service-rate": "100",
                "node-id": "XPDR-C1",
                "service-format": "Ethernet",
                "clli": "SNJSCAMCJT4",
                "tx-direction": {
                    "port": {
                        "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00",
                        "port-type": "router",
                        "port-name": "Gigabit Ethernet_Tx.ge-1/0/0.0",
                        "port-rack": "000000.00",
                        "port-shelf": "00"
                    },
                    "lgx": {
                        "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
                        "lgx-port-name": "LGX Back.29",
                        "lgx-port-rack": "000000.00",
                        "lgx-port-shelf": "00"
                    }
                },
                "rx-direction": {
                    "port": {
                        "port-device-name": "ROUTER_SNJSCAMCJT4_000000.00_00",
                        "port-type": "router",
                        "port-name": "Gigabit Ethernet_Rx.ge-1/0/0.0",
                        "port-rack": "000000.00",
                        "port-shelf": "00"
                    },
                    "lgx": {
                        "lgx-device-name": "LGX Panel_SNJSCAMCJT4_000000.00_00",
                        "lgx-port-name": "LGX Back.30",
                        "lgx-port-rack": "000000.00",
                        "lgx-port-shelf": "00"
                    }
                },
                "optic-type": "gray"
            },
            "due-date": "2016-11-28T00:00:01Z",
            "operator-contact": "pw1234"
        }
        }
        response = self.trpceClient.createService(data)
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False

        success = self.assertIn('PCE calculation in progress',
                                response.data['output']['configuration-response-common']['response-message'])

        return success

    def deleteService(self, serviceName):
        data = {"input": {
            "sdnc-request-header": {
                "request-id": "e3028bae-a90f-4ddd-a83f-cf224eba0e58",
                "rpc-action": "service-delete",
                "request-system-id": "appname",
                "notification-url": "http://localhost:8585/NotificationServer/notify"
            },
            "service-delete-req-info": {
                "service-name": serviceName,
                "tail-retention": "no"
            }
        }
        }
        response = self.trpceClient.deleteService(data)
        if not response.isSucceeded():
            self.logError(str(response.code) + " | " + response.content)
            return False
        while response.data['output']['configuration-response-common']['response-code'] == '200':
            time.sleep(self.WAITING)
            response = self.trpceClient.deleteService(data)
            print("Renderer service delete in progress")

        success = self.assertIn('Service \'' + serviceName + '\' does not exist in datastore',
                                response.data['output']['configuration-response-common'][
                                    'response-message']) and self.assertIn('500',
                                                                           response.data['output'][
                                                                               'configuration-response-common'][
                                                                               'response-code'])

        if not success:
            self.logError("problem with deleting " + serviceName + ": " + json.dumps(response.data))
            return False
        return True

    def test3DeleteServices(self):
        success = self.deleteService("service3")
        if not success:
            return False
        time.sleep(self.WAITING)
        # delete_eth_service1
        success = self.deleteService("service1")
        if not success:
            return False
        time.sleep(self.WAITING)
        # delete_eth_service2(self):
        success = self.deleteService("service2")
        if not success:
            return False
        return True

    def test3_check_no_xc_ROADMA(self):
        """
        if self.config.isRemoteEnabled():
            response = self.sdncClient.getNodeData("ROADM-A1", "/org-openroadm-device:org-openroadm-device")
        else:
            response = self.trpceClient.getNodeData("ROADM-A1", "/org-openroadm-device:org-openroadm-device") 
        if not response.isSucceeded():
            self.logError(str(response.code)+" | "+response.content)
            return False
        success = self.assertNotIn('roadm-connections',
                 dict.keys(response.data['org-openroadm-device']))
        return success
        """
        return True

    def test3CheckTopology(self):
        """
        #check_topo_XPDRA
        response = self.trpceClient.getOpenroadmTopology("node/XPDR-A1-XPDR1")
        if not response.isSucceeded():
            self.logError(str(response.code)+" | "+response.content)
            return False
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            if ele[u'org-openroadm-common-network:tp-type'] == 'XPONDER-CLIENT':
                success = self.assertNotIn('org-openroadm-network-topology:xpdr-client-attributes', dict.keys(ele))
                print("success XPONDER-CLIENT",success)
            elif (ele[u'org-openroadm-common-network:tp-type'] == 'XPONDER-NETWORK'):
                success = self.assertIn(u'tail-equipment-id', dict.keys(ele[u'org-openroadm-network-topology:xpdr-network-attributes'])) and self.assertNotIn('wavelength', dict.keys(ele[u'org-openroadm-network-topology:xpdr-network-attributes']))
                print("success XPONDER-NETWORK",success)
            if not success:
                self.logError("problem with tp elem in XPDR-A1-XPDR1: "+json.dumps(ele))
                return False
        time.sleep(20)
        #check_topo_ROADMA_SRG1
        response = self.trpceClient.getOpenroadmTopology("node/ROADM-A1-SRG1")
        if not response.isSucceeded():
            self.logError(str(response.code)+" | "+response.content)
            return False
        self.assertIn({u'index': 1}, response.data['node'][0]
                      [u'org-openroadm-network-topology:srg-attributes']['available-wavelengths'])
        self.assertIn({u'index': 2}, response.data['node'][0]
                      [u'org-openroadm-network-topology:srg-attributes']['available-wavelengths'])
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            if ele['tp-id'] == 'SRG1-PP1-TXRX' or ele['tp-id'] == 'SRG1-PP1-TXRX':
                success = self.assertNotIn(
                    'org-openroadm-network-topology:pp-attributes', dict.keys(ele))
            else:
                success = self.assertNotIn(
                    'org-openroadm-network-topology:pp-attributes', dict.keys(ele))
            if not success:
                self.logError("problem with tp elem in ROADM-A1-SRG1: "+json.dumps(ele))
                return False

        time.sleep(10)

        #check_topo_ROADMA_DEG2
        response = self.trpceClient.getOpenroadmTopology("node/ROADM-A1-DEG2")
        if not response.isSucceeded():
            self.logError(str(response.code)+" | "+response.content)
            return False
        self.assertIn({u'index': 1}, response.data['node'][0]
                      [u'org-openroadm-network-topology:degree-attributes']['available-wavelengths'])
        self.assertIn({u'index': 2}, response.data['node'][0]
                      [u'org-openroadm-network-topology:degree-attributes']['available-wavelengths'])
        liste_tp = response.data['node'][0]['ietf-network-topology:termination-point']
        for ele in liste_tp:
            success = True
            if ele['tp-id'] == 'DEG2-CTP-TXRX':
                success = self.assertNotIn(
                    'org-openroadm-network-topology:ctp-attributes', dict.keys(ele))
            if ele['tp-id'] == 'DEG2-TTP-TXRX':
                success = self.assertNotIn(
                    'org-openroadm-network-topology:tx-ttp-attributes', dict.keys(ele))
            if not success:
                self.logError("problem with tp elem in ROADM-A1-DEG2: "+json.dumps(ele))
                return False
        """
        return True
