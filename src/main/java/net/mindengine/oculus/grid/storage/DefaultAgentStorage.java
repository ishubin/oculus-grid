package net.mindengine.oculus.grid.storage;

import java.io.File;
import java.util.Date;

public class DefaultAgentStorage extends DefaultGridStorage {

    
    @Override
    public String putProjectZip(final String name, final String version, byte[] content, String user, String controlKey) throws Exception {
        lock().lock(name, version);
        try {
            String pathToProject = getPathToProject(name, version);
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
