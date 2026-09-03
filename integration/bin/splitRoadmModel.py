#!/usr/bin/python3
import sys
import os
import shutil
from typing import List
from lib.treeparser import TreeParser, TreeFile
from lib.xmlfile import XmlFile

class RoadmModelSplitter:


    def __init__(self) -> None:
        pass


    def makeSplit(self, xmlFilename:str, treeFilename:str):
        if not os.path.isfile(xmlFilename):
            raise FileNotFoundError(f"XML file not found: {xmlFilename}")
        if not os.path.isfile(treeFilename):
            raise FileNotFoundError(f"Tree file not found: {treeFilename}")
        
        print("xmlFilename {}".format(xmlFilename))
        parser = TreeParser()
        treeFile = parser.parse(treeFilename)
        base, ext = os.path.splitext(xmlFilename)
        runningXmlFilename = f"{base}-running{ext}"
        print("runningXmlFilename {}".format(runningXmlFilename))
        operationalXmlFilename = f"{base}-operational{ext}"
        if os.path.isfile(runningXmlFilename):
            os.remove(runningXmlFilename)
        if os.path.isfile(operationalXmlFilename):
            os.remove(operationalXmlFilename)
        shutil.copyfile(xmlFilename, runningXmlFilename)
        shutil.copyfile(xmlFilename,operationalXmlFilename)
        xmlFile = XmlFile(runningXmlFilename)
        roPaths = treeFile.getReadOnlyPaths()
        for path in roPaths:
            xmlFile.removeXmlEntry(path, True)

    def printHelp(self):
        print("Usage: python splitRoadmModel.py --src <xml_file> --tree <tree_file>")
        print("Example: python splitRoadmModel.py --src conf-generated-7-1-0/ROADM-Berlin --tree conf_7-1-0/org-openroadm-device-710.tree")
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


if __name__ == "__main__":
    args = sys.argv[1:]
    splitter = RoadmModelSplitter()
    splitter.run(args)
