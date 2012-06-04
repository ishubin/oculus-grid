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
package net.mindengine.oculus.grid.agent.taskrunner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.mindengine.oculus.experior.suite.XmlSuiteParser;
import net.mindengine.oculus.grid.agent.AgentProjectContext;
import net.mindengine.oculus.grid.domain.task.SuiteTask;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

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
			
			if(getAgent().getStorage()==null) {
			    throw new NullPointerException("Agent storage is not specified");
			}
			
			syncProject(task);
			
			String currentProjectDir = getAgent().getStorage().getProjectPath(task.getProjectName(), task.getProjectVersion());
			String pathToOculusGrid = getAgent().getAgentOculusGridLibrary();
			
			if(!new File(pathToOculusGrid).exists()) {
			    throw new FileNotFoundException(pathToOculusGrid);
			}
			
			
			/*
			 * Determine separator for java classpath
			 */
	        AgentProjectContext ctx = new AgentProjectContext();
			ctx.setJlibSeparator(":");
			String osName = System.getProperty("os.name");
			if (osName != null) {
				logger.info("OS: " + osName);
				if (osName.toLowerCase().startsWith("windows")) {
					ctx.setJlibSeparator(";");
				}
			}

			/*
			 * Going to create a temporary suite where all received tests
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
			
			logger.info("Saving suite to " + file.getAbsolutePath());
			XmlSuiteParser.saveSuite(task.getSuite(), file);
			ctx.setSuiteFile(file.getAbsolutePath());
			ctx.setProjectDir(currentProjectDir);
			String processCommand = buildProcessCommand(ctx);

			logger.info("Working directory is: " + currentProjectDir);
			logger.info("Executing the process: \n" + processCommand);

			
			CommandLine cmdLine = CommandLine.parse(processCommand);
			DefaultExecutor executor = new DefaultExecutor();
			executor.setWorkingDirectory(new File(currentProjectDir));
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler(stdout);
            executor.setStreamHandler(psh);
            
            try {
                executor.execute(cmdLine);
            }
			catch (Exception e) {
			    System.out.println(stdout.toString());
                throw e;
            }
			
			/*
			 * Removing the temporary suite file
			 */
			file.delete();
		}
		catch (Throwable error) {
		    error.printStackTrace();
			getAgent().onTaskError(error);
		}
	}

    private String buildProcessCommand(AgentProjectContext ctx) throws IOException, TemplateException {
        Configuration cfg = new Configuration();
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        cfg.setTemplateLoader(new StringTemplateLoader());
        cfg.setNumberFormat("0.######");
        Template template = new Template("agentProcess", new StringReader(getAgent().getAgentOculusRunnerProcessTemplate()), cfg);
        StringWriter sw = new StringWriter();
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("agent", getAgent());
        model.put("ctx", ctx);
        template.process(model, sw);
        return sw.toString();
    }

    private void syncProject(SuiteTask task) throws Exception, IOException {
        if(getProjectSyncNeeded()) {
            File projectFile = getAgent().getServer().downloadProject(task.getProjectName(), task.getProjectVersion());
            String controlCode = getAgent().getServer().getProjectControlCode(task.getProjectName(), task.getProjectVersion());
            
            byte[] bytes = FileUtils.readFileToByteArray(projectFile);
            getAgent().getStorage().putProjectZip(task.getProjectName(), task.getProjectVersion(), bytes, "agent", controlCode);
        }
    }
}
