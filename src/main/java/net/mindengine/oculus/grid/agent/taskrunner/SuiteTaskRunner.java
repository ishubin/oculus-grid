/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
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
package net.mindengine.oculus.grid.agent.taskrunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.grid.GridProperties;
import net.mindengine.oculus.grid.agent.AgentProperties;
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
			 * run the automation project
			 */
			SuiteTask task = (SuiteTask) getTask();

			String projectFolder = task.getProjectName() + File.separator + task.getProjectName();
			if (task.getProjectVersion() != null && !task.getProjectVersion().isEmpty()) {
				projectFolder += "-" + task.getProjectVersion();
			}
			else {
				projectFolder += "-current";
			}

			String storagePath = getAgentProperties().getProperty(GridProperties.STORAGE_PATH);
			String pathToOculusGrid = getAgentProperties().getProperty(AgentProperties.AGENT_OCULUS_GRID_LIBRARY);

			String currentProjectDir = storagePath + File.separator + projectFolder;

			String oculusRunnerClasspath = getAgentProperties().getProperty(AgentProperties.AGENT_OCULUS_RUNNER);
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

			String processCommand = "java -classpath \"" + pathToOculusGrid + jSeparator 
			    + currentProjectDir + File.separator + "libs" + File.separator + "*" + jSeparator 
			    + currentProjectDir + File.separator + "*\"" 
			    + " " + oculusRunnerClasspath 
			    + " localhost " 
			    + getAgentProperties().getProperty(AgentProperties.AGENT_PORT) + " " 
			    + getAgentProperties().getProperty(AgentProperties.AGENT_REMOTE_NAME)  + " " 
			    + suiteFileName;

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
