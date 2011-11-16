package net.mindengine.oculus.grid.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import net.mindengine.oculus.grid.domain.agent.AgentId;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.service.ServerAgentRemoteInterface;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Does all the work associated with agents entity.<br>
 * TRMServer delegates the RMI methods to AgentContainer
 * 
 * @author Ivan Shubin
 * 
 */
public class AgentContainer {
	private Map<Long, AgentWrapper> freeAgents = new HashMap<Long, AgentWrapper>();
	private Map<Long, AgentWrapper> agents = new HashMap<Long, AgentWrapper>();

	/**
	 * Used for locking all methods which use the agents map to prevent problems
	 * with iterators
	 */
	private ReentrantLock agentLock = new ReentrantLock();
	private Log logger = LogFactory.getLog(getClass());

	/**
	 * Delegated RMI method from TRMServer
	 * This method locks agentsContainer
	 * 
	 * @param agentInformation
	 *            Information about new agent
	 * 
	 * @param agentRemoteInterface
	 *            Agents remote instance
	 * @return Id and generated token of the agent
	 */
	public AgentId registerAgent(AgentInformation agentInformation, ServerAgentRemoteInterface agentRemoteInterface) {
		AgentId agentId = null;
		agentLock.lock();
		try {
			logger.info("Registering Agent: " + agentInformation.toString());
			agentId = AgentWrapper.generateAgentId(agents);
			AgentWrapper agent = new AgentWrapper();
			agent.setAgentRemoteInterface(agentRemoteInterface);
			
			agent.setAgentId(agentId);
			agent.setStatus(AgentWrapper.FREE);
			agent.setAgentInformation(agentInformation);
			agents.put(agentId.getId(), agent);
			freeAgents.put(agentId.getId(), agent);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			agentLock.unlock();
		}
		return agentId;
	}
	
	/**
	 * This method locks agentsContainer
	 * @param agentId
	 */
	public void removeAgent(AgentId agentId) {
	    agentLock.lock();
	    try{
	        AgentWrapper agentWrapper = agents.get(agentId.getId());
	        if(agentWrapper!=null) {
	            if(agentWrapper.getAgentId().getToken().equals(agentId.getToken())){
	                removeAgent(agentWrapper);
	            }
	        }
	    }
	    catch (Exception e) {
            e.printStackTrace();
        }
	    finally {
	        agentLock.unlock();
	    }
	}

	/**
	 * Sets the agent for freeAgents map
	 * 
	 * @param agentId
	 */
	public void freeAgent(Long agentId) {
		agentLock.lock();
		try {
			AgentWrapper agent = agents.get(agentId);
			agent.setStatus(AgentWrapper.FREE);
			agent.setAssignedTask(null);
			freeAgents.put(agent.getAgentId().getId(), agent);
		}
		catch (Throwable e) {
			logger.error(e);
		}
		finally {
			agentLock.unlock();
		}
	}

	public boolean hasFreeAgents() {
		return !freeAgents.isEmpty();
	}

	/**
	 * Fetches free agent which is in the list of preferred agents
	 * 
	 * @param preferredAgentsNames
	 *            Array of preferred agents names
	 * @return Free agent
	 */
	public AgentWrapper fetchAgentWrapper(String[] preferredAgentsNames) {
		Iterator<AgentWrapper> iterator = freeAgents.values().iterator();
		if (preferredAgentsNames == null) {
			if (iterator.hasNext()) {
				return iterator.next();
			}
		}
		else {
			while (iterator.hasNext()) {
				AgentWrapper agent = iterator.next();
				if (agent.isInList(preferredAgentsNames)) {
					return agent;
				}
			}
		}
		return null;
	}

	public Map<Long, AgentWrapper> getFreeAgents() {
		return freeAgents;
	}

	public void setFreeAgents(Map<Long, AgentWrapper> freeAgents) {
		this.freeAgents = freeAgents;
	}

	public Map<Long, AgentWrapper> getAgents() {
		return agents;
	}

	public void setAgents(Map<Long, AgentWrapper> agents) {
		this.agents = agents;
	}

	protected ReentrantLock getAgentLock() {
		return agentLock;
	}

	
	protected void removeAgent(AgentWrapper agent) {
	    System.out.println("Removing agent with id = "+agent.getAgentId().getId()+", token = "+agent.getAgentId().getToken());
		agents.remove(agent.getAgentId().getId());
		freeAgents.remove(agent.getAgentId().getId());
	}

	public void printAgents() {
		agentLock.lock();
		System.out.println("=======================================");
		System.out.println("Agents num: " + agents.size());
		System.out.println("=======================================");
		for (Map.Entry<Long, AgentWrapper> agent : agents.entrySet()) {
			System.out.println(agent.getValue());
		}
		agentLock.unlock();
	}
}
