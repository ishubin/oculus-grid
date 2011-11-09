package net.mindengine.oculus.grid.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

public class TRMProjectUpload {
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

		server.uploadProject(projectName, projectVersion, buffer);
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
