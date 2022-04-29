import os
import sys
from typing import List

args = sys.argv
args.pop(0)
AUGMENTS={
    'org-openroadm-device':[
        "/org-openroadm-device:org-openroadm-device/org-openroadm-device:interface"
    ]
}

def executeCommand(cmd):
    'yanglint -p . -f tree org-openroadm-device.y > ../conf/org-openroadm-device.tree'

def findAugmentFiles(yangFolder, augments:List[str])->List[str]:
    pass
def run(yangFolder:str, treeFolder:str):
    # generate org-openroadm-device.tree
    modules = ['org-openroadm-device','org-openroadm-pm','org-openroadm-syslog']
    for module in modules:
        augmentFiles = findAugmentFiles(yangFolder,AUGMENTS[module] if module in AUGMENTS else [])
        executeCommand('yanglint -p {src} -f tree {module}.yang {files} > {dst}/{module}.tree'.format(
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