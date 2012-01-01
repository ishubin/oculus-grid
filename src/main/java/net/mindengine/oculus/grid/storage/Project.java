package net.mindengine.oculus.grid.storage;

import net.mindengine.jeremy.bin.RemoteFile;

public class Project extends RemoteFile {

    /**
     * 
     */
    private static final long serialVersionUID = 1800931527219318420L;

    private String controlKey;
    private String projectName;
    private String projectVersion;

    public String getControlKey() {
        return controlKey;
    }

    public void setControlKey(String controlKey) {
        this.controlKey = controlKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(String projectVersion) {
        this.projectVersion = projectVersion;
    }
    
}
