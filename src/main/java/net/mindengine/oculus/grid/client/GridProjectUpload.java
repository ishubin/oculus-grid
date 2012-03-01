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
package net.mindengine.oculus.grid.client;

import java.io.File;

import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

public class GridProjectUpload {
	public static void upload(String serverHost, String serverName, Integer serverPort, String zipFilePath, String projectName, String projectVersion, String userName) throws Exception {
		System.out.println("Locating registry " + serverHost + " port " + serverPort);
		
		System.out.println("Looking for server...");
		Lookup lookup = new Lookup("http://"+serverHost+":"+serverPort);
		ClientServerRemoteInterface server = lookup.getRemoteObject(serverName, ClientServerRemoteInterface.class);
		 
		if (server == null)
			throw new Exception("The server wasn't found");

		File file = new File(zipFilePath);
		server.uploadProject(projectName, projectVersion, file, userName);
	}

	public static String getArgument(String name, String [] args, boolean mandatory, String description) {
	    for(int i=0;i<args.length;i++) {
	        if(args[i].equals(name)) {
	            int j = i+1;
	            if(j<args.length) {
	                return args[j];
	            }
	        }
	    }
	    
	    if(mandatory) {
	        throw new IllegalArgumentException(name+" ("+description+") argument is not defined");
	    }
	    return null;
	}
	
	public static void main(String[] args) throws Exception {
	    
	    String serverHost = getArgument("-h", args, true, "server remote host");
		Integer serverPort = Integer.parseInt(getArgument("-p", args, true, "server remote port"));
		String serverName = getArgument("-n", args, true, "server remote name");
		String zipFilePath = getArgument("-f", args, true, "path to zip archive");
		String projectName = getArgument("-p", args, true, "project name");
		String projectVersion = getArgument("-v", args, true, "project version");
		String userName = getArgument("-u", args, false, null);
		
		upload(serverHost, serverName, serverPort, zipFilePath, projectName, projectVersion, userName);
	}
}
