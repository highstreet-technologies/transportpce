import re
import tempfile
import tempfile
import shutil
from .xpath import XPath

class XmlFile:

    def __init__(self, filename):
        self.filename=filename

                  
    # remove xmlElement (simple leafs or complete objects)
    # valuePath: xpath
    #    e.g. /project/parent/version
    #         /project/dependencies/dependency[groupId=org.opendaylight.netconf ]/version
    # value: value to set
    def removeXmlEntry(self, valuePath, replaceMultiple=False) -> bool:
        
        found=False
        pathToFind = XPath(valuePath)
        pattern = re.compile('<([^>^\ ^?^!]+)')
        curPath=XPath()
        curParent=None
        isComment=False
        toRemove=False
        removeThisLine=False
        with tempfile.NamedTemporaryFile(mode='w', delete=False) as tmp_file:
            with open(self.filename) as src_file:
                for line in src_file:
                    if found == False or replaceMultiple:
                        x=line.find('<!--')
                        y=line.find('-->')
                        if x>=0:
                            isComment=True
                        if y>=0 and y > x:
                            isComment=False
                        if not isComment:
                            matches = pattern.finditer(line,y)
                            for matchNum, match in enumerate(matches, 1):
                                f = match.group(1)
                                # end tag detected
                                if f.startswith("/"):
                                    # remove end tag for containers too
                                    if pathToFind.equals(curPath, ignoreFilter=False, ignoreDeeper=True):
                                        toRemove=True
                                        removeThisLine=True
                                        found=True
                                    curPath.remove(f[1:])
                                # start tag detected (not autoclosing xml like <br />)
                                elif not f.endswith("/"):
                                    x = curPath.add(f)
                                    if curParent is None:
                                        curParent = x
                                    else:
                                        curParent = curPath.last(1)
                                else:
                                    continue
                                # if path matches or even deeper 
                                if pathToFind.equals(curPath, ignoreFilter=False, ignoreDeeper=True):
                                    toRemove=True
                                    removeThisLine=True
                                    found=True
                                else:
                                    toRemove=False
                                    if pathToFind.parentParamIsNeeded(curPath.subpath(1), f):
                                        v = self.tryToGetValue(line, f)
                                        if v is not None:
                                            curParent.setFilter(f, v)
                    if not (toRemove or removeThisLine):
                        tmp_file.write(line)
                    removeThisLine=False
            # Overwrite the original file with the munged temporary file in a
            # manner preserving file attributes (e.g., permissions).
            shutil.copystat(self.filename, tmp_file.name)
            shutil.move(tmp_file.name, self.filename)
        print("removed {} in {}: {}".format(valuePath, self.filename, str(found)))
        return found    
    # set xmlElementValue (just simple values - no objects)
    # valuePath: xpath
    #    e.g. /project/parent/version
    #         /project/dependencies/dependency[groupId=org.opendaylight.netconf]/version
    # value: value to set
    def setXmlValue(self, valuePath, value, replaceMultiple=False) -> bool:
        
        found=False
        pathToFind = XPath(valuePath)
        pattern = re.compile('<([^>^\ ^?^!]+)')
        curPath=XPath()
        curParent=None
        isComment=False
        with tempfile.NamedTemporaryFile(mode='w', delete=False) as tmp_file:
            with open(self.filename) as src_file:
                for line in src_file:
                    if found == False or replaceMultiple:
                        x=line.find('<!--')
                        y=line.find('-->')
                        if x>=0:
                            isComment=True
                        if y>=0 and y > x:
                            isComment=False
                        if not isComment:
                            matches = pattern.finditer(line,y)
                            for matchNum, match in enumerate(matches, 1):
                                f = match.group(1)
                                # end tag detected
                                if f.startswith("/"):
                                    curPath.remove(f[1:])
                                # start tag detected (not autoclosing xml like <br />)
                                elif not f.endswith("/"):
                                    x = curPath.add(f)
                                    if curParent is None:
                                        curParent = x
                                    else:
                                        curParent = curPath.last(1)
                                else:
                                    continue
                                if pathToFind.equals(curPath, False):
                                    pre=line[0:line.index('<')]
                                    line=pre+'<{x}>{v}</{x}>\n'.format(x=f,v=value)
                                    found=True
                                    curPath.remove(f)
                                    break
                                elif pathToFind.parentParamIsNeeded(curPath.subpath(1), f):
                                    v = self.tryToGetValue(line, f)
                                    if v is not None:
                                        curParent.setFilter(f, v)

                    tmp_file.write(line)
            # Overwrite the original file with the munged temporary file in a
            # manner preserving file attributes (e.g., permissions).
            shutil.copystat(self.filename, tmp_file.name)
            shutil.move(tmp_file.name, self.filename)
        print("set {} to {} in {}: {}".format(valuePath, value, self.filename, str(found)))
        return found

    def tryToGetValue(self, line, xmlTag=None):
        pattern = re.compile('<([^>^\ ^?^!]+)>([^<]+)<\/([^>^\ ^?^!]+)>' if xmlTag is None else '<('+xmlTag+')>([^<]+)<\/('+xmlTag+')>') 
        matches = pattern.finditer(line)
        match = next(matches)
        if match is not None:
            return match.group(2)
        return None
