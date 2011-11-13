package net.mindengine.oculus.grid.server;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import net.mindengine.oculus.grid.domain.task.MultiTask;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskInformation;

/**
 * Used in TaskContainer for simple access to all kind of tasks
 * 
 * @author Ivan Shubin
 * 
 */
public class TaskWrapper {
	/**
	 * This state can be only for {@link MultiTask} as they could be added to
	 * tasks queue. When all the child tasks of multitask are completed it will
	 * be also marked as completed.
	 */
	public static final int UNDEFINED = 0;
	public static final int COMPLETED = 1;
	public static final int ASSIGNED = 2;
	public static final int QUEUED = 3;

	private Integer state = UNDEFINED;
	private Task task;
	private AgentWrapper assignedAgent = null;

	private List<TaskWrapper> children;

	public ReentrantLock taskLock = new ReentrantLock();

	public boolean isMultiTask() {
		if (task instanceof MultiTask) {
			return true;
		}
		return false;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getState() {
		return state;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Task getTask() {
		return task;
	}

	public void setAssignedAgent(AgentWrapper assignedAgent) {
		this.assignedAgent = assignedAgent;
	}

	public AgentWrapper getAssignedAgent() {
		return assignedAgent;
	}

	public Long getId() {
		return task.getId();
	}

	public void setChildren(List<TaskWrapper> children) {
		this.children = children;
	}

	public List<TaskWrapper> getChildren() {
		return children;
	}
	
	public TaskInformation getTaskInformation() {
	    TaskInformation info = new TaskInformation();
	    info.setTaskName(getTask().getName());
	    info.setTaskId(getTask().getId());
	    info.setTaskStatus(getTask().getTaskStatus());
	    info.setTaskUser(getTask().getTaskUser());
	    info.setType(getTask().type());
	    
	    if(children!=null) {
	        List<TaskInformation> childInfos = new LinkedList<TaskInformation>();
	        info.setChildTasks(childInfos);
	        
	        for(TaskWrapper tw: children) {
	            childInfos.add(tw.getTaskInformation());
	        }
	    }
	    return info;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer("task={");
		str.append(task.toString());
		str.append("}");

		str.append(", state=");
		if (state.equals(COMPLETED)) {
			str.append("COMPLETED");
		}
		else if (state.equals(ASSIGNED)) {
			str.append("ASSIGNED");
		}
		else if (state.equals(QUEUED)) {
			str.append("QUEUED");
		}
		else {
			str.append("UNDEFINED");
		}
		str.append(", assignedAgent=");
		if (assignedAgent == null) {
			str.append("null");
		}
		else {
			str.append("{");
			str.append(assignedAgent.toString());
			str.append("}");
		}
		return str.toString();
	}
}
