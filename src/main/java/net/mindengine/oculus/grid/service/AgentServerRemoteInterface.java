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
package net.mindengine.oculus.grid.service;

import java.io.File;
import java.rmi.Remote;

import net.mindengine.oculus.grid.domain.agent.AgentId;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.domain.task.TaskStatus;

/**
 * Implemented by Test Run Manager Server and specifies the messages from
 * TRMAgent to Server
 * 
 * @author Ivan Shubin
 * 
 */
public interface AgentServerRemoteInterface extends Remote {
    /**
     * Notifies the server about the status of task. TRMAgent invokes this
     * method in the following cases:
     * <ul>
     * <li>test-action is running</li>
     * <li>test is running</li>
     * <li>test is finished</li>
     * <li>task is completed</li>
     * </ul>
     * 
     * @param taskId
     * @param taskStatus
     *            Task Status with detailed information about the task
     * @throws Exception
     */
    public void updateTaskStatus(Long taskId, TaskStatus taskStatus) throws Exception;

    /**
     * Notifies the server that the agent has join and is ready to execute
     * queued tasks
     * 
     * @param agentInformation
     *            Detailed information about the agent
     * @param previousId
     *            Id and token of previous agent registration. This is used when
     *            agent is trying to reconnect to server so it notifies that its
     *            previous remote stub should be removed from server.
     * @throws Exception
     * @return Id and generated token for just registered agent on server
     */
    public AgentId registerAgent(AgentInformation agentInformation, AgentId previousId) throws Exception;

    /**
     * Used to check the connection with Server
     * 
     * @return
     */
    public String checkConnection() throws Exception;
    
    /**
     * Looks for the project in storage and returns its latest control key, so the agent can check if theirs projects data is synchronized
     * @param projectName Name of project
     * @param version Version of project
     * @return Control key of the specified project in storage. In case if the project wasn't uploaded at - it returns null.
     * @throws Exception 
     */
    public String getProjectControlCode(String projectName, String version) throws Exception;

    /**
     * Returns project zipped data
     * @param projectName
     * @param projectVersion
     * @return
     * @throws Exception 
     */
    public File downloadProject(String projectName, String projectVersion) throws Exception;
}
