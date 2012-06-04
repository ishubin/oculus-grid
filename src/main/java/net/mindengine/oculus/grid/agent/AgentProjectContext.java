package net.mindengine.oculus.grid.agent;

public class AgentProjectContext {
    
    private String jlibSeparator;
    private String projectDir;
    private String suiteFile;
    public String getJlibSeparator() {
        return jlibSeparator;
    }
    public void setJlibSeparator(String jlibSeparator) {
        this.jlibSeparator = jlibSeparator;
    }
    public String getProjectDir() {
        return projectDir;
    }
    public void setProjectDir(String projectDir) {
        this.projectDir = projectDir;
    }
    public String getSuiteFile() {
        return suiteFile;
    }
    public void setSuiteFile(String suiteFile) {
        this.suiteFile = suiteFile;
    }
}
