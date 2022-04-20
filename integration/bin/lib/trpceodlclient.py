import json

from .odlclient import OdlClient

URI_CONFIG_ORDM_TOPO = "/restconf/config/ietf-network:networks/network/openroadm-topology/"


class TrpceOdlClient(OdlClient):

    def __init__(self, baseUrl, username, password):
        OdlClient.__init__(self, baseUrl, username, password)

    def linkRoadmToRoadm(self, roadmANodeId, roadmADeg, roadmATermPoint, roadmZNodeId, roadmZDeg, roadmZTermPoint):
        data = {
            "input": {
                "rdm-a-node": roadmANodeId,
                "deg-a-num": roadmADeg,
                "termination-point-a": roadmATermPoint,
                "rdm-z-node": roadmZNodeId,
                "deg-z-num": roadmZDeg,
                "termination-point-z": roadmZTermPoint
            }
        }
        payload = json.dumps(data)
        response = self.requestRest('/rests/operations/transportpce-networkutils:init-roadm-nodes',
                                    'POST', self.defaultJsonHeaders, payload)
        print(roadmANodeId + " to "+ roadmZNodeId+" link "+str(response.code))
        return response
    
    def linkXpdrToRoadm(self, xpdrNode, xpdrNum, xpdrNetworkPortNumber, roadmNodeId, srgNumber, logicalConnectionPoint):
        data ={
            "input": {
                "links-input": {
                "xpdr-node": xpdrNode,
                "xpdr-num": xpdrNum,
                "network-num": xpdrNetworkPortNumber,
                "rdm-node": roadmNodeId,
                "srg-num": srgNumber,
                "termination-point-num": logicalConnectionPoint
                }
            }
        }
        payload = json.dumps(data)
        response = self.requestRest('/rests/operations/transportpce-networkutils:init-xpdr-rdm-links',
                                    'POST', self.defaultJsonHeaders, payload)
        print(xpdrNode + " to "+ roadmNodeId+" link "+str(response.code))
        return response
    
    
    def linkRoadmTpXpdr(self, xpdrNode, xpdrNum, xpdrNetworkPortNumber, roadmNodeId, srgNumber, logicalConnectionPoint):
        data ={
            "input": {
                "links-input": {
                "xpdr-node": xpdrNode,
                "xpdr-num": xpdrNum,
                "network-num": xpdrNetworkPortNumber,
                "rdm-node": roadmNodeId,
                "srg-num": srgNumber,
                "termination-point-num": logicalConnectionPoint
                }
            }
        }
        payload = json.dumps(data)
        response = self.requestRest('/rests/operations/transportpce-networkutils:init-rdm-xpdr-links',
                                    'POST', self.defaultJsonHeaders, payload)
        print(xpdrNode + " to "+ roadmNodeId+" link "+str(response.code))
        return response

    def addOmsAttributes(self, link: str, attr):
        uri = URI_CONFIG_ORDM_TOPO + (
            "ietf-network-topology:link/" + link + "/org-openroadm-network-topology:OMS-attributes/span"
        )
        response = self.requestRest(uri,
            'PUT', self.defaultJsonHeaders, attr)
        return response
    
    def createTopoLink(self, linkId, srcXpdrNodeId, srcXpdrNetworkPortId, dstXpdrNodeId, dstXpdrNetworkPortId):
        data={
            "ietf-network-topology:link": [
            {
                "link-id": linkId,
                "source": {
                    "source-node": srcXpdrNodeId,
                    "source-tp": srcXpdrNetworkPortId
                },
                "org-openroadm-common-network:link-type": "OTN-LINK",
                "destination": {
                    "dest-node": dstXpdrNodeId,
                    "dest-tp": dstXpdrNetworkPortId
                }
            }]
        }
        payload = json.dumps(data)
        response = self.requestRest('/rests/data/ietf-network:networks/network/otn-topology/ietf-network-topology:link/'+linkId,
                                    'POST', self.defaultJsonHeaders, payload)
        print("topolink from "+srcXpdrNodeId + " to "+ dstXpdrNodeId+" created "+str(response.code))
        return response

    def createService(self,serviceData):
       
        response = self.requestRest('/restconf/operations/org-openroadm-service:service-create',
            'POST',self.defaultJsonHeaders,serviceData)
   
        return response

    def getService(self, serviceName):
        uri = "/restconf/operational/org-openroadm-service:service-list/services/"+serviceName
        response = self.requestRest(uri,'GET',self.defaultJsonHeaders,None)
        return response

    def getServiceDetailed(self, serviceName):
        uri = "/rests/data/transportpce-service-path:service-path-list/service-paths={}".format(serviceName)
        response = self.requestRest(uri,'GET',self.defaultJsonHeaders,None)
        return response

    def getOpenroadmTopology(self, suffix):
        return self.requestRest(URI_CONFIG_ORDM_TOPO+suffix,'GET',self.defaultJsonHeaders)
        
    def deleteService(self, serviceData):
        response = self.requestRest('/restconf/operations/org-openroadm-service:service-delete',
            'POST', self.defaultJsonHeaders,serviceData)
        return response

    def getIetfNetworkIds(self):
        response = self.getIetfNetworks('?fields=network/network-id')
        list=None
        if response.isSucceeded():
            list=[]
            for n in response.data['ietf-network:networks']['network']:
                list.append(n['network-id'])

        else:
            print('unable to read ietf networks. response code '+response.code)
        return list
    
    def getIetfNetworkNodes(self, networkId):
        response = self.getIetfNetwork(networkId,'?fields=node/node-id')
        list=None
        if response.isSucceeded():
            if  'node' in response.data['ietf-network:network'][0]:
                list=[]
                for n in response.data['ietf-network:network'][0]['node']:
                    list.append(n['node-id'])
            else:
                print('no nodes found for '+networkId)
        else:
            print('unable to read ietf network nodes for '+networkId+'. response code '+str(response.code))
        return list
