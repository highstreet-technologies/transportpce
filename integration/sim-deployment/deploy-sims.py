import sys
import os
import subprocess
from typing import List

DIR=os.path.abspath(os.path.dirname(os.path.realpath(__file__)))
TRPCEPATH=os.path.abspath(DIR+'/../../')
HOSTSFILE=DIR+'/hosts.ini'
ODL_SCHEME="http"
ODL_PORT=8181
BASEPORT=50000
COMMAND_DEPLOY="deploy"
COMMAND_DESTROY="destroy"
COMMAND_DESTROY_ALL="destroy-all"
COMMANDS=[COMMAND_DEPLOY, COMMAND_DESTROY, COMMAND_DESTROY_ALL]

def execCommand(command:List[str]):
    process = subprocess.Popen(command, shell=False)
    process.communicate()
    return process.returncode

def runDeploy(targetHost:str, xmlFilename:str, sdnrBaseUrl:str, simPort:int):
    execCommand(['ansible-playbook','-i',HOSTSFILE,
        '-e', 'file='+xmlFilename,
        '-e','target='+targetHost,
        '-e','sdnr='+sdnrBaseUrl,
        '-e','simport='+str(simPort),
        '-e','@'+DIR+'/vars.yml',
        DIR+'/deploy-sims.yml'])

def runDestroy(targetHost:str, xmlFilename:str, sdnrBaseUrl:str, simPort:int):
    execCommand(['ansible-playbook','-i',HOSTSFILE,
        '-e', 'file='+xmlFilename,
        '-e','target='+targetHost,
        '-e','sdnr='+sdnrBaseUrl,
        '-e','simport='+str(simPort),
        '-e','@'+DIR+'/vars.yml',
        DIR+'/destroy-sims.yml'])

def printHelp():
    print("deploy or destroy roadm sim containers for transportpce network")
    print("usage:")
    print("  python3 deploy-sims.py [{}] [folder-with-generated-xml-files]".format('|'.join(COMMANDS)))   
 
def loadHostsFile(filename)->List[str]:
    hosts=[]
    odls=[]
    with open(filename, 'r') as fp:
        simsStarted = False
        odlStarted = False
        lines = fp.readlines()
        for line in lines:
            if line.find('[simulation]')>=0:
                simsStarted=True
                odlStarted=False
                continue
            if line.find('[opendaylight]')>=0:
                simsStarted=False
                odlStarted=True
                continue
            help = line.split(' ')
            name = help.pop(0).strip()

            if len(name)<=0:
                continue
            user=None
            connection=None
            host=None
            for hlp in help:
                if hlp.startswith('ansible_host'):
                    host=hlp[13:].strip()
                elif hlp.startswith('ansible_connection'):
                    connection=hlp[19:].strip()
                elif hlp.startswith('ansible_user'):
                    user=hlp[13:].strip()
            if simsStarted:
                hosts.append({'name':name, 'host':host, 'connection':connection, 'user':user})
            if odlStarted:
                odls.append('{}://{}:{}'.format(ODL_SCHEME,host, ODL_PORT))

    return (hosts, odls)



args=sys.argv
args.pop(0)
if len(args)<1:
    printHelp()
    exit(1)
command = args.pop(0)
if not command in COMMANDS:
    printHelp()
    exit(1)
(hosts, odls) = loadHostsFile(HOSTSFILE)
if command==COMMAND_DESTROY_ALL:
    for host in hosts:
        execCommand(['ssh','{}@{}'.format(host['user'],host['host']),'docker rm -f $(docker ps -aq)'])
    exit(0)
if len(args)<1:
    printHelp()
    exit(1)
xmlFolder = os.path.abspath(args.pop(0))
if not os.path.isdir(xmlFolder):
    print("ERR: unable to local folder {}".format(xmlFolder))
    exit(1)
if execCommand(['which','ansible-playbook'])!=0:
    print("ERR: no ansbile installation found. please install")
    exit(1)
files = os.listdir(xmlFolder)
i=0
for file in files:
    if not file.endswith('.xml'):
        continue
    hostIndex = i % (len(hosts))
    odlIndex =  i % (len(odls))
    port = BASEPORT+(i//len(hosts))
    if command == COMMAND_DEPLOY:
        runDeploy(hosts[hostIndex]['name'],xmlFolder+'/'+file,odls[odlIndex],port)
    elif command == COMMAND_DESTROY:
        runDestroy(hosts[hostIndex]['name'],xmlFolder+'/'+file,odls[odlIndex],port)
    i+=1




