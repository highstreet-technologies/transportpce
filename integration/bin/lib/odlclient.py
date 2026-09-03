import base64
import json

import urllib3

urllib3.disable_warnings()
class OdlResponse:

    def __init__(self, r):
        self.code = r.status
        self.content = r.data.decode('utf-8')
        try:
            self.data = json.loads(self.content)
        except:
            self.data={}

    def isSucceeded(self):
        return self.code>=200 and self.code<300

    def sourceNotFound(self):
        return self.code==404

URI_CONFIG_NETCONF_TOPO = "/rests/data/network-topology:network-topology/topology=topology-netconf/"

class OdlClient:

    def __init__(self, baseUrl, username, password):

        self.baseUrl = baseUrl
        self.username = username
        self.password = password
        self.defaultJsonHeaders = dict()
        self.defaultJsonHeaders['Content-Type']='application/json'
        self.defaultJsonHeaders['Accept']='application/json'
        self.defaultJsonHeaders['Authorization']="Basic "+str(base64.b64encode((username+":"+password).encode('utf-8')),'utf-8')
        self.primarySource = False

    def requestRest(self, uri, method, headers=dict(), data=None):
        http = urllib3.PoolManager(cert_reqs='CERT_NONE')
        r = None
        if data == None:
            r = http.request(method, self.baseUrl+uri, headers=headers)
        else:
            if type(data) is dict:
                data = json.dumps(data)
            encoded_data = data.encode('utf-8')
            r = http.request(method, self.baseUrl+uri,
                             body=encoded_data, headers=headers)
        return OdlResponse(r)

    def mount(self, name, ip, port, username, password):
        data = dict()
        node = dict()
        data["node"] = node
        node["node-id"] = name
        node["port"] = port
        node["host"] = ip
        node["username"] = username
        node["password"] = password
        node["tcp-only"] = False
        node["reconnect-on-changed-schema"] = False
        node["connection-timeout-millis"] = 20000
        node["max-connection-attempts"] = 100
        node["between-attempts-timeout-millis"] = 2000
        node["sleep-factor"] = 1.5
        node["keepalive-delay"] = 120
        payload = json.dumps(data)
        response = self.requestRest('/rests/data/network-topology:network-topology/topology=topology-netconf/node='+name,
                                    'PUT', self.defaultJsonHeaders, payload)
        print(name + " mounted "+str(response.code)+ " on "+self.baseUrl)
        if response.code>=400:
            print("with error:")
            print(response.content)
        return response

    def unmount(self, name):
        response = self.requestRest('/rests/data/network-topology:network-topology/topology=topology-netconf/node='+name,
                                    'DELETE', self.defaultJsonHeaders)
        print(name + " unmounted "+str(response.code))

    def neInfos(self, name):
        return self.requestRest('/rests/data/network-topology:network-topology/topology=topology-netconf/node='+name,
                                    'GET', self.defaultJsonHeaders)
    def neStatus(self, name):
        response = self.neInfos(name)
        if response.isSucceeded():
            connectionStatus="unknown"
            try:
                o=response.data
                connectionStatus=o["network-topology:node"][0]["netconf-node-topology:netconf-node"]["connection-status"]
            except (KeyError, IndexError, TypeError) as e:
                print(f"Error parsing connection-status for {name}: {type(e).__name__}: {e}")
            return connectionStatus
        elif response.sourceNotFound():
            return "unmounted"
        else:
            return "unknown"

    def isReady(self):
        try:
            response = self.requestRest('/ready','GET', self.defaultJsonHeaders)
        except:
            return False
        return response.isSucceeded()

    def getNodeData(self, node: str, suffix: str):

        return self.requestRest(URI_CONFIG_NETCONF_TOPO + "node=" + node + "/yang-ext:mount" + suffix,
        'GET',self.defaultJsonHeaders)

    def getNodeA1Data(self, url:str):
        return self.requestRest(url, 'GET', self.defaultJsonHeaders)

    def getIetfNetworks(self, filter=""):
        return self.requestRest('/rests/data/ietf-network:networks'+filter, 'GET', self.defaultJsonHeaders)

    def getIetfNetwork(self, networkId, filter=''):
        return self.getIetfNetworks('/network='+networkId+filter)

    def setPrimary(self, prim):
        self.primarySource = prim

    def isPrimary(self):
        return self.primarySource
