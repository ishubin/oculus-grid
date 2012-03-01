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
* along with Oculus Grid.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
package net.mindengine.oculus.grid.runner;

import net.mindengine.oculus.experior.test.TestLauncher;

/**
 * This class is used for running the tests and obtaining the information about
 * tests via RMI from TRMAgent. Will be used by {@link #TRMAgent}
 * 
 * @author ishubin
 * 
 */
public class DefaultOculusRunner extends OculusRunner {

	public static void main(String[] args) throws Exception {
		OculusRunner oculusRunner = new DefaultOculusRunner();
	    String agentHost = args[0];
        Integer agentPort = Integer.parseInt(args[1]);
        String agentName = args[2];
        String suitePath = null;
        if (args.length > 3) {
            suitePath = args[3];
        }
        else {
            suitePath = "suite.xml";
        }
		oculusRunner.start(agentHost, agentPort, agentName, suitePath);
	}

	@Override
	public void run()  {
		System.setErr(System.out);
		try {
			TestLauncher testLauncher = new TestLauncher();
			testLauncher.setSuite(getSuite());
			testLauncher.setTestRunListener(this);
			testLauncher.setSuiteInterruptListener(this);
			testLauncher.launch();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			getAgentTestRunnerListener().onTaskFinished(getSuite().getId());
		}
	}
}
