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
package net.mindengine.oculus.grid.server;

import java.util.Date;

import net.mindengine.oculus.grid.domain.task.SuiteTask;
import net.mindengine.oculus.grid.domain.task.TaskStatus;

/**
 * Runs each 5 minutes and handles the task. If the task is in waiting state -
 * tries to run it on free agent.
 * 
 * @author Ivan Shubin
 * 
 */
public class TaskHandler extends Thread {
	private TaskContainer taskContainer;
	private AgentContainer agentContainer;
	private Server server;

	public TaskHandler(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sleep(5000);
				/*
				 * Checking for queued tasks and free agents for assigning.
				 * Locking all tasks and agents entities to prevent threads
				 * interruption
				 */
				taskContainer.getTaskLock().lock();
				agentContainer.getAgentLock().lock();
				
				try {
					if (taskContainer.hasQueuedTasks()) {
						for (TaskWrapper task : taskContainer.getQueuedTasks()) {
						    
						    if(task.getTask() instanceof SuiteTask) {
						        /*
						         * Checking that project with specified name and version is in storage already.
						         * If not the task will be rejected with NO_PROJECT_IN_STORAGE status
						         */
						        SuiteTask suiteTask = (SuiteTask) task.getTask();
						        String storageKey = server.getStorage().readProjectControlKey(suiteTask.getProjectName(), suiteTask.getProjectVersion());
						        
						        if(storageKey!=null) {
						            AgentWrapper agent = agentContainer.fetchFreeAgentWrapperMatchingNames(task.getTask().getAgentNames());
						            if (agent != null) {
						                try {
						                    /*
						                     * Assigning task to agent
						                     */
						                    assignTaskToAgent(task, agent);
						                }
						                catch (Exception ex) {
						                    ex.printStackTrace();
						                    taskContainer.moveTaskToErrorTask(task, ex.getClass().getName()+": " + ex.getMessage());
						                }
						            }
						        }
						        else {
						            taskContainer.moveTaskToErrorTask(task, TaskStatus.ERROR_NO_PROJECT_IN_STORAGE);
						        }
						    }
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}

				agentContainer.getAgentLock().unlock();
				taskContainer.getTaskLock().unlock();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void assignTaskToAgent(TaskWrapper task, AgentWrapper agentWrapper) throws Exception {
	    if(task.getTask() instanceof SuiteTask) {
	        
    		agentWrapper.setStatus(AgentWrapper.BUSY);
    		agentWrapper.setAssignedTask(task);
    		taskContainer.getQueuedTasks().remove(task);
    		taskContainer.getAssignedTasks().put(task.getId(), task);
    		agentContainer.getFreeAgents().remove(agentWrapper.getAgentId().getId());
    		task.setState(TaskWrapper.ASSIGNED);
    		task.setAssignedAgent(agentWrapper);
    
    		task.getTask().setStartedDate(new Date());
    		task.getTask().getTaskStatus().setStatus(TaskStatus.ACTIVE);
    		task.getTask().getTaskStatus().setAssignedAgent(agentWrapper.getAgentInformation());
    		
    		agentWrapper.getAgentRemoteInterface().runSuiteTask((SuiteTask)task.getTask());
    		server.updateTaskStatus(task.getId(), task.getTask().getTaskStatus());
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
}
