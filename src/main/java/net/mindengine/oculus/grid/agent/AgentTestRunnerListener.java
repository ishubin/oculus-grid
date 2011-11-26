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
