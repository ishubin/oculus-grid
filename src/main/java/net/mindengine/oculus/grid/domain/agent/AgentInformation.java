package net.mindengine.oculus.grid.domain.agent;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Contains all information about agent. All the agent information is filled on
 * agents side and passed to server when agents registers in server
 * 
 * @author Ivan Shubin
 * 
 */
public class AgentInformation implements Serializable {
	private static final long serialVersionUID = 4788131383871097120L;

	private String name;
	private String hostName;
	private String description;

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

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void detectHostName() throws UnknownHostException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		hostName = inetAddress.getHostName();
	}

	@Override
	public String toString() {
		return "name=" + name + ", hostName=" + hostName;
	}
}
