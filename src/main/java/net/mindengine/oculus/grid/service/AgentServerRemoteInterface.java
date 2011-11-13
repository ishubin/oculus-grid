package net.mindengine.oculus.grid.service;

import java.rmi.Remote;

import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.task.TaskStatus;

/**
 * Implemented by Test Run Manager Server and specifies the messages from
 * TRMAgent to Server
 * 
 * @author Ivan Shubin
 * 
 */
public interface AgentServerRemoteInterface extends Remote {
	/**
	 * Notifies the server about the status of task. TRMAgent invokes this
	 * method in the following cases:
	 * <ul>
	 * <li>test-action is running</li>
	 * <li>test is running</li>
	 * <li>test is finished</li>
	 * <li>task is completed</li>
	 * </ul>
	 * 
	 * @param taskId
	 * @param taskStatus
	 *            Task Status with detailed information about the task
	 * @throws Exception
	 */
	public void updateTaskStatus(Long taskId, TaskStatus taskStatus) throws Exception;

	/**
	 * Notifies the server that the agent has join and is ready to execute
	 * queued tasks
	 * 
	 * @param agentInformation
	 *            Detailed information about the agent
	 * @throws Exception
	 * @return Id of the registered agent on server
	 */
	public Long registerAgent(AgentInformation agentInformation) throws Exception;

	/**
	 * Used to check the connection with TRMServer
	 * 
	 * @return
	 */
	public String checkConnection() throws Exception;
}
