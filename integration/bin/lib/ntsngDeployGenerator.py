import os
import tempfile
from zipfile import ZipFile

class OpenroamdNtsNgDeployGenerator:



    def __init__(self, basePath, outputPath=None) -> None:
        self.basePath = basePath
        self.outputPath = outputPath

    

    def createArchive(self, outputFilename, xmlDataFilename):
        zipObj = ZipFile(self.outputPath+'/'+outputFilename, 'w')
        
        #zipObj.write('.env')
        zipObj.write(self.basePath+'/integration/yang','yang')
        yangfiles = os.listdir(self.basePath+'/integration/yang')
        for file in yangfiles:
            zipObj.write(self.basePath+'/integration/yang/'+file,'yang'+'/'+file)
        zipObj.write(self.basePath+'/integration/demo-standalone/conf/ntsim_configuration.json','config.json')
        with tempfile.NamedTemporaryFile('w') as tmpfile:
            with open(self.basePath+'/'+xmlDataFilename) as inputfile:
                lines = inputfile.readlines()
                lines.pop(0)
                tmpfile.writelines(lines)
                tmpfile.flush()
            zipObj.write(tmpfile.name,'data/org-openroadm-device.xml')
            tmpfile.close()
        #zipObj.write(self.basePath+'/'+xmlDataFilename,'data/org-openroadm-device.xml')
        # close the Zip File
        zipObj.close()