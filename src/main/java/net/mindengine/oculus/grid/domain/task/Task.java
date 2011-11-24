package net.mindengine.oculus.grid.domain.task;

import java.io.Serializable;
import java.util.Date;


public abstract class Task implements Serializable {
	private static final long serialVersionUID = -347244634673235L;
	
	public final static String TYPE_MULTITASK = "multitask".intern();
	public final static String TYPE_DEFAULTTASK = "default".intern();
	public final static String TYPE_SUITETASK = "suitetask".intern();
	
	private Long id;
	private String name;
	private TaskUser taskUser;
	private TaskStatus taskStatus = new TaskStatus();
	private Date createdDate = new Date();
	private Date startedDate;
	private Date completedDate;
	
	public abstract String type();
	
	
	/**
	 * The list of preferable agent names where the task should be run.
	 */
	private String[] agentNames;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskUser getTaskUser() {
		return taskUser;
	}

	public void setTaskUser(TaskUser taskUser) {
		this.taskUser = taskUser;
	}

	
	public void setAgentNames(String[] agentNames) {
		this.agentNames = agentNames;
	}

	public String[] getAgentNames() {
		return agentNames;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("id=");
		str.append(id);
		str.append(", name=");
		str.append(name);
		return str.toString();
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setStartedDate(Date startedDate) {
		this.startedDate = startedDate;
		
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

}
