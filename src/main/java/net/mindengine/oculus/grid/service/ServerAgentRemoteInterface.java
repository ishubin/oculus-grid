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
package net.mindengine.oculus.grid.service;

import java.rmi.Remote;

import net.mindengine.oculus.grid.agent.Agent;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.SuiteTask;
import net.mindengine.oculus.grid.server.Server;

/**
 * Server to TRMAgent message specification. Will be implemented by TRMAgent
 * 
 * @author Ivan Shubin
 * @see Agent
 * @see Server
 */
public interface ServerAgentRemoteInterface extends Remote {
	/**
	 * Used for obtaining the information about agent as well as for checking
	 * the communication between agent and server
	 * 
	 * @return Status of agent
	 * @throws Exception
	 */
	public AgentStatus getAgentStatus() throws Exception;

	/**
	 * Starts running the the task on agent
	 * 
	 * @param suite
	 *            A set of tests with tests dependency and parameters
	 * @throws Exception
	 */
	public void runSuiteTask(SuiteTask task) throws Exception;

	/**
	 * Stops the current task on agent
	 * 
	 * @throws Exception
	 */
	public void stopCurrentTask() throws Exception;

	/**
	 * Notifies the agent about stopping and agent will immediately exit
	 * 
	 * @throws Exception
	 */
	public void killAgent() throws Exception;

}
