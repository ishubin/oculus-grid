package net.mindengine.oculus.grid.agent;

import java.rmi.Remote;

import net.mindengine.oculus.grid.domain.task.TestStatus;
import net.mindengine.oculus.grid.runner.OculusRunner;

/**
 * Used in {@link Agent} for handling all the events from
 * {@link OculusRunner}
 * 
 * @author Ivan Shubin
 * 
 */
public interface AgentTestRunnerListener extends Remote {
    
    /**
     * Used to check if the test suite need to be interrupted.
     * @return false in case if test should be interrupted, true - if it can proceed
     */
    public Boolean shouldProceed();
    
	public void onTestStarted(TestStatus testStatus) throws Exception;

	/**
	 * Notifies the subscriber that the test action has started
	 * 
	 * @param name
	 *            Name of the test action
	 * @param percent
	 *            Percents of test actions done
	 * @throws Exception
	 */
	public void onTestAction(String name, Integer percent);

	public void onTestFinished(TestStatus testStatus);

	/**
	 * 
	 * @param suiteId
	 *            Id of the suite in DB
	 * @throws RemoteException
	 */
	public void onTaskFinished(Long suiteId);
}
