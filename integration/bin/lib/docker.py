import subprocess
import json

class InspectInfo:

    def __init__(self, jsonString):
        try:
            self.state = "unknown"
            self.ip = "unknown"
            o = json.loads(jsonString)
            self.state = o[0]["State"]["Status"]
            networks = o[0]["NetworkSettings"]["Networks"]
            self.ip = networks[list(networks)[0]]["IPAddress"]
            self.image = o[0]['Config']['Image']
        except:
            print("WARN: unable to parse docker inspect info")

    def getImage(self):
        return self.image

    def getIpAddress(self):
        return self.ip

    def getContainerStatus(self):
        return self.state


class Docker:

    def __init__(self):
        self.bin = "/usr/bin/docker"

    def exec(self, params):
        output = subprocess.Popen(
            self.bin+" "+params, shell=True, stdout=subprocess.PIPE).stdout.read()
        return output

    def inspect(self, name):
        return InspectInfo(self.exec("inspect "+name))

    def status(self, name):
        pass

    def copy(self, name, srcFilename, dstFilename):
        return self.exec("cp "+name+":"+srcFilename+" "+dstFilename)
    
    def exists(self, name):
        output = self.exec("ps | grep "+name)
        return len(output)>0

class DockerContainer:

    def __init__(self, containerName):
        self.containerName = containerName
        self.docker = Docker()

    def logs(self, params):
        return self.docker.exec("logs " + self.containerName + " "+params)

    def exec(self, params):
        return self.docker.exec("exec -ti "+self.containerName+" "+params)

    def karaflogs(self,params):
        return self.exec("cat /opt/opendaylight/data/log/karaf.log "+params)

    def inspect(self):
        return self.docker.inspect(self.containerName)

    def getImageTag(self):
        return self.docker.inspect(self.containerName).getImage()

    