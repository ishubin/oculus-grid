package net.mindengine.oculus.grid.agent;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.mindengine.oculus.grid.runner.OculusRunner;
import net.mindengine.oculus.grid.service.AgentOculusRunnerRemoteInterface;

/**
 * Used in {@link TRMAgent} for handling all the events from
 * {@link OculusRunner}
 * 
 * @author Ivan Shubin
 * 
 */
public interface AgentTestRunnerListener extends Remote {
	public void setOculusRunner(AgentOculusRunnerRemoteInterface oculusRunnerRemoteInterface) throws RemoteException;

	public void onTestStarted(String name, Long id) throws Exception;

	/**
	 * Notifies the subscriber that the test action has started
	 * 
	 * @param name
	 *            Name of the test action
	 * @param percent
	 *            Percents of test actions done
	 * @throws Exception
	 */
	public void onTestAction(String name, Integer percent) throws RemoteException;

	public void onTestFinished(String name, Long id, Integer status) throws RemoteException;

	/**
	 * 
	 * @param suiteId
	 *            Id of the suite in DB
	 * @throws RemoteException
	 */
	public void onTaskFinished(Long suiteId) throws RemoteException;
}
