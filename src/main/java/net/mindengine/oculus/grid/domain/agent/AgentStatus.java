package net.mindengine.oculus.grid.domain.agent;

import java.io.Serializable;

import net.mindengine.oculus.grid.server.TRMServer;

public class AgentStatus implements Serializable {
	public static final int FREE = 1;
	public static final int BUSY = 2;

	private static final long serialVersionUID = 749454293034716415L;

	private int state = FREE;

	/**
	 * This field is used only for passing the info to Client. Used in getAgents
	 * method of {@link TRMServer}
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
