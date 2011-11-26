/*******************************************************************************
 * 2011 Ivan Shubin http://mindengine.net
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
package net.mindengine.oculus.grid.domain.agent;

import java.io.Serializable;

import net.mindengine.oculus.grid.server.Server;

public class AgentStatus implements Serializable {
	public static final int FREE = 1;
	public static final int BUSY = 2;

	private static final long serialVersionUID = 749454293034716415L;

	private int state = FREE;

	/**
	 * This field is used only for passing the info to Client. Used in getAgents
	 * method of {@link Server}
	 */
	private AgentInformation agentInformation;

	public void setState(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}

	public void setAgentInformation(AgentInformation agentInformation) {
		this.agentInformation = agentInformation;
	}

	public AgentInformation getAgentInformation() {
		return agentInformation;
	}
}
