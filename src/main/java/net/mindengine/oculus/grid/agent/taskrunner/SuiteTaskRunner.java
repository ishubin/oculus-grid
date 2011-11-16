package net.mindengine.oculus.grid.agent.taskrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.grid.domain.task.SuiteTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Used for running the tests with <b>Oculus Test Run Framework</b>
 * 
 * @author Ivan Shubin
 * 
 */
public class SuiteTaskRunner extends TaskRunner {
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public void run() {
		try {
			/*
			 * Creating the java process with all libraries which are needed to
			 * run the automation project: - All oculus jars
			 * (test-run-framework, test-run-manager etc.) - All project related
			 * jars
			 */

			SuiteTask task = (SuiteTask) getTask();

			// String projectJarFileName = task.getProjectName()+".jar";
			String projectFolder = task.getProjectName() + File.separator + task.getProjectName();
			if (task.getProjectVersion() != null && !task.getProjectVersion().isEmpty()) {
				projectFolder += "-" + task.getProjectVersion();
			}
			else {
				projectFolder += "-current";
			}

			String projectsLibraryPath = getAgentProperties().getProperty("agent.projects.library");
			String oculusLibraryPath = getAgentProperties().getProperty("agent.oculus.library");

			String currentProjectDir = projectsLibraryPath + File.separator + projectFolder;

			String oculusRunnerClasspath = getAgentProperties().getProperty("agent.oculus.runner");
			/*
			 * Determine separator for java classpath
			 */
			String jSeparator = ":";

			String osName = System.getProperty("os.name");
			if (osName != null) {
				logger.info("OS: " + osName);
				if (osName.toLowerCase().startsWith("windows")) {
					jSeparator = ";";
				}
			}

			/*
			 * Going to create a temporary suite where all the received tests
			 * will be stored. In order to avoid the collision in suite file
			 * name with another agents on same machine we would need to
			 * generate suite file name with current date and a random number
			 */
			String suiteFileName = "suite" + new Date().getTime() + (new Random().nextInt(6)) + ".xml";

			File file = new File(currentProjectDir + File.separator + suiteFileName);

			/*
			 * Checking if file already exists
			 */
			if (file.exists()) {
				/*
				 * Regenerating the suite filename
				 */
				suiteFileName = "suite" + new Date().getTime() + (new Random().nextInt(6)) + ".xml";
				file = new File(currentProjectDir + File.separator + suiteFileName);
			}
			/*
			 * Making dirs in case if the path wasn't specified the right way.
			 * In this case the whole TRMAgent might stuck and be busy forever.
			 * In order to prevent this situation we need need to make sure that
			 * we don't get any exceptions before the OculusRunner will be
			 * launched
			 */
			File fileProjectsDir = new File(currentProjectDir);
			if(!fileProjectsDir.exists()){
				fileProjectsDir.mkdirs();
			}
			
			logger.info("Saving suite to " + file.getAbsolutePath());
			XmlSuiteParser.saveSuite(task.getSuite(), file);

			String processCommand = "java -classpath " + oculusLibraryPath + File.separator + "*" + jSeparator + currentProjectDir + File.separator + "libs" + File.separator + "*" + jSeparator + currentProjectDir + File.separator + "*" + " " + oculusRunnerClasspath + " " + getAgentProperties().getProperty("agent.host") + " " + getAgentProperties().getProperty("agent.port") + " " + getAgentProperties().getProperty("agent.remoteName")  + " " + suiteFileName;

			logger.info("Working directory is: " + currentProjectDir);
			logger.info("Executing the process: \n" + processCommand);

			Runtime runtime = Runtime.getRuntime();
			Process process = runtime.exec(processCommand, null, new File(currentProjectDir));

			BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader((process.getInputStream())));
			BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String consoleText = null;
			String errorText = null;
			while (((consoleText = inputStreamReader.readLine()) != null) || (errorText = errorStreamReader.readLine()) != null) {
				if (consoleText != null) {
					System.out.println(consoleText);
				}
				else if (errorText != null) {
					System.out.println(errorText);
				}
			}
			/*
			 * Removing the temporary suite file
			 */
			file.delete();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
