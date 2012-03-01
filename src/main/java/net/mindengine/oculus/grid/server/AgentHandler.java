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
* along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
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
