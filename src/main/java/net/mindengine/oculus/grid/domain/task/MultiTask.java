package net.mindengine.oculus.grid.domain.task;

import java.util.ArrayList;
import java.util.List;

/**
 * Composition of simple tasks used for tasks allocation between agents
 * 
 * @author Ivan Shubin
 * 
 */
public class MultiTask extends Task {
	private static final long serialVersionUID = 2659186513531107457L;
	private List<Task> tasks = new ArrayList<Task>();

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Task> getTasks() {
		return tasks;
	}
	
	@Override
	public void initTask() {

	    if(tasks!=null) {
	        for(Task task : tasks) {
	            task.initTask();
	        }
	    }
	}

	/**
	 * Checks for statuses of the children tasks and updates its own status. If
	 * there is at least one task running and all other tasks are in waiting
	 * state the complete status of the MultiTask will be "ACTIVE". Only if all
	 * the children tasks were completed the complete status will be COMPLETED
	 * as well. Also in this method will be calculated the average amount of
	 * task completion in percents
	 */
	public void updateTaskStatus() {
		boolean hasWaiting = false;
		boolean hasActive = false;
		boolean hasCompleted = false;

		for (Task task : tasks) {
			if (TaskStatus.WAITING.equals(task.getTaskStatus().getStatus())) {
				hasWaiting = true;
			}
			else if (TaskStatus.ACTIVE.equals(task.getTaskStatus().getStatus())) {
				hasActive = true;
			}
			else if (TaskStatus.COMPLETED.equals(task.getTaskStatus().getStatus())) {
				hasCompleted = true;
			}

			if (hasCompleted && !hasActive && !hasWaiting) {
				getTaskStatus().setStatus(TaskStatus.COMPLETED);
			}
			else if (hasActive) {
				getTaskStatus().setStatus(TaskStatus.ACTIVE);
			}
			else
				getTaskStatus().setStatus(TaskStatus.WAITING);
		}
	}
	
	//TODO Task should calculate percent of completion

    @Override
    public String type() {
        return Task.TYPE_MULTITASK;
    }

}
