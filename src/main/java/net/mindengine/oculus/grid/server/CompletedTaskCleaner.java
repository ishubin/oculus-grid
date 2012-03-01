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
	private Server trmServer;

	public CompletedTaskCleaner(Server trmServer) {
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


	public void setTrmServer(Server trmServer) {
	    this.trmServer = trmServer;
    }


	public Server getTrmServer() {
	    return trmServer;
    }


	public void setTaskContainer(TaskContainer taskContainer) {
	    this.taskContainer = taskContainer;
    }


	public TaskContainer getTaskContainer() {
	    return taskContainer;
    }
}
