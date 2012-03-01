/*******************************************************************************
* 2012 Ivan Shubin http://mindengine.net
* 
* This file is part of MindEngine.net Oculus Grid.
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
package net.mindengine.oculus.grid.storage;

import java.io.File;
import java.util.Date;

public class DefaultAgentStorage extends DefaultGridStorage {

    
    @Override
    public String putProjectZip(final String name, final String version, byte[] content, String user, String controlKey) throws Exception {
        lock().lock(name, version);
        try {
            String pathToProject = getProjectPath(name, version);
            File file = new File(pathToProject);
            if(!file.exists()) {
                file.mkdirs();
            }
            
            if(!file.isDirectory()){
                throw new RuntimeException("Cannot upload project, given path is not a directory: "+file.getAbsolutePath());
            }
            
            extractZip(content, getStoragePath(), pathToProject);
            
            writeGridProject(pathToProject, name, version, controlKey, new Date(), user);
            return controlKey;
        }
        catch (Exception e) {
            throw e;
        }
        finally {
            lock().unlock(name, version);
        }
    }
}
