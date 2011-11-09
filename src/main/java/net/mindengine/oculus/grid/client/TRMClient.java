package net.mindengine.oculus.grid.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

public class TRMClient {
	public static ClientServerRemoteInterface getServer(String serverAddress) throws RemoteException, MalformedURLException, NotBoundException {
		return (ClientServerRemoteInterface) Naming.lookup(serverAddress);
	}
}
