package net.mindengine.oculus.grid.server;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.service.ServerAgentRemoteInterface;

public class AgentWrapper {

	public static final Integer FREE = 1;
	public static final Integer BUSY = 2;

	private static ReentrantLock staticReentrantLock = new ReentrantLock();
	/**
	 * Used for generating the agentRemoteInterface ids
	 */
	private static Long _uniqueIdCounter = 0L;

	private Long agentId;
	private ServerAgentRemoteInterface agentRemoteInterface;
	private Integer status = 0;
	private AgentInformation agentInformation;
	private TaskWrapper assignedTask;

	public Long getAgentId() {
		return agentId;
	}

	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}

	public ServerAgentRemoteInterface getAgentRemoteInterface() {
		return agentRemoteInterface;
	}

	public void setAgentRemoteInterface(ServerAgentRemoteInterface agentRemoteInterface) {
		this.agentRemoteInterface = agentRemoteInterface;
	}

	/**
	 * Generates agentRemoteInterface unique ID and checks if ID exists in
	 * agentRemoteInterface list.<br>
	 * This method is thread-safe.
	 * 
	 * @param agents
	 *            Map of agents, used for verification if generated ID already
	 *            exists
	 * @return
	 */
	public static Long generateAgentId(Map<Long, AgentWrapper> agents) {
		staticReentrantLock.lock();
		_uniqueIdCounter++;
		if (agents.containsKey(_uniqueIdCounter)) {
			// Important! Don't forget to unlock as this method is based on
			// recursion
			staticReentrantLock.unlock();
			return generateAgentId(agents);
		}
		else {
			staticReentrantLock.unlock();
		}
		return _uniqueIdCounter;
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
		str.append(agentId);
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

}