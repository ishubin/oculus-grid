package net.mindengine.oculus.grid.domain.agent;

import java.io.Serializable;

/**
 * Contains all information about agent. All the agent information is filled on
 * agents side and passed to server when agents registers in server
 * 
 * @author Ivan Shubin
 * 
 */
public class AgentInformation implements Serializable {
	private static final long serialVersionUID = 4788131383871097120L;

	private Long agentId;
	private String name;
	/**
	 * Remote host on which the agent can be accessed from server
	 */
	private String host;
	private String remoteName;
	private String description;
	private Integer port;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "name=" + name + ", host=" + getHost()+", remoteName="+remoteName;
	}

    public void setRemoteName(String remoteName) {
        this.remoteName = remoteName;
    }

    public String getRemoteName() {
        return remoteName;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public Long getAgentId() {
        return agentId;
    }
}
