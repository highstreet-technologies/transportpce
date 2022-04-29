import sys
import os
import shutil
from typing import List
from lib.treeparser import TreeNode, TreeParser, TreeFile
from lib.xmlfile import XmlFile

class RoadmModelSplitter:


    def __init__(self) -> None:
        pass


    def makeSplit(self, xmlFilename:str, treeFilename:str):
        parser = TreeParser()
        treeFile = parser.parse(treeFilename)
        runningXmlFilename='{}-running.xml'.format(xmlFilename.rstrip('.xml'))
        operationalXmlFilename='{}-operational.xml'.format(xmlFilename.rstrip('.xml'))
        if os.path.isfile(runningXmlFilename):
            os.remove(runningXmlFilename)
        if os.path.isfile(operationalXmlFilename):
            os.remove(operationalXmlFilename)
        shutil.copyfile(xmlFilename, runningXmlFilename)
        shutil.copyfile(xmlFilename,operationalXmlFilename)
        xmlFile = XmlFile(runningXmlFilename)
        RoadmModelSplitter.recursiveRemoveROProperties(xmlFile, treeFile.rootNodes, '')
    
    @staticmethod
    def recursiveRemoveROProperties(xmlFile:XmlFile, treeNodes:List[TreeNode], curXpath:str):
        for node in treeNodes:
            xpath='{}/{}'.format(curXpath, node.name)
            if xpath.endswith('circuit-pack-category'):
                x=1+2
            if node.isReadOnly():
                xmlFile.removeXmlEntry(xpath,True)
            if not node.isLeaf():
                RoadmModelSplitter.recursiveRemoveROProperties(xmlFile,node.children, xpath)

    def printHelp(self):
        pass
    def run(self, args:List[str]=[]):
        src=None
        tree=None
        while len(args)>0:
            arg = args.pop(0)
            if arg == '--src':
                src = args.pop(0)
            elif arg == '--tree':
                tree = args.pop(0)
        
        if src is None or tree is None:
            self.printHelp()
            exit(1)

        self.makeSplit(os.path.abspath(src), os.path.abspath(tree))


args = sys.argv
args.pop(0)

splitter = RoadmModelSplitter()
splitter.run(args)