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

	/**
     * 
     */
	private static final long serialVersionUID = 1141848202782406169L;


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
