from io import TextIOWrapper
import re
from typing import List

class TreeNode:

    def __init__(self, name, access, mandatory, listKey=None) -> None:
        self.name = name
        self.access = access
        self.mandatory = mandatory
        self.listKey = listKey
        self.children=[]

    def addChild(self, child):
        return self.children.append(child)
    def isLeaf(self):
        return len(self.children)<=0
    def isReadOnly(self):
        return self.access=='ro'
    def isList(self):
        return self.listKey is not None

    def __str__(self) -> str:
        schildren=[]
        for child in self.children:
            schildren.append(str(child))
        return 'TreeNode[name={}, access={}, mandatory={}, listKey={}]'.format(
            self.name, self.access, self.mandatory, self.listKey)

class TreeFile:

    baseIndent=3
    regexModule = r"^module:\ ([^ ]+)$"
    regexProperty = r"^([\s\|]+)\+--(rw|ro)\s([^\s\?\*]+)([\?\*])?\s?(\[([^\]]+)\])?"
    def __init__(self, filename) -> None:
        self.filename = filename
        self.rootNodes:List[TreeNode]=[]
        self.load()
        
    def load(self):
        with open(self.filename,'r') as fp:
            line = fp.readline().rstrip()
            matches = re.finditer(TreeFile.regexModule, line)
            match = next(matches, None)
            if match is None:
                raise Exception('unable to parse file, no module found in line 1: {}'.format(line))
            self.module = match.group(1)
            root=TreeNode('','',True)
            # line = fp.readline().rstrip()
            # (indent, node) = TreeFile.parseLine(line)
            TreeFile.parseInnerTree(fp, None, root, 2 )

            self.rootNodes = root.children

    def __str__(self) -> str:
        return TreeFile.printNodes(self.rootNodes)
            
                
                              
    @staticmethod
    def printNodes(nodes:List[TreeNode], indent=0, treelevel=0):
        out=''
        for node in nodes:
            out+='{}:{}{}\n'.format(treelevel,' '*indent,node)
            if not node.isLeaf():
                out+=TreeFile.printNodes(node.children, indent+2, treelevel+1)
        return out
    
    @staticmethod
    def parseInnerTree(fp:TextIOWrapper, curNode:TreeNode, parentNode:TreeNode, rootIndent:int)->str:
        while True:
            line = fp.readline().rstrip()
            if line is None:
                break
            (indent, node) = TreeFile.parseLine(line)
            if indent is None:
                return (None, None)
            if indent==rootIndent or curNode is None:
                parentNode.addChild(node)
                curNode=node
            elif indent == rootIndent+TreeFile.baseIndent:
                curNode.addChild(node)
                (tmpIndent, tmp) = TreeFile.parseInnerTree(fp, node, curNode, indent)
                if tmp is None:
                    return (None, None)
                if tmpIndent==rootIndent:
                    parentNode.addChild(tmp)
                    curNode = tmp
                else:
                    return (tmpIndent, tmp)
            else: 
                break
        return (indent, node)


    @staticmethod
    def parseLine(line:str):
        # end reached
        if len(line.strip())<=0:
            return (None, None)
        # parse line
        matches = re.finditer(TreeFile.regexProperty, line)
        match = next(matches, None)
        if match is None:
            print("WARN: unable to parse line: {}".format(line))
            return (None, None)
        else:
            indent=len(match.group(1))
            x = len(match.groups())
            node=TreeNode(match.group(3),match.group(2),
                (not match.group(4)=='?') if x>=4 else True, 
                match.group(6) if x>=6 else None)
            return (indent, node)


class TreeParser:


    def __init__(self) -> None:
        pass


    def parse(self, filename) -> TreeFile:
        return TreeFile(filename)

# usage
# parser = TreeParser()
# file = parser.parse('/home/jack/odl/transportpce/integration/demo-standalone/conf/org-openroadm-device.tree')
# print(file)