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
package net.mindengine.oculus.grid.domain.task;

import java.util.LinkedList;
import java.util.List;

public class DefaultTask extends Task{
    
    /**
     * 
     */
    private static final long serialVersionUID = -2023693184777968999L;
    private List<SuiteTask> suiteTasks;

    @Override
    public String type() {
        return "default";
    }

    public void setSuiteTasks(List<SuiteTask> suiteTasks) {
        this.suiteTasks = suiteTasks;
    }

    public List<SuiteTask> getSuiteTasks() {
        return suiteTasks;
    }
    
    @Override
    public TaskStatus updateTaskStatus() {
        return getTaskStatus();
    }
    
    
    @Override
    public void initTask() {
        if(suiteTasks!=null) {
            for(SuiteTask suiteTask : suiteTasks) {
                suiteTask.initTask();
            }
        }
    }

    public MultiTask convertToMultiTask() {
        MultiTask task  = new MultiTask();
        task.setAgentNames(getAgentNames());
        task.setCreatedDate(getCreatedDate());
        task.setId(getId());
        task.setCompletedDate(getCompletedDate());
        task.setStartedDate(getStartedDate());
        task.setTaskUser(getTaskUser());
        task.setTaskStatus(getTaskStatus());
        task.setName(getName());
        
        List<Task> tasks = new LinkedList<Task>();
        for(SuiteTask suiteTask : getSuiteTasks()) {
            tasks.add(suiteTask);
        }
        
        task.setTasks(tasks);
        return task;
    }
}
