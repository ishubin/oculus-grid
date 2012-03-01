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
     * @param controlKey Generated key which is used in order to check if projects are synchronized on all nodes.
     * @return Generated control key for new version of project 
     */
    public String putProjectZip(String name, String version, byte[] content, String user, String controlKey) throws Exception;
    
    /**
     * Reads and return control for project with specified version
     * @param name Name of project
     * @param version Version of project 
     * @return Control key for specified project. Returns null in case if project is not in storage.
     */
    public String readProjectControlKey(String name, String version) throws Exception;
    
    /**
     * Returns the latest version of project from storage
     * @param name
     * @param version
     * @return
     * @throws Exception
     */
    public File downloadProjectFromStorage(String name, String version) throws Exception;

    /**
     * Returns local path to specified project in storage
     * @param projectName Name of project
     * @param projectVersion Version of project
     * @return
     */
    public String getProjectPath(String projectName, String projectVersion);
}
