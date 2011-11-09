package net.mindengine.oculus.grid.domain.task.suite;

import net.mindengine.oculus.grid.domain.task.Task;

/**
 * Used for running automation suite on agents
 * 
 * @author Ivan Shubin
 * 
 */
public class SuiteTask extends Task {
	private static final long serialVersionUID = -7426044556438608794L;
	private SuiteWrapper suite;

	/**
	 * The name of automation project.
	 */
	private String projectName;
	/**
	 * The version of automation project. If it is set as empty or null - only
	 * the current snapshot of automation project will be used.
	 */
	private String projectVersion;

	public void setSuite(SuiteWrapper suite) {
		this.suite = suite;
	}

	public SuiteWrapper getSuite() {
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
	public String toString() {
		return super.toString() + ", suite = {" + suite + "}";
	}

}
