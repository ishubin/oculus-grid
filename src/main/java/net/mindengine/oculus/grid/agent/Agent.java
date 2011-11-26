package net.mindengine.oculus.grid.agent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.mindengine.jeremy.bin.RemoteFile;
import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.jeremy.registry.Registry;
import net.mindengine.jeremy.starter.RegistryStarter;
import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.agent.taskrunner.TaskRunner;
import net.mindengine.oculus.grid.domain.agent.AgentId;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.agent.AgentStatus;
import net.mindengine.oculus.grid.domain.task.SuiteTask;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.domain.task.TestStatus;
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
public class Agent implements ServerAgentRemoteInterface, AgentTestRunnerListener {
	
	private Log logger = LogFactory.getLog(getClass());

	private AgentInformation agentInformation = new AgentInformation();
	private AgentServerRemoteInterface server;
	
	private Properties properties;

	private AgentConnectionChecker agentConnectionChecker = new AgentConnectionChecker();
	private Task task;
	private TaskStatus taskStatus;
	private AgentId agentId = null;

	private ReentrantLock uploadProjectsLock = new ReentrantLock();
	private Collection<UploadProjectData> uploadProjects = new LinkedList<UploadProjectData>();
	
	private String serverName;
	private String serverHost;
	private Integer serverPort;
	
	private String agentRemoteName;
    private String agentHost;
    private String agentName;
    private Integer agentPort;
    private Integer agentReconnectionTimeout = 5;
    
    
    private Registry registry = GridUtils.createDefaultRegistry();
	/**
	 * Flag which is used by the oculus-runner in order to check if it should proceed running all next tests
	 */
	private volatile Boolean shouldCurrentTaskProceed = true;
	
	/**
	 * Abstract task runner which will be instantiated with each new task
	 */
	private TaskRunner taskRunner;
	private Lookup lookup;

	public Agent() {
	}

	public void startConnection() throws Exception {
		// Detecting the machines name
	    if(agentHost==null || agentHost.trim().isEmpty()) {
	        InetAddress addr = InetAddress.getLocalHost();
	        agentHost = addr.getHostName();
	    }
	    
	    getAgentInformation().setHost(agentHost);
		getAgentInformation().setName(agentName);
		getAgentInformation().setRemoteName(agentRemoteName);
		getAgentInformation().setDescription(properties.getProperty(AgentProperties.AGENT_DESCRIPTION));
		getAgentInformation().setPort(agentPort);

		logger.info("Starting agent: " + getAgentInformation());

		//Sending also the previous agentId in case if there was a reconnection
		AgentId newAgentId = server.registerAgent(getAgentInformation(), getAgentId());
		this.setAgentId(newAgentId);
		logger.info("Registered on server with id = " + getAgentId().getId()+" and token = "+getAgentId().getToken());
	}

	@Override
	public AgentStatus getAgentStatus() throws Exception {
		AgentStatus agentStatus = new AgentStatus();
		agentStatus.setAgentInformation(getAgentInformation());

		if (task != null) {
			agentStatus.setState(AgentStatus.BUSY);
		}
		else
			agentStatus.setState(AgentStatus.FREE);

		return agentStatus;
	}

	@Override
	public void killAgent() {
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
	public void runSuiteTask(SuiteTask task) throws Exception {
	    shouldCurrentTaskProceed = true;
	    
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

			suiteTask.getSuite().setAgentName(getAgentInformation().getName());
		}
		// The agent properties will be needed for launching the Process in
		// SuiteTaskRunner
		taskRunner.setAgentProperties(properties);
		taskRunner.start();
	}

	@Override
	public void stopCurrentTask() {
		shouldCurrentTaskProceed = false;
	}
	
	@Override
	public Boolean shouldProceed() {
	    return shouldCurrentTaskProceed;
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
	public void onTestFinished(TestStatus testStatus) {
		logger.info(testStatus.getName());
		
		try {
		    taskStatus.getSuiteInformation().changeTestStatus(testStatus.getCustomId(), testStatus);
			server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStarted(TestStatus testStatus) {
		logger.info(testStatus.getName());
		try {
		    taskStatus.getSuiteInformation().changeTestStatus(testStatus.getCustomId(), testStatus);
		    server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onTaskFinished(Long suiteId) {
		logger.info("Task is finished");
		taskStatus.setStatus(TaskStatus.COMPLETED);
		try {
			server.updateTaskStatus(task.getId(), taskStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		task = null;

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
	
	/**
	 * This method will be used each 30 seconds after the connection to
	 * TRMServer is lost
	 * 
	 * @throws Exception
	 */
	public void reconnect() throws Exception {
	    String serverName =  properties.getProperty(AgentProperties.SERVER_NAME);
		logger.info("Connecting to " + serverName);
		
		this.server = lookup.getRemoteObject(serverName, AgentServerRemoteInterface.class);
		startConnection();
	}

	public void uploadProjectToSystem(UploadProjectData upd) throws Exception {
		logger.info("Uploading project content: " + upd.getPath() + " version: " + upd.getVersion());
		String projectLibraryPath = properties.getProperty(AgentProperties.AGENT_PROJECTS_LIBRARY);
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
	public void uploadProject(String projectPath, String version, RemoteFile file) throws Exception {
		/*
		 * First will have to check that the agent is not running right now.
		 * Only when the agent is free - it is possible to upload project.
		 */
		logger.info("Received project content: " + projectPath + " version: " + version);
		UploadProjectData upd = new UploadProjectData(projectPath, version);

		saveZip("temp_" + upd.getPath() + "_" + upd.getVersion() + ".zip", file.getBytes());

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
	
	public void stopAgent() throws Exception {
	    agentConnectionChecker.stopConnectionChecker();
	    registry.stop();
	}
	
	public void startAgent() throws Exception {
	    verifyResource(properties, AgentProperties.AGENT_PROJECTS_LIBRARY);
        
        Lookup lookup = GridUtils.createDefaultLookup();
        lookup.setUrl("http://"+serverHost+":"+serverPort);
        
        this.server = (AgentServerRemoteInterface) lookup.getRemoteObject(serverName, AgentServerRemoteInterface.class);
        this.lookup = lookup;
        
        registry.addObject(agentRemoteName, this);
        registry.setPort(agentPort);
        
        RegistryStarter registryStarter = new RegistryStarter();
        registryStarter.setRegistry(registry);
        
        registryStarter.startRegistry();
        int count = 0;
        while(!registryStarter.getRegistry().isRunning()) {
            //Waiting for Registry to start
            Thread.sleep(100);
            count++;
            if(count>600) {
                throw new TimeoutException("Registry is not started");
            }
        }
        
        startConnection();
        agentConnectionChecker.setAgent(this);
        agentConnectionChecker.start();
        
        
        while(true) {
            //Just a dirty hack to keep agent running
        }
	}
	
	public String getServerName() {
        return serverName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public String getAgentRemoteName() {
        return agentRemoteName;
    }

    public String getAgentHost() {
        return agentHost;
    }

    public String getAgentName() {
        return agentName;
    }

    public Integer getAgentPort() {
        return agentPort;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public void setAgentRemoteName(String agentRemoteName) {
        this.agentRemoteName = agentRemoteName;
    }

    public void setAgentHost(String agentHost) {
        this.agentHost = agentHost;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public void setAgentPort(Integer agentPort) {
        this.agentPort = agentPort;
    }

    public static void main(String[] args) throws Exception {
        Agent agent = new Agent();
        agent.properties = new Properties();
        agent.properties.load(new FileReader(new File(GridUtils.getMandatoryResourceFile(Agent.class, "/agent.properties"))));
        agent.serverHost = agent.properties.getProperty(AgentProperties.SERVER_HOST);
        agent.serverPort = Integer.parseInt(agent.properties.getProperty(AgentProperties.SERVER_PORT));
        agent.serverName = agent.properties.getProperty(AgentProperties.SERVER_NAME);
        
        agent.agentHost = agent.properties.getProperty(AgentProperties.AGENT_HOST);
        agent.agentPort = Integer.parseInt(agent.properties.getProperty(AgentProperties.AGENT_PORT));
        agent.agentName = agent.properties.getProperty(AgentProperties.AGENT_NAME);
        agent.agentRemoteName = agent.properties.getProperty(AgentProperties.AGENT_REMOTE_NAME);
        agent.agentReconnectionTimeout = Integer.parseInt(agent.properties.getProperty(AgentProperties.AGENT_RECONNECT_TIMEOUT));
        agent.startAgent();
    }

    public void setAgentInformation(AgentInformation agentInformation) {
        this.agentInformation = agentInformation;
    }

    public AgentInformation getAgentInformation() {
        return agentInformation;
    }

    public void setAgentId(AgentId agentId) {
        this.agentId = agentId;
    }

    public AgentId getAgentId() {
        return agentId;
    }

    public void setAgentReconnectionTimeout(Integer agentReconnectionTimeout) {
        this.agentReconnectionTimeout = agentReconnectionTimeout;
    }

    public Integer getAgentReconnectionTimeout() {
        return agentReconnectionTimeout;
    }
}
