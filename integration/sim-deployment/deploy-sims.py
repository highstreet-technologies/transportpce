#!/usr/bin/python3

import sys
import os
import subprocess
from typing import List
import json

DIR = os.path.abspath(os.path.dirname(os.path.realpath(__file__)))
TRPCEPATH = os.path.abspath(DIR + '/../../')
HOSTSFILE = DIR + '/hosts.ini'
ODL_SCHEME = "http"
ODL_PORT = 8181
BASEPORT = 50000
COMMAND_DEPLOY = "deploy"
COMMAND_DESTROY = "destroy"
COMMAND_RESTART = "restart"
COMMAND_RESTARTVMS = "restart-vms"
COMMAND_DESTROY_ALL = "destroy-all"
COMMANDS = [COMMAND_DEPLOY, COMMAND_DESTROY, COMMAND_DESTROY_ALL, COMMAND_RESTART, COMMAND_RESTARTVMS]


def execCommand(command: List[str], inBackground=False):
    
    if inBackground:
        process = subprocess.Popen(command)
    else:
        process = subprocess.Popen(command, shell=False)
    process.communicate()
    return process.returncode


def runDeploy(targetHost: str, xmlFilename: str, sdnrBaseUrl: str,
              simPort: int, debug = False):
    execCommand([
        'ansible-playbook', '-i', HOSTSFILE, '-e', 'file=' + xmlFilename, '-e',
        'target=' + targetHost, '-e', 'sdnr=' + sdnrBaseUrl, '-e',
        'simport=' + str(simPort), '-e', '@' + DIR + '/vars.yml',
        DIR + '/deploy-sims.yml'
    ], not debug)

def runRestart(targetHost: str, xmlFilename: str, sdnrBaseUrl: str,
              simPort: int, debug = False):
    execCommand([
        'ansible-playbook', '-i', HOSTSFILE, '-e', 'file=' + xmlFilename, '-e',
        'target=' + targetHost, '-e', 'sdnr=' + sdnrBaseUrl, '-e',
        'simport=' + str(simPort), '-e', '@' + DIR + '/vars.yml',
        DIR + '/restart-sims.yml'
    ], not debug)
def runDestroy(targetHost: str, xmlFilename: str, sdnrBaseUrl: str,
               simPort: int, debug = False):
    execCommand([
        'ansible-playbook', '-i', HOSTSFILE, '-e', 'file=' + xmlFilename, '-e',
        'target=' + targetHost, '-e', 'sdnr=' + sdnrBaseUrl, '-e',
        'simport=' + str(simPort), '-e', '@' + DIR + '/vars.yml',
        DIR + '/destroy-sims.yml'
    ], not debug)


def printHelp(message=None):
    if message is not None:
        print(message)
    print("deploy or destroy roadm sim containers for transportpce network")
    print("usage:")
    print("  python3 deploy-sims.py --src [folder-with-generated-xml-files] --profile [name-of-the-profile] [{}] ".
          format('|'.join(COMMANDS)))


def loadHostsFile(filename) -> List[str]:
    hosts = []
    odls = []
    with open(filename, 'r') as fp:
        simsStarted = False
        odlStarted = False
        lines = fp.readlines()
        for line in lines:
            if line.lstrip().startswith('#'):
                continue
            if line.find('[simulation]') >= 0:
                simsStarted = True
                odlStarted = False
                continue
            if line.find('[opendaylight]') >= 0:
                simsStarted = False
                odlStarted = True
                continue
            help = line.split(' ')
            name = help.pop(0).strip()

            if len(name) <= 0:
                continue
            user = None
            connection = None
            host = None
            for hlp in help:
                if hlp.startswith('ansible_host'):
                    host = hlp[13:].strip()
                elif hlp.startswith('ansible_connection'):
                    connection = hlp[19:].strip()
                elif hlp.startswith('ansible_user'):
                    user = hlp[13:].strip()
            if simsStarted:
                hosts.append({
                    'name': name,
                    'host': host,
                    'connection': connection,
                    'user': user
                })
            if odlStarted:
                odls.append('{}://{}:{}'.format(ODL_SCHEME, host, ODL_PORT))

    return (hosts, odls)


def updateProfile(filename, xmlfile:str, host:str, port:int)->bool:
    fn = os.path.basename(xmlfile)
    nodeId = fn[0:len(fn) - 4]
    success = False
    with open(filename, 'r') as fp:
        data = json.load(fp)
        for item in data:
            if item['node-id'] == nodeId:
                item['container'] = ""
                item['host'] = host
                item['port'] = port
                success = True
    
    with open(filename, 'w') as fp:
        json.dump(data, fp)
    return success


args = sys.argv
if len(args)>0 and args[0].endswith('.py'):
    args.pop(0)
src = None
profile = None
debug=False
newArgs=[]
print(args)
while len(args)>0:
    arg = args.pop(0)
    if arg == "--src":
        src = args.pop(0)
    elif arg == '--profile':
        profile = args.pop(0)
    elif arg == '--debug' or arg == '-v':
        debug=True
    else:
        newArgs.append(arg)
args=newArgs
if len(args) < 1:
    printHelp('no command given')
    exit(1)
command = args.pop(0)
if not command in COMMANDS:
    printHelp('unknown command')
    exit(1)
(hosts, odls) = loadHostsFile(HOSTSFILE)

if len(hosts) == 0:
    print(
        "ERR: no hosts found to deploy the simulators. please add at least one entry to the simulators section in hosts.ini"
    )
    exit(1)
if len(odls) == 0:
    print(
        "ERR: no hosts found to deploy the opendaylight. please add at least one entry to the opendaylight section in hosts.ini"
    )
    exit(1)

if command == COMMAND_DESTROY_ALL:
    for host in hosts:
        execCommand([
            'ssh', '{}@{}'.format(host['user'], host['host']),
            'docker rm -f $(docker ps -aq)'
        ])
elif command == COMMAND_RESTARTVMS:
    for host in hosts:
        print('restarting {}'.format(host['name']))
        execCommand([
            'ssh', '{}@{}'.format(host['user'], host['host']),
            'sudo reboot &'
        ])
    exit(0)

if src is None:
    print("please add --src argument for the source folder")
    exit(1)
if profile is None:
    print("please add --profile argument to update the profile")
    exit(1)
profileFilename = '{}/integration/profiles/sims/{}.json'.format(
    TRPCEPATH, profile)
if not os.path.isfile(profileFilename):
    print("unable to find sims profile with name {}".format(profile))
    exit(1)
if not os.path.isdir(src):
    print("ERR: unable to local folder {}".format(src))
    exit(1)
if execCommand(['which', 'ansible-playbook']) != 0:
    print("ERR: no ansbile installation found. please install")
    exit(1)
files = os.listdir(src)
i = 0
for xmlfile in files:
    if not xmlfile.endswith('.xml'):
        continue
    print('exec {} for {} of {}'.format(command, i, len(files)))
    hostIndex = i % (len(hosts))
    odlIndex = i % (len(odls))
    port = BASEPORT + (i // len(hosts))
    if command == COMMAND_DEPLOY:
        runDeploy(hosts[hostIndex]['name'],src+'/'+xmlfile, odls[odlIndex], port, debug)
        updateProfile(profileFilename, xmlfile, hosts[hostIndex]['host'], port)
    elif command == COMMAND_DESTROY:
        runDestroy(hosts[hostIndex]['name'], src + '/' + xmlfile,
                   odls[odlIndex], port, debug)
    elif command == COMMAND_RESTART:
        runRestart(hosts[hostIndex]['name'], src + '/' + xmlfile,
                   odls[odlIndex], port, debug)
    
    i += 1
