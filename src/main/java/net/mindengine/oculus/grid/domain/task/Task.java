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
	 * Used in order to initialize data on server
	 */
	public abstract void initTask();
	
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

    public abstract TaskStatus updateTaskStatus();

}
