package net.mindengine.oculus.grid.domain.task;

import java.util.List;


/**
 * Stores most important information about task. This class is used in order to reduce amount of remote info.
 * @author Ivan Shubin
 *
 */
public class TaskInformation {

    private Long taskId;
    private String taskName;
    private TaskStatus taskStatus;
    private TaskUser taskUser;
    private List<TaskInformation> childTasks;
    private String type;
    public Long getTaskId() {
        return taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public TaskStatus getTaskStatus() {
        return taskStatus;
    }
    public List<TaskInformation> getChildTasks() {
        return childTasks;
    }
    public String getType() {
        return type;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
    public void setChildTasks(List<TaskInformation> childTasks) {
        this.childTasks = childTasks;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setTaskUser(TaskUser taskUser) {
        this.taskUser = taskUser;
    }
    public TaskUser getTaskUser() {
        return taskUser;
    }
    
}
