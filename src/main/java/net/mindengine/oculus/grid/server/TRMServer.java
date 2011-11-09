package net.mindengine.oculus.grid.server;

import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.console.ConsoleCommandScanner;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.MultiTask;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.domain.task.TaskUser;
import net.mindengine.oculus.grid.service.AgentServerRemoteInterface;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;
import net.mindengine.oculus.grid.service.ServerAgentRemoteInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test Run Manager Server is an RMI remote object which manages task and
 * automation suites between agents
 * 
 * @author Ivan Shubin
 * 
 */
public class TRMServer extends UnicastRemoteObject implements ClientServerRemoteInterface, AgentServerRemoteInterface {
	private static final long serialVersionUID = 699877285660409518L;

	private Log logger = LogFactory.getLog(getClass());
	private Scheduler scheduler;
	private TaskContainer taskContainer = new TaskContainer();
	private AgentContainer agentContainer = new AgentContainer();
	private AgentHandler agentHandler = new AgentHandler();
	private TaskHandler taskHandler = new TaskHandler(this);
	private CompletedTaskCleaner completedTaskCleaner = new CompletedTaskCleaner(this);
	private ServerConsoleHandler consoleHandler = new ServerConsoleHandler(this);

	/**
	 * The amount of time in minutes in which the completed tasks will be
	 * removed from TRMServer. Configured in server.properties file
	 */
	private Long storeCompletedTasksTime = 300000L;

	protected TRMServer() throws RemoteException {
		super();
	}

	/**
	 * Handles the console reading and executes the console command
	 * 
	 * @param properties
	 * 
	 * @throws Exception
	 */
	private void handle(Properties properties) throws Exception {
		agentHandler.setAgentContainer(agentContainer);
		agentHandler.setTaskContainer(taskContainer);
		agentHandler.start();

		taskHandler.setAgentContainer(agentContainer);
		taskHandler.setTaskContainer(taskContainer);
		taskHandler.start();

		if (storeCompletedTasksTime != null) {
			completedTaskCleaner.setTaskContainer(taskContainer);
			completedTaskCleaner.start();
		}

		File schedulerFile = new File("server.scheduler");
		if (schedulerFile.exists()) {
			try {
				scheduler = Scheduler.importScheduler(schedulerFile);

			}
			catch (Exception e) {
				logger.info("Scheduler couldn't be read from file. Instantiating Scheduler");
				scheduler = new Scheduler();
			}
		}
		else
			scheduler = new Scheduler();

		String strSchedulerAutosaveTime = properties.getProperty("server.scheduler.autosave.time");
		if (strSchedulerAutosaveTime != null && !strSchedulerAutosaveTime.isEmpty()) {
			try {
				scheduler.setSaveTimePeriod(Integer.parseInt(strSchedulerAutosaveTime));
			}
			catch (Exception e) {
				throw new Exception("Can't read server.scheduler.autosave.time from properties");
			}
		}
		scheduler.setServer(this);
		scheduler.start();

		ConsoleCommandScanner commandScanner = new ConsoleCommandScanner();
		commandScanner.setHandler(consoleHandler);
		commandScanner.start();
	}

	@Override
	public void deleteScheduledTask(Long taskId) throws Exception {
		scheduler.tasksLock.lock();
		try {
			scheduler.getTasks().remove(taskId);
		}
		catch (Throwable e) {
			throw new Exception(e);
		}
		finally {
			scheduler.tasksLock.unlock();
		}
	}

	@Override
	public Task getScheduledTask(Long taskId) throws Exception {
		scheduler.tasksLock.lock();
		Task task = null;
		try {
			return scheduler.getTasks().get(taskId);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scheduler.tasksLock.unlock();
		}
		return task;
	}

	@Override
	public Long saveScheduledTask(Task task) throws Exception {
		/*
		 * The following row of the code is needed to have an ability to change
		 * tasks and not only to create.
		 */

		scheduler.tasksLock.lock();
		try {
			if (task.getId() == null) {
				/*
				 * Creating a unique task id
				 */
				Long uniqueId = new Date().getTime();
				if (scheduler.getTasks().containsKey(uniqueId)) {
					Thread.sleep(100);
					uniqueId = new Date().getTime();
				}
				if (scheduler.getTasks().containsKey(uniqueId)) {
					throw new Exception("Couldn't save task to scheduler. Couldn't create a unique task ID");
				}
				task.setId(uniqueId);
			}

			scheduler.addTask(task);
		}
		catch (Throwable e) {
			throw new Exception(e);
		}
		finally {
			scheduler.tasksLock.unlock();
		}
		return task.getId();
	}

	@Override
	public Collection<Task> getScheduledTasks() throws Exception {
		scheduler.tasksLock.lock();
		Collection<Task> tasks = null;
		try {
			Collection<Task> tempTasks = scheduler.getTasks().values();
			tasks = new ArrayList<Task>();
			for (Task task : tempTasks) {
				tasks.add(task);
			}
		}
		catch (Throwable e) {
			throw new Exception(e);
		}
		finally {
			scheduler.tasksLock.unlock();
		}
		return tasks;
	}

	@Override
	public TaskStatus getTaskStatus(Long taskId) throws Exception {
		TaskWrapper taskWrapper = taskContainer.getTask(taskId);

		if (taskWrapper.getTask() instanceof MultiTask) {
			taskWrapper.taskLock.lock();
			try {
				((MultiTask) taskWrapper.getTask()).updateTaskStatus();
			}
			catch (Exception e) {

				logger.error(e);
				throw e;
			}
			finally {
				taskWrapper.taskLock.unlock();
			}
		}
		if (taskWrapper != null) {
			TaskStatus taskStatus = taskWrapper.getTask().getTaskStatus();
			taskStatus.setTaskName(taskWrapper.getTask().getName());
			taskStatus.setTaskId(taskWrapper.getId());
			return taskStatus;
		}
		return null;
	}

	@Override
	public Long runTask(Task task) throws Exception {
		logger.info("Recieved new task");
		TaskWrapper taskWrapper = new TaskWrapper();
		taskWrapper.setTask(task);

		return taskContainer.registerNewTask(taskWrapper);
	}

	/**
	 * Stops all tasks. Moves all queued tasks to completed list.
	 * 
	 * @param task
	 * @throws Exception
	 */
	private void stopTask(TaskWrapper task) throws Exception {
		task.getTask().getTaskStatus().setInterrupted(true);
		if (task != null) {
			logger.info("Stoping task " + task.getId());

			if (TaskStatus.WAITING.equals(task.getTask().getTaskStatus().getStatus())) {
				taskContainer.getQueuedTasks().remove(task);
				taskContainer.moveTaskToCompleted(task);
			}

			if (task.getTask() instanceof MultiTask) {
				for (TaskWrapper childTask : task.getChildren()) {
					stopTask(childTask);
				}
			}
			else {
				AgentWrapper agent = task.getAssignedAgent();
				if (agent != null) {
					agent.getAgentRemoteInterface().stopTask();
				}
			}
		}
	}

	@Override
	public void stopTask(Long taskId) throws Exception {
		TaskWrapper task = taskContainer.getTask(taskId);
		stopTask(task);
	}

	@Override
	public Collection<Task> getTasksList() throws RemoteException {
		taskContainer.getTaskLock().lock();
		Collection<Task> tasksList = null;
		try {
			Collection<TaskWrapper> tasks = this.taskContainer.getTasks().values();
			tasksList = new ArrayList<Task>();
			for (TaskWrapper tw : tasks) {
				tasksList.add(tw.getTask());
			}
		}
		catch (Throwable e) {
			throw new RemoteException(e.getMessage());
		}
		finally {
			taskContainer.getTaskLock().unlock();
		}
		return tasksList;
	}

	@Override
	public Collection<Task> getAllUserTasks(Long userId) throws RemoteException {
		taskContainer.getTaskLock().lock();
		Collection<Task> tasksList = null;
		try {
			Collection<TaskWrapper> tasks = taskContainer.getTasks().values();
			tasksList = new LinkedList<Task>();
			for (TaskWrapper taskWrapper : tasks) {
				/*
				 * Only parent tasks should be returned to the user as they
				 * contain all their child tasks
				 */
				if (taskWrapper.getTask().getParent() == null) {
					TaskUser taskUser = taskWrapper.getTask().getTaskUser();
					if (taskUser == null) {
						throw new NullPointerException("The taskUser is null");

					}
					if (userId.equals(taskUser.getId())) {
						if (taskWrapper.getTask() instanceof MultiTask) {
							MultiTask multiTask = (MultiTask) taskWrapper.getTask();
							multiTask.updateTaskStatus();
						}
						tasksList.add(taskWrapper.getTask());
					}
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}
		finally {
			taskContainer.getTaskLock().unlock();
		}
		return tasksList;
	}

	@Override
	public Task getTask(Long taskId) throws RemoteException {
		taskContainer.getTaskLock().lock();
		Task task = null;
		try {
			TaskWrapper taskWrapper = taskContainer.getTask(taskId);
			if (taskWrapper != null) {
				task = taskWrapper.getTask();
			}
		}
		catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		finally {
			taskContainer.getTaskLock().unlock();
		}

		return task;
	}

	@Override
	public void removeCompletedTask(Long taskId) throws RemoteException {
		taskContainer.removeCompletedTask(taskId);
	}

	@Override
	public Collection<AgentStatus> getAgents() throws RemoteException {
		agentContainer.getAgentLock().lock();
		Collection<AgentStatus> agentList = null;
		try {
			Collection<AgentWrapper> agents = agentContainer.getAgents().values();
			agentList = new ArrayList<AgentStatus>();
			for (AgentWrapper aw : agents) {
				AgentStatus as = new AgentStatus();
				as.setAgentInformation(aw.getAgentInformation());
				as.setState(aw.getStatus());
				agentList.add(as);
			}

		}
		catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		finally {
			agentContainer.getAgentLock().unlock();
		}
		return agentList;
	}

	@Override
	public Long registerAgent(AgentInformation agentInformation, ServerAgentRemoteInterface agentRemoteInterface) throws Exception {
		Long agentId = agentContainer.registerAgent(agentInformation, agentRemoteInterface);
		logger.info("Agent " + agentInformation.getName() + " was registered");
		return agentId;
	}

	@Override
	public void updateTaskStatus(Long taskId, TaskStatus taskStatus) throws Exception {
		TaskWrapper taskWrapper = taskContainer.getTask(taskId);
		if (taskWrapper != null) {
			taskWrapper.taskLock.lock();
			try {

				taskStatus.setAssignedAgent(taskWrapper.getAssignedAgent().getAgentInformation());
				taskWrapper.getTask().setTaskStatus(taskStatus);

				if (taskStatus.getStatus().equals(TaskStatus.COMPLETED)) {
					AgentWrapper agent = taskWrapper.getAssignedAgent();
					agentContainer.freeAgent(agent.getAgentId());
					taskContainer.moveTaskToCompleted(taskWrapper);
					logger.info("Task " + taskId + " is completed");
				}
				/*
				 * Updating the status of parent task if it exists
				 */
				MultiTask parentTask = taskWrapper.getTask().getParent();
				if (parentTask != null) {
					parentTask.updateTaskStatus();
				}
			}
			catch (Exception e) {
				throw e;
			}
			finally {
				taskWrapper.taskLock.unlock();
			}
		}
		else
			logger.info("Couldn't find task with id: " + taskId);
	}

	public static void main(String[] args) throws Exception {
		TRMServer server = new TRMServer();
		
		System.setProperty("java.security.policy", GridUtils.getMandatoryResourceFile(TRMServer.class, "/server_security.policy"));

		/*
		 * Loading properties file
		 */
		Properties properties = new Properties();
		properties.load(new FileReader(new File(GridUtils.getMandatoryResourceFile(TRMServer.class, "/server.properties"))));

		Integer port = Integer.parseInt(properties.getProperty("server.port"));

		String strStoreCompletedTasksTime = properties.getProperty("server.store.completed.tasks.time");
		if (strStoreCompletedTasksTime == null || strStoreCompletedTasksTime.isEmpty()) {
			server.setStoreCompletedTasksTime(null);
		}
		else
			server.setStoreCompletedTasksTime(Long.parseLong(strStoreCompletedTasksTime));

		/*
		 * Detecting the server host
		 */
		String host;
		String hostProperty = properties.getProperty("server.host");
		if (hostProperty != null && !hostProperty.isEmpty()) {
			host = hostProperty;
		}
		else {
			server.logger.info("Detecting IP address");
			InetAddress inetAddress = InetAddress.getLocalHost();
			host = inetAddress.getHostAddress();
		}
		server.logger.info("Host: " + host);
		server.logger.info("Creating registry on port: " + port);
		Registry registry = LocateRegistry.createRegistry(port);

		String bindName = "rmi://" + host + "/" + properties.getProperty("server.name");
		server.logger.info("Binding on " + bindName);
		registry.rebind(bindName, server);

		server.logger.info("Starting server");
		server.handle(properties);
		
	}

	public void quit() {

		agentHandler.setEnabled(false);
		System.exit(0);
	}

	protected TaskContainer getTaskContainer() {
		return taskContainer;
	}

	protected AgentContainer getAgentContainer() {
		return agentContainer;
	}

	@Override
	public ServerAgentRemoteInterface getAgent(Long id) throws RemoteException {
		logger.info("Searching for agent: " + id);
		agentContainer.getAgentLock().lock();
		AgentWrapper agentWrapper = agentContainer.getAgents().get(id);
		agentContainer.getAgentLock().unlock();

		if (agentWrapper != null) {
			return agentWrapper.getAgentRemoteInterface();
		}

		return null;
	}

	@Override
	public void uploadProject(String projectPath, String version, byte[] zippedContent) throws Exception {
		List<AgentWrapper> agentList = new LinkedList<AgentWrapper>();
		/*
		 * Collecting agent to another list to avoid thread problems
		 */
		agentContainer.getAgentLock().lock();
		for (AgentWrapper agetWrapper : agentContainer.getAgents().values()) {
			agentList.add(agetWrapper);
		}
		agentContainer.getAgentLock().unlock();

		/*
		 * Uploading the project to each agent
		 */
		for (AgentWrapper agentWrapper : agentList) {
			agentWrapper.getAgentRemoteInterface().uploadProject(projectPath, version, zippedContent);
		}
	}

	@Override
	public String checkConnection() throws Exception {
		return "OK";
	}

	public void setStoreCompletedTasksTime(Long storeCompletedTasksTime) {
		this.storeCompletedTasksTime = storeCompletedTasksTime;
	}

	public Long getStoreCompletedTasksTime() {
		return storeCompletedTasksTime;
	}
}
