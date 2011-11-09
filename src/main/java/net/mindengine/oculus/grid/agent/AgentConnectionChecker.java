package net.mindengine.oculus.grid.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentConnectionChecker extends Thread {

	private Log logger = LogFactory.getLog(getClass());

	private Boolean enabled = true;
	private TRMAgent agent;

	@Override
	public void run() {
		while (enabled) {
			try {
				Thread.sleep(10000);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				agent.getServer().checkConnection();
			}
			catch (Exception exception) {
				exception.printStackTrace();
				try {
					logger.info("Trying to reconnect");
					agent.reconnect();

					// Reconnection is done successfully
				}
				catch (Exception e) {
					logger.info("Unable to reconnect. Will try again later");
				}
			}
		}
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setAgent(TRMAgent agent) {
		this.agent = agent;
	}

	public TRMAgent getAgent() {
		return agent;
	}

}
