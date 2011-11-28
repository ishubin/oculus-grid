package net.mindengine.oculus.grid.storage;

/**
 * Used to store automation projects and synchronize them across all grid nodes
 * @author Ivan Shubin
 *
 */
public interface Storage {

    /**
     * Puts the project content to storage
     * @param name Name of project
     * @param version Version of project
     * @param content Zip archive of project
     * @param user Name of user who is uploading new version of project
     * @return Generated control key for new version of project 
     */
    public String putProjectZip(String name, String version, byte[] content, String user) throws Exception;
    
    /**
     * Reads and return control for project with specified version
     * @param name Name of project
     * @param version Version of project 
     * @return Control key for specified project. Returns null in case if project is not in storage.
     */
    public String readProjectControlKey(String name, String version) throws Exception;
}
