package net.mindengine.oculus.grid.domain.task;

import java.io.Serializable;

/**
 * The information about the user
 * 
 * @author Ivan Shubin
 * 
 */
public class TaskUser implements Serializable {
	private static final long serialVersionUID = 3361852401426878458L;
	private Long id;
	private String name;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
