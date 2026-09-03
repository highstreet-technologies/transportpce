import os
import tempfile
from zipfile import ZipFile

class OpenroamdNtsNgDeployGenerator:



    def __init__(self, basePath, yangPath=None, configPath=None, outputPath=None) -> None:
        self.basePath = basePath
        self.yangPath = yangPath or f"{basePath}/integration/yang"
        self.configPath = configPath or f"{basePath}/integration/demo-standalone/conf/ntsim_configuration.json"
        self.outputPath = outputPath

    

    def createArchive(self, outputFilename, xmlDataFilename):
        zipObj = ZipFile(self.outputPath+'/'+outputFilename, 'w')
        
        #zipObj.write('.env')
        zipObj.write(self.yangPath, 'yang')
        yangfiles = os.listdir(self.yangPath)
        for file in yangfiles:
            zipObj.write(os.path.join(self.yangPath, file), os.path.join('yang', file))
        zipObj.write(self.configPath, 'config.json')
        with tempfile.NamedTemporaryFile('w') as tmpfile:
            with open(xmlDataFilename) as inputfile:
                lines = inputfile.readlines()
                lines.pop(0)
                tmpfile.writelines(lines)
                tmpfile.flush()
            zipObj.write(tmpfile.name,'data/org-openroadm-device.xml')
            tmpfile.close()
        #zipObj.write(self.basePath+'/'+xmlDataFilename,'data/org-openroadm-device.xml')
        # close the Zip File
        zipObj.close()