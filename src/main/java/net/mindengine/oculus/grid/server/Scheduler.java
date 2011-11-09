package net.mindengine.oculus.grid.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.mindengine.oculus.grid.domain.task.MultiTask;
import net.mindengine.oculus.grid.domain.task.Task;
import net.mindengine.oculus.grid.domain.task.TaskStatus;
import net.mindengine.oculus.grid.domain.task.suite.SuiteTask;

/**
 * Used by the {@link TRMServer} for checking
 * 
 * @author Ivan Shubin
 * 
 */
public class Scheduler extends Thread implements Serializable {
	private static final long serialVersionUID = 1185956764513377316L;
	private TRMServer server;
	private Map<Long, Task> tasks = new HashMap<Long, Task>();
	protected ReentrantLock tasksLock = new ReentrantLock();
	private boolean turnedOff = false;
	
	/*
	 * Period in which the scheduler will be auto-saved
	 */
	private int saveTimePeriod = 5;

	public void checkAllOccurrences() {
		tasksLock.lock();
		try {
			for (Task task : tasks.values()) {
				Date currentDate = new Date();
				if (task.getScheduleOccurrence().shouldRun(currentDate)) {
					try {

						/*
						 * Copying task because after the task is being run its
						 * id will be changed and after multiple runs it taskStatus will be corrupted
						 */
						server.runTask(copyTask(task));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			tasksLock.unlock();
		}
	}

	@Override
	public void run() {
		/*
		 * Counter for saving the scheduler in the specified amount of time
		 */
		int saveCounter = 0;
		
		while (!turnedOff) {
			try {
				checkAllOccurrences();
				Thread.sleep(1000);
				saveCounter++;
				if(saveCounter>=saveTimePeriod){
					saveCounter = 0;
					saveScheduler();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void saveScheduler() throws IOException{
		File file = new File("server.scheduler");
		Scheduler.exportScheduler(this, file);
	}

	public boolean isTurnedOff() {
		return turnedOff;
	}

	public void addTask(Task task) {
		getTasks().put(task.getId(), task);
	}

	public void setTurnedOff(boolean turnedOff) {
		this.turnedOff = turnedOff;
	}

	public void setServer(TRMServer server) {
		this.server = server;
	}

	public TRMServer getServer() {
		return server;
	}

	public Map<Long, Task> getTasks() {
		return tasks;
	}

	public static void exportScheduler(Scheduler scheduler, File file) throws IOException {
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		scheduler.tasksLock.lock();
		try {
			oos.writeObject(scheduler.getTasks());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			scheduler.tasksLock.unlock();
		}
		oos.flush();
		oos.close();
	}

	@SuppressWarnings("unchecked")
	public static Scheduler importScheduler(File file) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream ois = new ObjectInputStream(fis);
		Scheduler scheduler = new Scheduler();
		scheduler.tasks = (Map<Long, Task>) ois.readObject();
		ois.close();

		return scheduler;
	}

	public int getSaveTimePeriod() {
    	return saveTimePeriod;
    }

	public void setSaveTimePeriod(int saveTimePeriod) {
    	this.saveTimePeriod = saveTimePeriod;
    }
	
	
	public Task copyTask(Task task){
		Task copiedTask;
		if(task instanceof SuiteTask){
			SuiteTask suiteTask = (SuiteTask)task;
			copiedTask = new SuiteTask();
			((SuiteTask)copiedTask).setSuite(suiteTask.getSuite());
			((SuiteTask)copiedTask).setProjectName(suiteTask.getProjectName());
			((SuiteTask)copiedTask).setProjectVersion(suiteTask.getProjectVersion());
		}
		else if(task instanceof MultiTask){
			MultiTask multiTask = (MultiTask)task;
			copiedTask = new MultiTask();
			List<Task> tasks = new ArrayList<Task>();
			
			//Copying all child tasks of multi-task
			for(Task childTask : multiTask.getTasks()){
				tasks.add(copyTask(childTask));
				childTask.setParent((MultiTask)copiedTask);
			}
			
			((MultiTask)copiedTask).setTasks(tasks);
		}
		else throw new IllegalArgumentException("Cannot copy task "+task.getClass().getName());
		//Copying fields which are generic for all types of tasks
		copiedTask.setName(task.getName());
		copiedTask.setTaskStatus(new TaskStatus());
		copiedTask.setTaskUser(task.getTaskUser());
		copiedTask.setCreatedDate(new Date());
		
		return  copiedTask;
	}
}
