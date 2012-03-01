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

import net.mindengine.oculus.grid.domain.agent.AgentInformation;

/**
 * Detailed status of task. <br>
 * Used to pass detailed information about task execution from TRMAgent to
 * TRMServer
 * 
 * @author Ivan Shubin
 * 
 */
public class TaskStatus implements Serializable {
	private static final long serialVersionUID = -3075389071512049788L;
	public static final Integer ACTIVE = 0;
	public static final Integer ERROR = 1;
	public static final Integer COMPLETED = 2;
	public static final Integer WAITING = 3;
	
	public static final String ERROR_NO_PROJECT_IN_STORAGE = "No project in storage";
	private Long taskId;

	private boolean interrupted = false;
	private AgentInformation assignedAgent;

	private Integer status = 0;

	/**
	 * Percent of completion from 0.0 to 100.0
	 */
	private Float percent = 0.0f;
	
	private SuiteInformation suiteInformation;
	/**
	 * Message text with task execution details
	 */
	private String message;
	private String taskName;

	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}


	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setAssignedAgent(AgentInformation assignedAgent) {
		this.assignedAgent = assignedAgent;
	}

	public AgentInformation getAssignedAgent() {
		return assignedAgent;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	public boolean isInterrupted() {
		return interrupted;
	}

    public void setSuiteInformation(SuiteInformation suiteInformation) {
        this.suiteInformation = suiteInformation;
    }

    public SuiteInformation getSuiteInformation() {
        return suiteInformation;
    }

    public void setPercent(Float percent) {
        this.percent = percent;
    }

    public Float getPercent() {
        return percent;
    }

}
