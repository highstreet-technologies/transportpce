#!/usr/bin/python3
import os
import sys
from typing import List
import subprocess

args = sys.argv
args.pop(0)
AUGMENTS={
    'org-openroadm-device':[
        "/org-openroadm-device:org-openroadm-device/org-openroadm-device:interface",
        "/org-openroadm-device:org-openroadm-device/org-openroadm-device:protocols"
    ]
}

def executeCommand(cmd):
    'yanglint -p . -f tree org-openroadm-device.y > ../conf/org-openroadm-device.tree'
    bash_cmd='''#!/bin/sh
            if [ "$(dpkg -l | awk '/libyang-tools/ {print }'|wc -l)" -ge 1 ]; then
              echo 'libyang-tools is already installed'
            else
              if sudo apt install -y libyang-tools; then
                echo "Successfully installed libyang-tools"
              else
                echo "Error installing libyang-tools"
              fi
            fi
            ''' + "\n" + cmd
    subprocess.call(bash_cmd, shell=True)

def findAugmentFiles(yangFolder, augments: List[str]) -> List[str]:
    augmentFiles = []
    for yangFile in os.listdir(yangFolder):
        with open(yangFolder + os.sep + yangFile) as f:
            fileContent = f.read()
            for augment in augments:
                if (augment + "\"") in fileContent:
                    augmentFiles.append(f.name)
    return augmentFiles


def run(yangFolder:str, treeFolder:str):
    # generate org-openroadm-device.tree
    modules = ['org-openroadm-device','org-openroadm-pm','org-openroadm-syslog']
    for module in modules:
        print(yangFolder)
        augmentFiles = findAugmentFiles(yangFolder,AUGMENTS[module] if module in AUGMENTS else [])
        executeCommand('yanglint -p {src} -f tree {src}/{module}.yang {files} > {dst}/{module}.tree'.format(
            src=yangFolder,
            module=module,files = ' '.join(augmentFiles),
            dst=treeFolder))

    

def printHelp():
    pass


while len(args)>0:
    arg = args.pop(0)
    if arg == '--src':
        src = args.pop(0)
    elif arg == '--tree':
        tree = args.pop(0)

if src is None or tree is None:
    printHelp()
    exit(1)


run(os.path.abspath(src), os.path.abspath(tree))
