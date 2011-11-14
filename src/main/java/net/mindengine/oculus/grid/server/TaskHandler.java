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
	private TRMServer trmServer;

	public TaskHandler(TRMServer trmServer) {
		this.trmServer = trmServer;
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
						    
							AgentWrapper agent = agentContainer.fetchAgentWrapper(task.getTask().getAgentNames());
							if (agent != null) {
								try {
									/*
									 * Assigning task to agent
									 */
									assignTaskToAgent(task, agent);
								}
								catch (Exception ex) {
									agentContainer.getFreeAgents().remove(agent.getAgentId());
									agentContainer.getAgents().remove(agent.getAgentId());
									ex.printStackTrace();
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

	private void assignTaskToAgent(TaskWrapper task, AgentWrapper agent) throws Exception {
	    if(task.getTask() instanceof SuiteTask) {
    		agent.getAgentRemoteInterface().runSuiteTask((SuiteTask)task.getTask());
    		agent.setStatus(AgentWrapper.BUSY);
    		agent.setAssignedTask(task);
    		taskContainer.getQueuedTasks().remove(task);
    		taskContainer.getAssignedTasks().put(task.getId(), task);
    		agentContainer.getFreeAgents().remove(agent.getAgentId());
    		task.setState(TaskWrapper.ASSIGNED);
    		task.setAssignedAgent(agent);
    
    		task.getTask().setStartedDate(new Date());
    		task.getTask().getTaskStatus().setStatus(TaskStatus.ACTIVE);
    		task.getTask().getTaskStatus().setAssignedAgent(agent.getAgentInformation());
    		trmServer.updateTaskStatus(task.getId(), task.getTask().getTaskStatus());
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
