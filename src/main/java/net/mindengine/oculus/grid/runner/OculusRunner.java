package net.mindengine.oculus.grid.runner;

import java.io.File;

import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.oculus.experior.SuiteInterruptListener;
import net.mindengine.oculus.experior.TestRunListener;
import net.mindengine.oculus.experior.suite.Suite;
import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.experior.test.descriptors.ActionInformation;
import net.mindengine.oculus.experior.test.descriptors.TestDefinition;
import net.mindengine.oculus.experior.test.descriptors.TestInformation;
import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.agent.AgentTestRunnerListener;

/**
 * This can be used as a superclass for all task runners.
 * 
 * @author Ivan Shubin
 * 
 */
public abstract class OculusRunner implements TestRunListener, SuiteInterruptListener {
	private static final long serialVersionUID = -750851021970496017L;

	private Suite suite;
	private AgentTestRunnerListener agentTestRunnerListener;

	public OculusRunner() {
	}

	public abstract void run();

	/**
	 * Handles the connection with TRMAgent
	 * 
	 * @param args
	 *            Command line arguments passed by TRMAgent
	 * @param filePath
	 *            Path to the suite xml file
	 * @throws Exception
	 */
	public void start(String agentHost, Integer agentPort, String agentName, String suitePath) throws Exception {
		System.out.println("Locating agent on " + agentHost + " port " + agentPort);
		Lookup lookup = GridUtils.createDefaultLookup();
		lookup.setUrl("http://"+agentHost+":"+agentPort);
		
		AgentTestRunnerListener agent = lookup.getRemoteObject(agentName, AgentTestRunnerListener.class);
		if (agent == null)
			throw new Exception("Agent wasn't found on server");
		System.out.println("Agent was found");

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

	private boolean checkProceed() {
	    try {
            return agentTestRunnerListener.shouldProceed();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return true;
	}
	
	@Override
	public boolean proceedTest(TestDefinition testDefinition) {
	    return checkProceed();
	}


	@Override
	public boolean proceedSuite() {
		return checkProceed();
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


}
