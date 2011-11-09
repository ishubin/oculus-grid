package net.mindengine.oculus.grid.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Thread which runs each 5 seconds and updates the statuses of agents
 * 
 * @author Ivan Shubin
 * 
 */
public class AgentHandler extends Thread {
	private TaskContainer taskContainer;
	private AgentContainer agentContainer;
	private Log logger = LogFactory.getLog(getClass());
	private Boolean enabled = true;

	@Override
	public void run() {
		while (enabled) {
			try {
				sleep(5000);
				agentContainer.getAgentLock().lock();
				AgentWrapper removeAgent = null;
				for (AgentWrapper agent : agentContainer.getAgents().values()) {
					try {
						agent.getAgentRemoteInterface().getAgentStatus();

					}
					catch (Exception e) {
						e.printStackTrace();
						removeAgent = agent;
					}
				}
				agentContainer.getAgentLock().unlock();
				if (removeAgent != null) {
					logger.info("Removing agent from list");
					taskContainer.getTaskLock().lock();
					/*
					 * Removing agent from agents container. If agent was busy
					 * with some task moving this task back to task queue
					 */
					if (removeAgent.getStatus().equals(AgentWrapper.BUSY)) {
						TaskWrapper assignedTask = removeAgent.getAssignedTask();
						taskContainer.getAssignedTasks().remove(assignedTask);
						taskContainer.getQueuedTasks().add(assignedTask);
						assignedTask.setState(TaskWrapper.QUEUED);
					}
					taskContainer.getTaskLock().unlock();
					agentContainer.removeAgent(removeAgent);
					logger.info("Agent was removed");
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public TaskContainer getTaskContainer() {
		return taskContainer;
	}

	public void setTaskContainer(TaskContainer taskContainer) {
		this.taskContainer = taskContainer;
	}

	public AgentContainer getAgentContainer() {
		return agentContainer;
	}

	public void setAgentContainer(AgentContainer agentContainer) {
		this.agentContainer = agentContainer;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getEnabled() {
		return enabled;
	}
}
