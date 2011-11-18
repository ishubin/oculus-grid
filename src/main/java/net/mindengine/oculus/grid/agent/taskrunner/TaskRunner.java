package net.mindengine.oculus.grid.agent.taskrunner;

import java.util.Properties;

import net.mindengine.oculus.grid.agent.Agent;
import net.mindengine.oculus.grid.domain.task.SuiteTask;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.service.exceptions.IncorrectTaskException;

/**
 * A thread which is used for running the specified task and obtaining the task
 * progress information. With each new task the task runner should be
 * instantiated with a corresponded class via {@link #createTaskRunner(Task)}
 * method.
 * 
 * @author Ivan Shubin
 * 
 */
public abstract class TaskRunner extends Thread {
	private Task task;
	private TaskStatus taskStatus;
	private Properties agentProperties;
	private Agent agent;

	protected TaskRunner() {

	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public static TaskRunner createTaskRunner(Task task) throws IncorrectTaskException {
		TaskRunner taskRunner = null;
		if (task instanceof SuiteTask) {
			taskRunner = new SuiteTaskRunner();
		}
		if (taskRunner == null)
			throw new IncorrectTaskException();
		taskRunner.setTask(task);
		return taskRunner;
	}

	public void setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public void setAgentProperties(Properties agentProperties) {
		this.agentProperties = agentProperties;
	}

	public Properties getAgentProperties() {
		return agentProperties;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}
}
