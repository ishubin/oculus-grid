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

import net.mindengine.oculus.grid.domain.agent.AgentId;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.service.ServerAgentRemoteInterface;

public class AgentWrapper {

	public static final Integer FREE = 1;
	public static final Integer BUSY = 2;

	
	private AgentId agentId;
	private ServerAgentRemoteInterface agentRemoteInterface;
	private Integer status = 0;
	private AgentInformation agentInformation;
	private TaskWrapper assignedTask;



	public ServerAgentRemoteInterface getAgentRemoteInterface() {
		return agentRemoteInterface;
	}

	public void setAgentRemoteInterface(ServerAgentRemoteInterface agentRemoteInterface) {
		this.agentRemoteInterface = agentRemoteInterface;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getStatus() {
		return status;
	}

	public void setAgentInformation(AgentInformation agentInformation) {
		this.agentInformation = agentInformation;
	}

	public AgentInformation getAgentInformation() {
		return agentInformation;
	}

	/**
	 * Checks if agentRemoteInterface names is defined in list of preferred
	 * agents
	 * 
	 * @param preferredAgentsNames
	 *            An array containing preferred names of agents.
	 * @return true in case if agentRemoteInterface name is in specified
	 */
	public boolean isInList(String[] preferredAgentsNames) {
		for (String preferredName : preferredAgentsNames) {
			if (agentInformation.getName().equals(preferredName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("agentId=");
		str.append(getAgentId());
		str.append(", agentInformation={");
		str.append(agentInformation.toString());
		str.append("}, status=");
		if (status.equals(BUSY)) {
			str.append("BUSY");
		}
		else if (status.equals(FREE)) {
			str.append("FREE");
		}
		else {
			str.append("UNDEFINED");
		}
		return str.toString();
	}

	public void setAssignedTask(TaskWrapper assignedTask) {
		this.assignedTask = assignedTask;
	}

	public TaskWrapper getAssignedTask() {
		return assignedTask;
	}

    public void setAgentId(AgentId agentId) {
        this.agentId = agentId;
    }

    public AgentId getAgentId() {
        return agentId;
    }

}
