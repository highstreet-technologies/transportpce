import json

class IntegrationTestSimProfile:


    def __init__(self) -> None:
        self.items=[]

    def addSimulator(self, nodeId, host="",port=830, username="netconf",password="netconf", container='' ):
        self.items.append({'node-id':nodeId,
            'host':host, 
            'port':port, 
            'username':username, 
            'password':password, 
            'container':container})
    def addLocalContainerSim(self, nodeId, containerName, port=830, username='netconf', password='netconf'):
        self.addSimulator(nodeId, port=port, username=username,password=password,container=containerName )

    def addRemoteSim(self, nodeId, host, port, username='netconf', password='netconf'):
        self.addSimulator(nodeId, host, port, username, password)

    def save(self, filename):
        with open(filename, 'w') as fp:
            json.dump(self.items,fp)


class IntegrationTestControllerProfile:


    def __init__(self) -> None:
        self.sdnrs=[]
        self.transportpce=None

    def addSdnr(self,scheme='http', host='sdnr', port=8181, username='$\{SDNR_USERNAME\}', password='$\{SDNR_PASSWORD\}', container='', primary=False):
        self.sdnrs.append({
            'scheme':scheme,
            'host':host,
            'port':port,
            'username':username,
            'password':password,
            'container':container,
            'primary':primary
        })
    def setTransportPCE(self, scheme='http', host='sdnr', port=8181, username='$\{SDNR_USERNAME\}', password='$\{SDNR_PASSWORD\}', container=''):
        self.transportpce={
            'scheme':scheme,
            'host':host,
            'port':port,
            'username':username,
            'password':password,
            'container':container
        }
    def save(self, filename):
        with open(filename, 'w') as fp:
            json.dump({'sdnr':self.sdnrs,'transportpce':self.transportpce},fp)