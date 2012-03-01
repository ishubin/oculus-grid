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
package net.mindengine.oculus.grid.domain.task;

import java.util.Date;



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
    private Date completedDate;
    private Date createdDate;
    private Date startedDate;
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
    public Date getCompletedDate() {
        return completedDate;
    }
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }
    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    public Date getStartedDate() {
        return startedDate;
    }
    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }
    
}
