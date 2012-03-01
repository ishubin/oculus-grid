/*******************************************************************************
* 2012 Ivan Shubin http://mindengine.net
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
import net.mindengine.oculus.grid.domain.task.TestStatus;

/**
 * This can be used as a superclass for all task runners.
 * 
 * @author Ivan Shubin
 * 
 */
public abstract class OculusRunner implements TestRunListener, SuiteInterruptListener {
	
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
		    TestStatus testStatus = new TestStatus();
		    testStatus.setCustomId(testInformation.getTestDefinition().getCustomId());
		    testStatus.setName(testInformation.getTestName());
		    testStatus.setMapping(testInformation.getTestDefinition().getMapping());
		    testStatus.setDescription(testInformation.getTestDefinition().getDescription());
		    testStatus.setPhase(TestInformation.PHASE_DONE);
		    testStatus.setStatus(testInformation.getStatus());
		    testStatus.setTestRunId(testInformation.getTestRunId());
			agentTestRunnerListener.onTestFinished(testStatus);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTestStarted(TestInformation testInformation) {
		try {
		    TestStatus testStatus = new TestStatus();
            testStatus.setCustomId(testInformation.getTestDefinition().getCustomId());
            testStatus.setName(testInformation.getTestName());
            testStatus.setMapping(testInformation.getTestDefinition().getMapping());
            testStatus.setDescription(testInformation.getTestDefinition().getDescription());
            testStatus.setPhase(TestInformation.PHASE_DONE);
            testStatus.setStatus(testInformation.getStatus());
			agentTestRunnerListener.onTestStarted(testStatus);
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
