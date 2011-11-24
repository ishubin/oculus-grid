package net.mindengine.oculus.grid.domain.task;



/**
 * Stores most important information about task. This class is used in order to reduce amount of remote info.
 * @author Ivan Shubin
 *
 */
public class TaskInformation {

    private Long taskId;
    private Long parentId;
    private String taskName;
    private TaskStatus taskStatus;
    private TaskUser taskUser;
    private String type;
    private Integer childTasksAmount;
    public Long getTaskId() {
        return taskId;
    }
    public String getTaskName() {
        return taskName;
    }
    public TaskStatus getTaskStatus() {
        return taskStatus;
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
    public void setType(String type) {
        this.type = type;
    }
    public void setTaskUser(TaskUser taskUser) {
        this.taskUser = taskUser;
    }
    public TaskUser getTaskUser() {
        return taskUser;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
    public Long getParentId() {
        return parentId;
    }
    public void setChildTasksAmount(Integer childTasksAmount) {
        this.childTasksAmount = childTasksAmount;
    }
    public Integer getChildTasksAmount() {
        return childTasksAmount;
    }
    
}
