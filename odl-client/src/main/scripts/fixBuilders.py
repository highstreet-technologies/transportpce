#!/usr/bin/python3

import os
import sys
import re

class CodeDetailInfo:

    def __init__(self):
        self.startLine = 0
        self.endLine = 0
        self.lines = []


class JavaCodeFile:
    
    def __init__(self, filename):
        self.filename = filename
        self.lines = self.readFile()

    def readFile(self):
        file1 = open(self.filename, 'r') 
        lines = file1.readlines() 
        file1.close()
        return lines

    def writeFile(self,lines):
        file1 = open(self.filename, 'r') 
        lines = file1.writelines(lines) 
        file1.close()

    def getPackageName(self):
        pkg = None
        for line in self.lines:
            if line.startswith("package"):
                pkg = line[7:-2]
                break
        return pkg
    def findFunction(self,fnName):
        return None
    
    def addImport(self,toImport):
        pass
    

class ODLBuilderJavaCodeFile(JavaCodeFile):

    def __init__(self,filename):
        JavaCodeFile.__init__(self,filename)

    def hasDeprecatedMethod(self):
        for line in self.lines:
            if line.find("@Deprecated"):
                return True
        return False


def find_files(path, regex):
    result = []
    for root, dirs, files in os.walk(path):
        for name in files:
            if re.match(regex,name)!=None:
                result.append(os.path.join(root, name))
    return result


def do_run(srcFolders=[],dstFolderBase=".", baseJavaBuilderNamespace=""):

    cnt = 0
    for srcFolder in srcFolders:
        if not os.path.exists(srcFolder):
            print("src folder '"+srcFolder+"' not found")
            exit(1)
        #find all builders(*Builder.java)
        files = find_files(srcFolder,r".*Builder.java$")
        for file in files:
            #check if it contains deprecated functions
            jfile = ODLBuilderJavaCodeFile(file)
            if not jfile.hasDeprecatedMethod():
                continue
            print(str(cnt)+ " "+file)
            cnt+=1
            #copy to dst with new namespace
            #correct package name
            #add imports for interface and maybe key
            #remove deprecated functions





def print_help():
    print("no help")



if __name__ == "__main__":
#    if len(sys.argv) < 1:
#        print_help()
#        exit(1)

    do_run(["/home/jack/odl/transportpce/ordmodels/device/target/generated-sources",
    "/home/jack/odl/transportpce/ordmodels/common/target/generated-sources"],
    "/home/jack/odl/transportpce/odl-client/src/main/java/org/onap/ccsdk/features/sdnr/wt/odlclient/data/builders",
    "org.opendaylight.yang.gen.v1.http.org.openroadm"
    )