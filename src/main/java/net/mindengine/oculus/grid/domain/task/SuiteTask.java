package net.mindengine.oculus.grid.domain.task;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.oculus.experior.suite.Suite;
import net.mindengine.oculus.experior.test.descriptors.TestDefinition;
import net.mindengine.oculus.experior.test.descriptors.TestInformation;

/**
 * Used for running automation suite on agents
 * 
 * @author Ivan Shubin
 * 
 */
public class SuiteTask extends Task {
	private static final long serialVersionUID = -7426044556438608794L;
	private Suite suite;

	/**
	 * The name of automation project.
	 */
	
	
	@Override
	public void initTask() {
	    TaskStatus taskStatus = new TaskStatus();
	    SuiteInformation suiteInformation = new SuiteInformation();
	    
	    List<TestStatus> tests = new LinkedList<TestStatus>();
	    
	    if(suite!=null) {
	        List<TestDefinition> testDefinitions = suite.getTests();
	        for(TestDefinition testDefinition : testDefinitions) {
	            TestStatus testStatus = new TestStatus();
	            testStatus.setName(testDefinition.getName());
	            testStatus.setMapping(testDefinition.getMapping());
	            testStatus.setDescription(testDefinition.getDescription());
	            testStatus.setPhase(TestInformation.PHASE_NOT_LAUNCHED);
	            testStatus.setStatus(TestInformation.STATUS_UNKOWN);
	            testStatus.setCustomId(testDefinition.getCustomId());
	            tests.add(testStatus);
	        }
	    }
	    
        suiteInformation.setTests(tests);
	    taskStatus.setSuiteInformation(suiteInformation);
	    
	    setTaskStatus(taskStatus);
	}
	private String projectName;
	/**
	 * The version of automation project. If it is set as empty or null - only
	 * the current snapshot of automation project will be used.
	 */
	private String projectVersion;

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public Suite getSuite() {
		return suite;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public void setProjectVersion(String projectVersion) {
		this.projectVersion = projectVersion;
	}

	
	@Override
	public String type() {
	    return Task.TYPE_SUITETASK;
	}

}
