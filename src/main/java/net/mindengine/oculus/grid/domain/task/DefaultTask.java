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
