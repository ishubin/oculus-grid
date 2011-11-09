package net.mindengine.oculus.grid.service;

import java.rmi.Remote;

import net.mindengine.oculus.grid.agent.TRMAgent;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.server.TRMServer;

/**
 * Server to TRMAgent message specification. Will be implemented by TRMAgent
 * 
 * @author Ivan Shubin
 * @see TRMAgent
 * @see TRMServer
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
	public void runTask(Task task) throws Exception;

	/**
	 * Stops the current task on agent
	 * 
	 * @throws Exception
	 */
	public void stopTask() throws Exception;

	/**
	 * Notifies the agent about stopping and agent will immediately exit
	 * 
	 * @throws Exception
	 */
	public void stopAgent() throws Exception;

	/**
	 * Uploads zip archive with projects content and extracts it to the projects
	 * folder
	 * 
	 * @param projectPath
	 * @param version
	 * @param zippedContent
	 * @throws Exception
	 */
	public void uploadProject(String projectPath, String version, byte[] zippedContent) throws Exception;
}
