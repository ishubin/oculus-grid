package net.mindengine.oculus.grid.service;

import java.rmi.Remote;

import net.mindengine.jeremy.bin.RemoteFile;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.DefaultTask;
import net.mindengine.oculus.grid.domain.task.TaskInformation;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.server.TaskHandler;

/**
 * Used for messages from client to server specification
 * 
 * @author Ivan Shubin
 */
public interface ClientServerRemoteInterface extends Remote {

	/**
	 * Puts the task in tasks queue so it will be picked up later by the
	 * {@link TaskHandler}
	 * 
	 * @param task
	 *            Task for execution
	 * 
	 * @return The unique id of the task in a task queue
	 * @see TaskHandler
	 * @throws Exception
	 */
	public Long runTask(DefaultTask task) throws Exception;

	/**
	 * Sends stop message to the agent which was assigned for the task.
	 * 
	 * @param taskId
	 *            The unique id of the task in a task queue
	 * @throws Exception
	 */
	public void stopTask(Long taskId) throws Exception;

	/**
	 * Returns the status of requested task
	 * 
	 * @param taskId
	 *            The unique id of the task in a task queue
	 * 
	 * @return Task status of the specified task
	 * 
	 * @throws Exception
	 */
	public TaskStatus getTaskStatus(Long taskId) throws Exception;

	
	/**
	 * Returns the information about agents.
	 * 
	 * @return The list of agents
	 */
	public AgentStatus[] getAgents();

	/**
	 * Searches for all user tasks in TRMServer.
	 * 
	 * @param userId
	 *            Id of the user
	 * @return Statuses of the tasks which belongs to the specified user
	 */
	public TaskInformation[] getAllUserTasks(Long userId);

	/**
	 * Returns the task by its id
	 * 
	 * @param taskId
	 *            Id of the task
	 * @return
	 */
	public TaskInformation getTask(Long taskId);

	/**
	 * Removes completed task from server
	 * 
	 * @param taskId
	 *            Id of the completed task
	 */
	public void removeCompletedTask(Long taskId);

	/**
	 * Returns remote instance to the TRMAgent
	 * 
	 * @param id
	 *            Id of the registered agent on server
	 * @return
	 */
	public AgentInformation getAgent(Long id);

	/**
	 * Upload project to server
	 * 
	 * @param projectPath
	 * @param version
	 * @param file
	 *            the zip archive with project files
	 * @throws Exception
	 */
	public void uploadProject(String projectPath, String version, RemoteFile file) throws Exception;

	/**
	 * Returns array of tasks which belong to the specified task
	 * @param parentTaskId Id of parent task. In case if it specified as null then only high-level task will be returned
	 * @return
	 */
    public TaskInformation[] getTasks(Long parentTaskId);
}
