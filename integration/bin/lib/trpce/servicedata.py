
class TransportPceServiceDataTransmitDirection:

    def getMap(self):
        return {
            "port": {
                "port-device-name": "<xpdr-client-port>",
                "port-type": "fixed",
                "port-name": "<xpdr-client-port-number>",
                "port-rack": "000000.00",
                "port-shelf": "Chassis#1"
            },
            "lgx": {
                "lgx-device-name": "Some lgx-device-name",
                "lgx-port-name": "Some lgx-port-name",
                "lgx-port-rack": "000000.00",
                "lgx-port-shelf": "00"
            }
        }

class TransportPceServiceEnd:

    def __init__(self, nodeId, clliName):
        self.xpdrNodeId = nodeId
        self.clliName = clliName
        self.rxDir= TransportPceServiceDataTransmitDirection()
        self.txDir = TransportPceServiceDataTransmitDirection()

    def getMap(self):
        return {
            "service-rate": "100",
            "node-id": self.xpdrNodeId,
            "service-format": "Ethernet",
            "clli": self.clliName,
            "tx-direction": self.txDir.getMap(),
            "rx-direction": self.rxDir.getMap(),
            "optic-type": "gray"
        }


class TransportPceServiceData:

    def __init__(self, serviceName):
        self.serviceName = serviceName
        self.commonId = "commonId"
        self.connectionType = "service"
        self.serivceEndA = None
        self.serviceEndZ = None
        self.dueDate = "yyyy-mm-ddT00:00:01Z"
        self.contact = "some-contact-info"

    def getMap(self):

        return {
            "service-name": self.serviceName,
            "common-id": self.commonId,
            "connection-type": self.connectionType,
            "service-a-end": self.serivceEndA.getMap(),
            "service-z-end": self.serviceEndZ.getMap(),
            "due-date": self.dueDate,
            "operator-contact": self.contact
        }
