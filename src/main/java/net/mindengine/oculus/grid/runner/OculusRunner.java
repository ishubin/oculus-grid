package net.mindengine.oculus.grid.runner;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import net.mindengine.oculus.experior.SuiteInterruptListener;
import net.mindengine.oculus.experior.TestRunListener;
import net.mindengine.oculus.experior.suite.Suite;
import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.experior.test.descriptors.ActionInformation;
import net.mindengine.oculus.experior.test.descriptors.TestDefinition;
import net.mindengine.oculus.experior.test.descriptors.TestInformation;
import net.mindengine.oculus.grid.agent.AgentTestRunnerListener;
import net.mindengine.oculus.grid.service.AgentOculusRunnerRemoteInterface;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

/**
 * This can be used as a superclass for all task runners.
 * 
 * @author Ivan Shubin
 * 
 */
public abstract class OculusRunner extends UnicastRemoteObject implements TestRunListener, SuiteInterruptListener, AgentOculusRunnerRemoteInterface {
	private static final long serialVersionUID = -750851021970496017L;

	private String name;
	private Suite suite;
	private boolean shouldInterrupt = false;
	private AgentTestRunnerListener agentTestRunnerListener;

	public OculusRunner() throws RemoteException {
		super();
		setName("OculusRunner_" + (new Date()).getTime());
	}

	public abstract void run() throws RemoteException;

	/**
	 * Handles the connection with TRMAgent
	 * 
	 * @param args
	 *            Command line arguments passed by TRMAgent
	 * @param filePath
	 *            Path to the suite xml file
	 * @throws Exception
	 */
	public void start(String[] args) throws Exception {
		String serverHost = args[0];
		String serverPort = args[1];
		String serverName = args[2];
		Long agentId = Long.parseLong(args[3]);

		String suitePath = null;
		if (args.length > 4) {
			suitePath = args[4];
		}
		else {
			suitePath = "suite.xml";
		}

		System.out.println("Locating registry on " + serverHost + " port " + serverPort);
		Registry registry = LocateRegistry.getRegistry(serverHost, Integer.parseInt(serverPort));
		if (registry == null)
			throw new Exception("Agent wasn't found");

		String serverPath = "rmi://" + serverHost + "/" + serverName;
		System.out.println("Looking up for " + serverPath);
		ClientServerRemoteInterface server = (ClientServerRemoteInterface) registry.lookup(serverPath);
		if (server == null)
			throw new Exception("Server wasn't found");

		System.out.println("Searching for agent: " + agentId);
		AgentTestRunnerListener agent = (AgentTestRunnerListener) server.getAgent(agentId);
		if (agent == null)
			throw new Exception("Agent wasn't found on server");
		System.out.println("Agent was found");

		agent.setOculusRunner(this);
		agentTestRunnerListener = agent;

		System.out.println("Parsing suite in " + suitePath);

		suite = XmlSuiteParser.parse(new File(suitePath));
		System.out.println("Agent is " + suite.getAgentName());
		run();
	}

	@Override
	public void onTestAction(ActionInformation actionInformation) {
		try {
			agentTestRunnerListener.onTestAction(actionInformation.getActionName(), 0);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestFinished(TestInformation testInformation) {
		try {
			agentTestRunnerListener.onTestFinished(testInformation.getTestName(), testInformation.getTestDefinition().getCustomId(), testInformation.getStatus());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStarted(TestInformation testInformation) {
		try {
			agentTestRunnerListener.onTestStarted(testInformation.getTestName(), testInformation.getTestDefinition().getCustomId());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean proceedTest(TestDefinition testDefinition) {
		if (shouldInterrupt) {
			System.out.println("************************\nInterrupting\n********************************");
		}
		return !shouldInterrupt;
	}

	@Override
	public void stopSuite() throws RemoteException {
		System.out.println("************************\nStoping Suite\n********************************");
		shouldInterrupt = true;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean proceedSuite() {
		if (shouldInterrupt) {
			System.out.println("************************\nInterrupting\n********************************");
		}
		return !shouldInterrupt;
	}

	public Suite getSuite() {
		return suite;
	}

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public AgentTestRunnerListener getAgentTestRunnerListener() {
		return agentTestRunnerListener;
	}

	public void setAgentTestRunnerListener(AgentTestRunnerListener agentTestRunnerListener) {
		this.agentTestRunnerListener = agentTestRunnerListener;
	}

	public boolean getShouldInterrupt() {
		return shouldInterrupt;
	}

	public void setShouldInterrupt(boolean shouldInterrupt) {
		this.shouldInterrupt = shouldInterrupt;
	}

}
