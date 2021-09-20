#!/usr/bin/python3

import sys
import os
import os.path
import subprocess
import datetime
import time
import json
import re
from lib.config import IntegrationConfig
from lib.docker import Docker, DockerContainer
from lib.odlclient import OdlClient
from lib.trpceodlclient import TrpceOdlClient
from tests.mountingtest import MountingTest
from tests.end2endtest import End2EndTest
from lib.siminfo import SimulatorInfo

BIN_FOLDER="../bin"
SIM_FOLDER="../configs/sims"
CONTROLLER_FOLDER="../configs/controllers"
class Integration:

    def getUrl(self,config):
        host=config['host']
        if len(config['container'])>0:
            infos = self.dockerExec.inspect(config['container'])
            host=infos.getIpAddress()
        url = config['scheme']+'://'+host+':'+str(config['port'])
        if ('suffix' in config) and (len(config['suffix'])>0):
            url+=config['suffix']
        return url

    def __init__(self, prefix, envFiles=[], profile="default"):
        self.prefix = prefix
        self.config = IntegrationConfig(envFiles)
        self.dockerExec = Docker()
        self.profile = profile
        cconfig = self.loadControllerConfig()
        trpceConfig = cconfig['transportpce']
        self.odlTrpceClient = TrpceOdlClient(self.getUrl(trpceConfig), trpceConfig['username'], trpceConfig['password'])
        self.odlSdnrClients = []
        self.primarySdncClient = None
        if self.config.isRemoteEnabled():
            for sconfig in cconfig['sdnr']:
                client = OdlClient(self.getUrl(sconfig), sconfig['username'], sconfig['password'])
                if sconfig['primary']:
                    client.setPrimary(True)
                    self.primarySdncClient = client
                else:
                    self.odlSdnrClients.append(client)
           
    def resolveFile(self, filename):
        return os.path.dirname(__file__)+"/"+filename

    def loadControllerConfig(self):
        data=None
        regex = r"\$\{([^}]+)\}"
        with open(self.resolveFile(CONTROLLER_FOLDER+'/'+self.profile+'.json'),'r') as fp:
            content=fp.read()
            matches = re.finditer(regex, content, re.MULTILINE)
            for matchNum, match in enumerate(matches, start=1):
                content = content.replace(match.group(),self.config.getEnv(match.group(1)))
            data = json.loads(content)
            fp.close()
        return data

    def getContainerName(self, name, suffix="_1"):
        return name
#        return self.prefix+name+suffix

    def getSdnrClient(self, idx=0, primary=False):
        if (primary and self.primarySdncClient!=None) or len(self.odlSdnrClients) == 0:
            return self.primarySdncClient
        return self.odlSdnrClients[idx % len(self.odlSdnrClients)]

    def getTrpceClient(self):
        return self.odlTrpceClient
    
    def mount(self, args):
        infos = self.collectSimInfos()
        idx=0
        for info in infos:
            if self.config.getEnv("REMOTE_ODL_ENABLED") == "true":
                 self.getSdnrClient(idx).mount(info.name, info.ip, info.port, info.username, info.password)
            else:
                 self.odlTrpceClient.mount(info.name, info.ip, info.port, info.username, info.password)
            idx+=1
    
    def unmount(self, args):
        infos = self.collectSimInfos()
        idx=0
        for info in infos:
            if self.config.getEnv("REMOTE_ODL_ENABLED") == "true":
                self.getSdnrClient(idx).unmount(info.name)
            else:
                self.odlTrpceClient.unmount(info.name)
            idx+=1

    def printInfo(self, name, info):

        print(name.ljust(20)+info.getIpAddress().ljust(20) +
              info.getContainerStatus())

    def info(self):
        # print header
        print("name".ljust(20)+"ip".ljust(20)+"running")
        print("".ljust(60, "="))
        # print controller infos
        self.printInfo("sdnr", self.dockerExec.inspect(
            self.getContainerName("sdnr")))
        self.printInfo("sdncweb", self.dockerExec.inspect(
            self.getContainerName("sdncweb")))
        self.printInfo("transportpce", self.dockerExec.inspect(
            "transportpce"))
        self.printInfo("transportpce-gui", self.dockerExec.inspect(
            self.getContainerName("transportpce-gui")))


    def status(self,args):
        infos = self.collectSimInfos()
        for info in infos:
            if self.config.isRemoteEnabled():
                status = self.getSdnrClient(0, True).neStatus(info.name)
            else:
                status = self.odlTrpceClient.neStatus(info.name)
            print(info.name.ljust(20)+status)

    def collectSimInfos(self):
        mode = self.profile
        sims = []
        with open(self.resolveFile(SIM_FOLDER+"/"+mode+".json"), "r") as file:
            tmp=json.load(file)
            for sim in tmp:
                host=sim['host']
                if len(host) <=0:
                    simInfo = self.dockerExec.inspect(sim['container'])
                    host = simInfo.getIpAddress()
                sims.append(SimulatorInfo(sim['node-id'], host, sim['port'], sim['username'], sim['password']))
       
        return sims

    def getTransportPCEContainer(self):
        return DockerContainer("transportpce")
 
    def getSdnrContainer(self):
        return DockerContainer("sdnr")

    def getTransportPCEConsoleInfo(self):
        c = self.dockerExec.inspect("transportpce")

        return SimulatorInfo("transportPCE", c.getIpAddress(),2830, "admin", "admin")


    def openBrowser(self, url):
        subprocess.Popen("/usr/bin/xdg-open "+url+" >/dev/null 2>&1 &", shell=True)

    def openSdncWebBrowser(self):
        infos = self.dockerExec.inspect(self.getContainerName("sdncweb"))
        self.openBrowser("http://"+infos.getIpAddress()+":8080")
    def openTrpceGuiWebBrowser(self):
        infos = self.dockerExec.inspect(self.getContainerName("transportpce-gui"))
        self.openBrowser("http://"+infos.getIpAddress()+":8082")

    def openApidocsBrowser(self, args):
        container = args.pop(0)
        if container == "sdnc":
            infos = self.dockerExec.inspect(self.getContainerName("sdnr"))
            self.openBrowser("http://"+infos.getIpAddress()+":8181/apidoc/explorer/index.html")
        elif container == "trpce":
            infos = self.dockerExec.inspect("transportpce")
            self.openBrowser("http://"+infos.getIpAddress()+":8181/apidoc/explorer/index.html")

    def setLogs(self):

        tc = self.getTransportPCEContainer()
        tc.exec("/opt/opendaylight/bin/client 'log:set DEBUG org.opendaylight.transportpce'")
        tc.exec("/opt/opendaylight/bin/client 'log:set TRACE org.onap.ccsdk.features.sdnr.wt'")
        if self.config.isRemoteEnabled():
            sc = self.getSdnrContainer()
            sc.exec("/opt/opendaylight/bin/client 'log:set DEBUG org.onap.ccsdk.features.sdnr.wt'")
            sc.exec("/opt/opendaylight/bin/client 'log:set TRACE org.opendaylight.netconf'")    
        else:
            tc.exec("/opt/opendaylight/bin/client 'log:set TRACE org.opendaylight.netconf'")    
        
    def showTrpceLogs(self):
        tc = self.getTransportPCEContainer()
        grepFilter = "-ei org.opendaylight.transportpce -ei SdnrWebsocket -ei"
        tc.exec("/tail -f /opt/opendaylight/data/log/karaf.log | grep "+ grepFilter)

    def showCapabilities(self, device):
        if self.config.getEnv("REMOTE_ODL_ENABLED") == "true":
            response = self.odlTrpceClient.neInfos(self.getMountPointName(device))
        else:
            response = self.getSdnrClient(0, True).neInfos(self.getMountPointName(device))
        
        if response.isSucceeded():
            caps=response.data["network-topology:node"][0]["netconf-node-topology:available-capabilities"]["available-capability"]
            print(str(caps))
            for cap in caps:
                print(str(cap["capability"]))
        else:
            print("code: "+str(response.code))
            print(response.content)

    def stopSdncBundle(self, bundleId):
        tc = self.getSdnrContainer()
        tc.exec("/opt/opendaylight/bin/client 'bundle:stop "+bundleId+"'")

    def getLogs(self, args=[]):
        container = None
        if len(args)>0:
            container=args.pop(0)
        if not os.path.exists('logs'):
            os.makedirs('logs')
        c = Docker()
        src = "/opt/opendaylight/data/log/karaf.log"
        prefix = datetime.datetime.now(datetime.timezone.utc).strftime("%Y%m%dT%H%M%SZ")
        if container == "sdnc" or container is None:
            c.copy(self.getContainerName("sdnr"),src,"logs/"+prefix+"_sdnr.log")
        if container == "trpce" or container is None:
            c.copy("transportpce",src,"logs/"+prefix+"_trpce.log")

    def waitForReadyState(self, args=[]):
        timeout=60
        print("waiting for ready state (max "+str(timeout) + "sec)...",end="")
        while timeout>0:
            if self.config.isRemoteEnabled():
                ready = self.odlTrpceClient.isReady()
                if ready:
                    if self.primarySdncClient!=None:
                        ready= self.primarySdncClient.isReady()
                    for c in self.odlSdnrClients:
                        ready &= c.isReady()
            else:
                ready  = self.odlTrpceClient.isReady()
            
            if ready:
                print("ready!")
                return True
            timeout-=1
            print(".",end="")
            time.sleep(1)
        return False
        
    def executeTest(self, args):
        test = args.pop(0)
        if test == "1":
            test = MountingTest(self.odlSdnrClients, self.odlTrpceClient,
                                self.getTransportPCEContainer(), self.collectSimInfos())
            test.test1()
        elif test == "2":
            test = MountingTest(self.odlSdnrClients, self.odlTrpceClient,
                                self.getTransportPCEContainer(), self.collectSimInfos())
            test.test2()
        elif test == "end2end":
            test = End2EndTest(self.odlSdnrClients, self.primarySdncClient, self.odlTrpceClient,
                self.getTransportPCEContainer(),self.collectSimInfos(),self.config)
            test.test(args)
        elif test == "demo":
            test = End2EndTest(self.odlSdnrClients, self.odlTrpceClient,
                self.getTransportPCEContainer(),self.collectSimInfos(),self.config)
            test.test(args)
        else:
            print('unknown command: '+test)


def print_help():
    print("TransportPCE into ONAP integration script")
    print("=========================================")
    print("usage:")
    print("  integration.py [ARGS] COMMAND")
    print("ARGS:")
    print("  --env [envfile]       set custom env file (multiple possible)")
    print("  --profile [profile]   set custom profile")
    print("COMMANDS:")
    print("  info                  show docker container information")
    print("  status                show simulator states")
    print("  mount                 mount simulators")
    print("  unmount               unmount simulators")
    print("  setlogs               set more logs for sdnr and trpce")
    print("  test [test-number]    start integration test")
    print("  caps [device]         show capabilities of device")
    print("  bstop [bundleId]      stop bundle in SDN-R container")
    print("  web                   open ODLUX Gui in browser")
    print("  apidocs [trpce|sdnr]  open apidocs in Browser ")
   


if __name__ == "__main__":
    if len(sys.argv) < 1:
        print_help()
        exit(1)

    execDirAbs = os.getcwd()
    sys.argv.pop(0)

    profile="default"
    envFilenames = []
    x=0
    while x<len(sys.argv):
        arg = sys.argv[x]
        if arg == "--profile":
            sys.argv.pop(x)
            profile = sys.argv.pop(x)
            print("using profile: "+profile) 
        elif arg =="--env":
            sys.argv.pop(x)
            envFilename = sys.argv.pop(x)
            print("overwriting env file: "+envFilename)
            envFilenames.append(envFilename)          
        else:
            x+=1

    cmd = sys.argv.pop(0)
    # always autodetect prefix by exec folder
    tmp = execDirAbs.split("/")
    prefix = tmp[len(tmp)-1]
    prefix = prefix.replace("-", "").replace(" ", "_")+"_"
    # newest docker version doesnt replace '-' in folders to prefix
    d = Docker()
    if not d.exists(prefix+"sdnr_1"):
        prefix = tmp[len(tmp)-1]
        prefix = prefix.replace(" ", "_")+"_"

    if len(envFilenames) <=0:
        envFilename = execDirAbs+"/.env"
        if os.path.isfile(envFilename):
            envFilenames.append(envFilename)
    integration = Integration(prefix, envFilenames, profile)
    #print("args="+str(sys.argv))
    if cmd == "info":
        integration.info()
    elif cmd == "status":
        integration.status(sys.argv)
    elif cmd == "mount":
        integration.mount(sys.argv)
    elif cmd == "unmount":
        integration.unmount(sys.argv)
    elif cmd == "isready":
        integration.waitForReadyState(sys.argv)
    elif cmd == "setlogs":
        integration.setLogs()
    elif cmd == "test":
        if len(sys.argv) > 0:
            integration.executeTest(sys.argv)
        else:
            exit(1)
    elif cmd == "caps":
        integration.showCapabilities(sys.argv)
    elif cmd == "bstop":
        integration.stopSdncBundle(sys.argv)
    elif cmd == "getlogs":
        integration.getLogs(sys.argv)
    elif cmd == "web":
        integration.openSdncWebBrowser()
    elif cmd == "webtrpce":
        integration.openTrpceGuiWebBrowser()
    elif cmd == "apidocs":
        if len(sys.argv) > 0:
            integration.openApidocsBrowser(sys.argv)
        else:
            exit(1)
        
    else:
        exit(1)
