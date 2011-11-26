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
	public static final Integer RUNTIME_ERROR = 1;
	public static final Integer COMPLETED = 2;
	public static final Integer WAITING = 3;
	private Long taskId;

	private boolean interrupted = false;
	private AgentInformation assignedAgent;

	private Integer status = 0;

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

}
