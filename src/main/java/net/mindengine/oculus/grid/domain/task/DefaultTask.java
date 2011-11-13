package net.mindengine.oculus.grid.domain.task;

import java.util.LinkedList;
import java.util.List;

/**
 * Used only as argument for remote methods because because it is important to specify a concrete implementation of task for deserializer
 * @author Ivan Shubin
 *
 */
public class DefaultTask extends Task{

    /**
     * 
     */
    private static final long serialVersionUID = -1404275420777342879L;

    private List<SuiteTask> suiteTasks;

    public void setSuiteTasks(List<SuiteTask> suiteTasks) {
        this.suiteTasks = suiteTasks;
    }

    public List<SuiteTask> getSuiteTasks() {
        return suiteTasks;
    }
    
    @Override
    public String type() {
        return Task.TYPE_DEFAULTTASK;
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
