#
# integration test for transportpce with ONAP SDNC
#
# mounting test
# check if mount and unmount state is passed correctly to trpce container
# conditions:
#   * stable websocket connection
#   *
# szenario 1:
#   * everything running
#   * mount roadmA sim on SDNC side
#   * check connection state on SDNC side
#   * check logs on trpce side
# szenario 2:
#   * everything running
#   * unmount roadmA sim on SDNC side
#   * check connection state on SDNC side
#   * check logs on trpce side
import string
import random
import time
from datetime import datetime, timedelta


class MountingTest:

    def __init__(self, sdncClient, trpceClient, trpceContainer, sims):

        self.sdncClient = sdncClient
        self.trpceClient = trpceClient
        self.trpceContainer = trpceContainer
        self.sims = sims

    def generateRandomString(self, size=10, chars=string.ascii_lowercase + string.digits):
        return ''.join(random.choice(chars) for _ in range(size))

    def test1(self):

        now = datetime.utcnow()
        now2 = now + timedelta(minutes=1)
        print("running test 1")
        sim = self.sims[0]
        rndSuffix = "-"+self.generateRandomString()
        simName = sim.name+rndSuffix

        print("mounting "+simName+" to sdnc...", end='')
        mounted = self.sdncClient.mount(
            simName, sim.ip, sim.port, sim.username, sim.password)
        if mounted:
            print("success")
        else:
            print("failed")
            self.stopTest("unable to mount "+simName)

        print("check connection state on sdnc side...", end='')
        maxWait = 10
        while True:
            if self.sdncClient.neStatus(simName) == "connected":
                break

            time.sleep(1)
            maxWait -= 1
            if maxWait <= 0:
                print("failed")
                self.stopTest(
                    simName + " has not entered connected state in appropriate time")
        print("success")
        DATEFORMAT="%Y-%m-%dT%H:%M"
        print("waiting for log entry in trpce container...", end='')
        maxWait = 10
        grepFilter = "| grep -e \"{} | INFO | \" -e \"{} | INFO | \" | grep NetConfTopologyListener | grep \"{}\"".format(
            now.strftime(DATEFORMAT), now2.strftime(DATEFORMAT), "onDeviceConnected: "+simName)

        while True:
            if self.checkTrpceLogFor(grepFilter):
                break

            time.sleep(1)
            maxWait -= 1
            if maxWait <= 0:
                print("failed")
                self.stopTest(
                    simName + " was not registered as connected on trpce side")

        pass

    def test2(self):
        print("running test 2")
        pass

    def checkTrpceLogFor(self, log):
        logs = self.trpceContainer.karaflogs("|grep \""+log+"\"")
        if(len(logs) > 0):
            for log in logs:
                print(log)
            return True
        else:
            return False

    def stopTest(self, message):
        print(message)
        exit(1)
