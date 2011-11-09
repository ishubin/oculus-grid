package net.mindengine.oculus.grid.agent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.agent.taskrunner.TaskRunner;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.domain.task.suite.SuiteTask;
import net.mindengine.oculus.grid.service.AgentOculusRunnerRemoteInterface;
import net.mindengine.oculus.grid.service.AgentServerRemoteInterface;
import net.mindengine.oculus.grid.service.ServerAgentRemoteInterface;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Test Run Manager Agent.<br>
 * Used for running tasks and automation suites.<br>
 * Manages the RMI connection with the server. All the RMI configuration is
 * defined in "agent.properties" file
 * 
 * @author Ivan Shubin
 * 
 */
public class TRMAgent extends UnicastRemoteObject implements ServerAgentRemoteInterface, AgentTestRunnerListener {
	private static final long serialVersionUID = 4670539314743293843L;
	private Log logger = LogFactory.getLog(getClass());

	private AgentInformation agentInformation = new AgentInformation();
	private AgentServerRemoteInterface server;
	private AgentOculusRunnerRemoteInterface oculusRunnerRemoteInterface = null;
	private Properties properties;

	private AgentConnectionChecker agentConnectionChecker = new AgentConnectionChecker();
	private Task task;
	private TaskStatus taskStatus;
	private Long id;

	private ReentrantLock uploadProjectsLock = new ReentrantLock();
	private Collection<UploadProjectData> uploadProjects = new LinkedList<UploadProjectData>();

	/**
	 * Abstract task runner which will be instantiated with each new task
	 */
	private TaskRunner taskRunner;

	protected TRMAgent(AgentServerRemoteInterface server, Properties properties) throws RemoteException {
		super();
		this.server = server;
		this.properties = properties;
	}

	public void startConnection() throws Exception {

		// Detecting the machines name
		agentInformation.detectHostName();
		agentInformation.setName(properties.getProperty("agent.name"));
		agentInformation.setDescription(properties.getProperty("agent.description"));

		logger.info("Starting agent: " + agentInformation);

		id = server.registerAgent(agentInformation, this);
		logger.info("Registered on server with id = " + id);
	}

	@Override
	public AgentStatus getAgentStatus() throws Exception {
		AgentStatus agentStatus = new AgentStatus();
		agentStatus.setAgentInformation(agentInformation);

		if (task != null) {
			agentStatus.setState(AgentStatus.BUSY);
		}
		else
			agentStatus.setState(AgentStatus.FREE);

		return agentStatus;
	}

	@Override
	public void stopAgent() throws RemoteException {
		UnicastRemoteObject.unexportObject(this, false);
		System.exit(0);
	}

	/**
	 * Checks whether there are projects to be uploaded in the list and
	 * uploads them
	 */
	public void checkUploadedProjects() {
		uploadProjectsLock.lock();
		try{
			Iterator<UploadProjectData> it = uploadProjects.iterator();
			while(it.hasNext()){
				UploadProjectData upd = it.next();
				uploadProjectToSystem(upd);
				it.remove();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			uploadProjectsLock.unlock();
		}
	}

	@Override
	public void runTask(Task task) throws Exception {
		/*
		 * Checking if there are any projects that should be uploaded. Doing
		 * this here because for now it is the only safe place to upload
		 * project. If we try to do it on the task completion it will not be
		 * possible to remove {project}-{version}.jar file as it still will be
		 * used for few seconds by oculus runner
		 */
		checkUploadedProjects();

		this.task = task;
		logger.info("Running task " + task);
		taskStatus = new TaskStatus();
		taskRunner = TaskRunner.createTaskRunner(task);
		taskRunner.setAgent(this);
		if (task instanceof SuiteTask) {
			SuiteTask suiteTask = (SuiteTask) task;

			suiteTask.getSuite().getSuite().setAgentName(agentInformation.getName());
		}
		// The agent properties will be needed for launching the Process in
		// SuiteTaskRunner
		taskRunner.setAgentProperties(properties);
		taskRunner.start();
	}

	@Override
	public void stopTask() throws RemoteException {
		if (oculusRunnerRemoteInterface != null) {
			logger.info("Stoping current task");
			oculusRunnerRemoteInterface.stopSuite();
		}
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setServer(AgentServerRemoteInterface server) {
		this.server = server;
	}

	public AgentServerRemoteInterface getServer() {
		return server;
	}

	@Override
	public void onTestAction(String name, Integer percent) {
		logger.info(name);

		taskStatus.setStatus(TaskStatus.ACTIVE);
		try {
			server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFinished(String name, Long id, Integer status) {
		logger.info(name);

		taskStatus.getCompletedTests().add(name);
		if (task instanceof SuiteTask) {
			SuiteTask suiteTask = (SuiteTask) task;
			int testsAmount = suiteTask.getSuite().getSuite().getTestsMap().size();
			if (testsAmount > 0) {
				taskStatus.setPercent((taskStatus.getCompletedTests().size() * 100) / testsAmount);
			}
		}
		try {
			server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStarted(String name, Long id) {
		logger.info(name);
	}

	@Override
	public void onTaskFinished(Long suiteId) throws RemoteException {
		logger.info("Task is finished");
		taskStatus.setStatus(TaskStatus.COMPLETED);
		taskStatus.setPercent(100);
		taskStatus.setSuiteId(suiteId);
		try {
			server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		task = null;

	}

	@Override
	public void setOculusRunner(AgentOculusRunnerRemoteInterface oculusRunnerRemoteInterface) throws RemoteException {
		this.oculusRunnerRemoteInterface = oculusRunnerRemoteInterface;
	}

	public Long getId() {
		return id;
	}

	public static void verifyResource(Properties properties, String key) throws Exception {
		String path = properties.getProperty(key);
		if (path == null)
			throw new Exception("The " + key + " property wasn't specified");

		File file = new File(path);
		if (!file.exists()) {
			throw new Exception("The " + key + " property refers to unexistent path: " + path);
		}
		else if (!file.isDirectory()) {
			throw new Exception("The " + key + " property refers to not a directory: " + path);
		}
	}

	public static boolean contains(File[] files, String libName) {
		for (File f : files) {
			if (f.isFile()) {
				String str = f.getName().toLowerCase();
				if (str.endsWith(".jar")) {
					if (str.startsWith(libName)) {
						return true;
					}
				}

			}
		}
		return false;
	}

	public static void verifyOculusLibrary(File[] files, String libName) throws Exception {
		if (!contains(files, libName)) {
			throw new Exception("The agent.oculus.library directory should contain " + libName + " library");
		}
	}

	public static void verifyOculusLibrary(Properties properties) throws Exception {
		String path = properties.getProperty("agent.oculus.library");
		File file = new File(path);
		File[] files = file.listFiles();

		verifyOculusLibrary(files, "oculus-test-run-manager");
		verifyOculusLibrary(files, "oculus-test-run-framework");
		verifyOculusLibrary(files, "commons-beanutils");
		verifyOculusLibrary(files, "commons-digester");
		verifyOculusLibrary(files, "commons-lang");
		verifyOculusLibrary(files, "commons-logging");
		verifyOculusLibrary(files, "mysql-connector-java-5.0.6-bin");
		verifyOculusLibrary(files, "spring.jar");
		verifyOculusLibrary(files, "spring-jdbc.jar");

	}

	/**
	 * This method will be used each 30 seconds after the connection to
	 * TRMServer was lost
	 * 
	 * @throws Exception
	 */
	public void reconnect() throws Exception {
		String serverAddress = "rmi://" + properties.getProperty("server.host") + "/" + properties.getProperty("server.name");
		logger.info("Connecting to " + serverAddress);

		Registry registry = LocateRegistry.getRegistry(properties.getProperty("server.host"), Integer.parseInt(properties.getProperty("server.port")));
		this.server = (AgentServerRemoteInterface) registry.lookup(serverAddress);

		startConnection();
	}

	public static void main(String[] args) throws Exception {
		Log logger = LogFactory.getLog("");
		Properties properties = new Properties();
		properties.load(new FileReader(new File(GridUtils.getMandatoryResourceFile(TRMAgent.class, "/agent.properties"))));

		verifyResource(properties, "agent.oculus.library");
		verifyResource(properties, "agent.projects.library");
		verifyOculusLibrary(properties);

		String serverAddress = "rmi://" + properties.getProperty("server.host") + "/" + properties.getProperty("server.name");

		System.setProperty("java.security.policy", GridUtils.getMandatoryResourceFile(TRMAgent.class, "/agent_security.policy"));

		TRMAgent agent = null;

		try {
			logger.info("Connecting to " + serverAddress);

			Registry registry = LocateRegistry.getRegistry(properties.getProperty("server.host"), Integer.parseInt(properties.getProperty("server.port")));
			AgentServerRemoteInterface server = (AgentServerRemoteInterface) registry.lookup(serverAddress);
			agent = new TRMAgent(server, properties);

			agent.startConnection();
			agent.agentConnectionChecker.setAgent(agent);
			agent.agentConnectionChecker.start();
			logger.info("Registered in " + properties.getProperty("server.name"));
		}
		catch (Exception e) {
			if (agent != null) {
				UnicastRemoteObject.unexportObject(agent, false);
			}
			throw e;
		}
	}

	public void uploadProjectToSystem(UploadProjectData upd) throws Exception {
		logger.info("Uploading project content: " + upd.getPath() + " version: " + upd.getVersion());
		String projectLibraryPath = properties.getProperty("agent.projects.library");
		File projectDir = new File(projectLibraryPath + File.separator + upd.getPath());
		if (!projectDir.exists()) {
			projectDir.mkdir();
		}

		File projectVersionDir = new File(projectLibraryPath + File.separator + upd.getPath() + File.separator + upd.getPath() + "-" + upd.getVersion());

		if (projectVersionDir.exists()) {
			FileUtils.deleteDirectory(projectVersionDir);
		}
		projectVersionDir.mkdir();

		extractZip(upd, projectVersionDir.getAbsolutePath());
	}

	@Override
	public void uploadProject(String projectPath, String version, byte[] zippedContent) throws Exception {
		/*
		 * First will have to check that the agent is not running right now.
		 * Only when the agent is free - it is possible to upload project.
		 */
		logger.info("Received project content: " + projectPath + " version: " + version);
		UploadProjectData upd = new UploadProjectData(projectPath, version);

		saveZip("temp_" + upd.getPath() + "_" + upd.getVersion() + ".zip", zippedContent);

		if (getAgentStatus().getState() == AgentStatus.FREE) {
			uploadProjectToSystem(upd);
		}
		else {
			logger.info("Agent is busy. Project upload is postponed");
			uploadProjectsLock.lock();
			try {
				if (!uploadProjects.contains(upd)) {
					uploadProjects.add(upd);
				}
			}
			catch (Exception e) {
				throw e;
			}
			finally {
				uploadProjectsLock.unlock();
			}

		}
	}

	public static void saveZip(String path, byte[] bytes) throws IOException {
		File fileTemp = new File(path);
		if (fileTemp.exists()) {
			fileTemp.delete();
		}
		fileTemp.createNewFile();

		FileOutputStream fos = new FileOutputStream(fileTemp);
		fos.write(bytes);
		fos.flush();
		fos.close();
	}

	public static void extractZip(UploadProjectData upd, String dirPath) throws IOException {
		ZipFile zipFile = new ZipFile("temp_" + upd.getPath() + "_" + upd.getVersion() + ".zip");
		Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			File file = new File(dirPath + File.separator + entry.getName());
			if (entry.isDirectory()) {
				file.mkdir();
			}
			else {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				int size = 2048;
				byte[] buffer = new byte[size];

				BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
				BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
				while ((size = bis.read(buffer, 0, buffer.length)) != -1) {
					bos.write(buffer, 0, size);
				}
				bos.flush();
				bos.close();
				fos.close();
				bis.close();
			}

			System.out.println(entry.getName());
		}
	}

}
