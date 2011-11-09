package net.mindengine.oculus.grid.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.mindengine.oculus.grid.agent.TRMAgent;
import net.mindengine.oculus.grid.runner.OculusRunner;

/**
 * Used for message specification between {@link TRMAgent} and
 * {@link OculusRunner}
 * 
 * @author Ivan Shubin
 * 
 */
public interface AgentOculusRunnerRemoteInterface extends Remote {
	/**
	 * Stops the suite. The {@link OculusRunner} will wait for the current test
	 * to be finished and then stops running all the rest tests.
	 * 
	 * @throws RemoteException
	 */
	public void stopSuite() throws RemoteException;

}
