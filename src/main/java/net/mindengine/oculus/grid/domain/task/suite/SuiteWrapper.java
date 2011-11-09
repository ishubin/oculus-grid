package net.mindengine.oculus.grid.domain.task.suite;

import java.io.Serializable;

import net.mindengine.oculus.experior.suite.Suite;

/**
 * Used in SuiteTask class for wrapping all server information about the suite
 * 
 * @author Ivan Shubin
 */
public class SuiteWrapper implements Serializable {
	private static final long serialVersionUID = 7368474660227556838L;

	private Suite suite;

	public void setSuite(Suite suite) {
		this.suite = suite;
	}

	public Suite getSuite() {
		return suite;
	}

	@Override
	public String toString() {
		if (suite == null)
			return "null";

		return suite.toString();
	}
}
