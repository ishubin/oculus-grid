package net.mindengine.oculus.grid.runner;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

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

	public DefaultOculusRunner() throws RemoteException {
		setName("DefaultOculusRunner_" + (new Date()).getTime());
	}

	public static void main(String[] args) throws Exception {
		OculusRunner oculusRunner = new DefaultOculusRunner();
		try {
			oculusRunner.start(args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			UnicastRemoteObject.unexportObject(oculusRunner, false);
			System.exit(0);
		}
	}

	@Override
	public void run() throws RemoteException {
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
