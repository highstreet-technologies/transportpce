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
    regexCase = r"^([\s\|]+)\+--:\(([^)]+)\)"
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
            TreeFile.parseInnerTree(fp, root, 2)

            self.rootNodes = root.children

    def __str__(self) -> str:
        return TreeFile.printNodes(self.rootNodes)

    def getReadOnlyPaths(self) -> List[str]:
        out: List[str] = []
        TreeFile._collectReadOnlyPaths(self.rootNodes, '', out)
        return out

    @staticmethod
    def _collectReadOnlyPaths(nodes: List[TreeNode], curXpath: str, out: List[str]):
        for node in nodes:
            xpath = '{}/{}'.format(curXpath, node.name)
            if node.isReadOnly():
                out.append(xpath)
                if not node.isLeaf():
                    continue
            elif not node.isLeaf():
                TreeFile._collectReadOnlyPaths(node.children, xpath, out)
            
                
                              
    @staticmethod
    def printNodes(nodes:List[TreeNode], indent=0, treelevel=0):
        out=''
        for node in nodes:
            out+='{}:{}{}\n'.format(treelevel,' '*indent,node)
            if not node.isLeaf():
                out+=TreeFile.printNodes(node.children, indent+2, treelevel+1)
        return out
    
    @staticmethod
    def parseInnerTree(fp:TextIOWrapper, root:TreeNode, rootIndent:int):
        # Depth-driven, stack-based parse: each line's nesting level is derived
        # purely from its indentation relative to its parent's, not from a fixed
        # indent step. This reaches the last node of the tree and never skips a
        # child merely because the indentation step between two levels differs
        # from baseIndent (e.g. YANG choice/case subtrees).
        #
        # stack holds (node, indent) pairs, stack[-1] being the current parent.
        # The root entry anchors top-level siblings at indent rootIndent.
        stack=[(root, rootIndent-1)]
        for line in fp:
            line=line.rstrip()
            if line is None or line=='':
                break
            # The data tree ends at the rpcs/notifications sections; skip them
            # entirely so their nested ro/rw leaves are never attached.
            stripped = line.strip()
            if stripped == 'rpcs:' or stripped == 'notifications:':
                break
            (indent, node) = TreeFile.parseLine(line)
            if indent is None:
                # unparseable structural line (YANG choice/case already handled
                # by parseLine; other non-property lines). Skip without
                # affecting the stack.
                continue
            # pop until the parent is shallower than this line
            while len(stack)>1 and stack[-1][1] >= indent:
                stack.pop()
            stack[-1][0].addChild(node)
            stack.append((node, indent))
        return None


    @staticmethod
    def parseLine(line:str):
        # end reached
        if len(line.strip())<=0:
            return (None, None)
        # parse line
        matches = re.finditer(TreeFile.regexProperty, line)
        match = next(matches, None)
        if match is None:
            # YANG choice case line, e.g. "+--:(otucn)".
            # Parse it as a node so the indent bookkeeping in parseInnerTree
            # stays consistent; its access is inherited from the enclosing
            # choice, so mark it 'case' (treated as non-ro/non-rw for removal).
            caseMatches = re.finditer(TreeFile.regexCase, line)
            caseMatch = next(caseMatches, None)
            if caseMatch is not None:
                indent = len(caseMatch.group(1))
                node = TreeNode(caseMatch.group(2), 'case', True, None)
                return (indent, node)
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