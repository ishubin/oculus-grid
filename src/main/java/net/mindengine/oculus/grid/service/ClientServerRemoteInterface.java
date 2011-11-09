package net.mindengine.oculus.grid.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.server.TaskHandler;

/**
 * Used for messages from client to server specification
 * 
 * @author Ivan Shubin
 */
public interface ClientServerRemoteInterface extends Remote {

	/**
	 * Puts the task in tasks queue so it will be be picked up later by the
	 * {@link TaskHandler}
	 * 
	 * @param task
	 *            Task for execution
	 * 
	 * @return The unique id of the task in a task queue
	 * @see TaskHandler
	 * @throws Exception
	 */
	public Long runTask(Task task) throws Exception;

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
	 * Saves the task in a scheduler.
	 * 
	 * @param task
	 *            Task for scheduler
	 * @return The unique id of the task in a scheduler
	 * 
	 * @throws Exception
	 */
	public Long saveScheduledTask(Task task) throws Exception;

	/**
	 * Returns the task with specified id from scheduler
	 * 
	 * @param taskId
	 *            Id of the task
	 * @return Task with specified id from scheduler
	 * @throws Exception
	 */
	public Task getScheduledTask(Long taskId) throws Exception;

	/**
	 * Deletes the specified task from scheduler
	 * 
	 * @param taskId
	 *            The unique id of the task in a scheduler
	 * 
	 * @throws Exception
	 */
	public void deleteScheduledTask(Long taskId) throws Exception;

	/**
	 * Returns the tasks from scheduler
	 * 
	 * @return List of tasks from scheduler
	 * 
	 * @throws Exception
	 */
	public Collection<Task> getScheduledTasks() throws Exception;

	/**
	 * Returns list of all tasks
	 * 
	 * @return List of all tasks
	 * @throws RemoteException
	 */
	public Collection<Task> getTasksList() throws RemoteException;

	/**
	 * Returns the information about agents.
	 * 
	 * @return The list of agents
	 * @throws RemoteException
	 */
	public Collection<AgentStatus> getAgents() throws RemoteException;

	/**
	 * Searches for all user tasks in TRMServer.
	 * 
	 * @param userId
	 *            Id of the user
	 * @return Statuses of the tasks which belongs to the specified user
	 * @throws RemoteException
	 */
	public Collection<Task> getAllUserTasks(Long userId) throws RemoteException;

	/**
	 * Returns the task by its id
	 * 
	 * @param taskId
	 *            Id of the task
	 * @return
	 * @throws RemoteException
	 */
	public Task getTask(Long taskId) throws RemoteException;

	/**
	 * Removes completed task from server
	 * 
	 * @param taskId
	 *            Id of the completed task
	 * @throws RemoteException
	 */
	public void removeCompletedTask(Long taskId) throws RemoteException;

	/**
	 * Returns remote instance to the TRMAgent
	 * 
	 * @param id
	 *            Id of the registered agent on server
	 * @return
	 * @throws RemoteException
	 */
	public ServerAgentRemoteInterface getAgent(Long id) throws RemoteException;

	/**
	 * Upload project to server
	 * 
	 * @param projectPath
	 * @param version
	 * @param zippedContent
	 *            the zip archive with project files
	 * @throws Exception
	 */
	public void uploadProject(String projectPath, String version, byte[] zippedContent) throws Exception;
}
