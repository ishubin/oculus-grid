/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.mindengine.jeremy.bin.RemoteFile;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

public class GridProjectUpload {
	public static void upload(String serverHost, String serverName, int serverPort, String zipFilePath, String projectName, String projectVersion) throws Exception {
		String serverAddress = "rmi://" + serverHost + "/" + serverName;
		System.out.println("Locating registry " + serverHost + " port " + serverPort);
		Registry registry = LocateRegistry.getRegistry(serverHost, serverPort);
		System.out.println("Looking for " + serverAddress);

		ClientServerRemoteInterface server = (ClientServerRemoteInterface) registry.lookup(serverAddress);
		if (server == null)
			throw new Exception("The server wasn't found");

		File file = new File(zipFilePath);
		byte buffer[] = new byte[(int) file.length()];
		BufferedInputStream input = new BufferedInputStream(new FileInputStream(zipFilePath));
		input.read(buffer, 0, buffer.length);
		input.close();

		RemoteFile remoteFile = new RemoteFile();
		remoteFile.setBytes(buffer);
		remoteFile.setName(file.getName());
		server.uploadProject(projectName, projectVersion, remoteFile);
	}

	public static void main(String[] args) throws Exception {
		String serverHost = args[0];
		int serverPort = Integer.parseInt(args[1]);
		String serverName = args[2];
		String zipFilePath = args[3];
		String projectName = args[4];
		String projectVersion = args[5];

		upload(serverHost, serverName, serverPort, zipFilePath, projectName, projectVersion);
	}
}
