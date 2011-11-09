package net.mindengine.oculus.grid.server;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Checks if there are any completed tasks which should be removed
 * @author ishubin
 *
 */
public class CompletedTaskCleaner extends Thread {

	private Log logger = LogFactory.getLog(getClass());
	private TaskContainer taskContainer;
	private TRMServer trmServer;

	public CompletedTaskCleaner(TRMServer trmServer) {
		this.setTrmServer(trmServer);
	}

	
	@Override
	public void run() {
		while(true){
			try{
				Thread.sleep(10000);
				taskContainer.getTaskLock().lock();
				try{
					for(TaskWrapper taskWrapper : taskContainer.getCompletedTasks().values()){
						//)
						/*
						 * Calculating the amount of time when the task was completed
						 */
						Date completionDate = taskWrapper.getTask().getCompletedDate();
						if(completionDate!=null){
							Long completionTime = new Date().getTime() - completionDate.getTime();
							/*
							 * converting milliseconds to minutes
							 */
							completionTime = completionTime/60000;
							/*
							 * Checking the completion time with the maximum allowed time in server.properties
							 */
							if(completionTime>trmServer.getStoreCompletedTasksTime()){
								/*
								 *  
								 */
								logger.info("Removing the completed task because it was stored for too long. Id = "+taskWrapper.getId()+", name = "+taskWrapper.getTask().getName());
								taskContainer.removeCompletedTaskWithoutLock(taskWrapper.getId());
							}
						}
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					taskContainer.getTaskLock().unlock();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void setTrmServer(TRMServer trmServer) {
	    this.trmServer = trmServer;
    }


	public TRMServer getTrmServer() {
	    return trmServer;
    }


	public void setTaskContainer(TaskContainer taskContainer) {
	    this.taskContainer = taskContainer;
    }


	public TaskContainer getTaskContainer() {
	    return taskContainer;
    }
}
