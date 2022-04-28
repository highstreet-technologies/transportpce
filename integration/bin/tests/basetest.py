import time;
import re
import urllib

class BaseTest:

    def __init__(self, sdncClients, primarySdncClient, trpceClient, sims, config):
        self.sdncClients = sdncClients
        self.primarySdncClient = primarySdncClient
        self.trpceClient = trpceClient
        self.sims = sims
        self.config = config

    def getSdncClient(self, idx, primary=False):
        if (primary and self.primarySdncClient!=None) or len(self.sdncClients) == 0:
            return self.primarySdncClient
        return self.sdncClients[idx % len(self.sdncClients)]

    def waitForReadyState(self, timeout=60):
        if self.config.doIgnoreReadyState():
            print("ignoring ready state by config")
            return True
        print('waiting for ready state',end='',flush=True)
        while timeout>0:
            print('.',end='',flush=True)
            if self.config.isRemoteEnabled():
                ready = self.trpceClient.isReady()
                if ready:
                    if self.primarySdncClient!=None:
                        ready= self.primarySdncClient.isReady()
                    for c in self.sdncClients:
                        ready &= c.isReady()
            else:
                ready = self.trpceClient.isReady()
            if ready:
                print('succeeded')
                return True
            timeout-=1
            time.sleep(1)
        print('failed')
        return False

    def waitForConnectedState(self, timeout=60):
        print('waiting for connected state',end='',flush=True)
        while timeout>0:
            print('.',end='',flush=True)
            allConnected = True
            idx=0
            for sim in self.sims:
                connected = False
                if self.config.isRemoteEnabled():
                    client=self.getSdncClient(idx,self.primarySdncClient!=None)
                    connected = client.neStatus(sim.name)=="connected"
                else:
                    connected = self.trpceClient.neStatus(sim.name)=="connected"
                allConnected = allConnected and connected
                if not allConnected:
                    break
            
            if allConnected:
                print('succeeded')
                return True
            timeout-=1
            time.sleep(1)
        print('failed')
        return False

    def urlencode(self, s:str):
        return urllib.parse.quote_plus(s)

    def testString(self, s:str, regex):
        pattern = re.compile(regex)
        return pattern.search(s) != None

    def assertIn(self, const, data):
        if type(data) == str:
            return data.find(const)>=0
        return const in data

    def assertEqual(self, const, data, msg=""):
        r= const == data
        if r == False and len(msg)>0:
            print(msg)
        return r

    def assertDictEqual(self, a, b):
        return a == b

    def assertNotIn(self, k, ar):
        return not k in ar
    

    