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
package net.mindengine.oculus.grid.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AgentConnectionChecker extends Thread {

	private Log logger = LogFactory.getLog(getClass());

	private Boolean enabled = true;
	private Agent agent;
	private Boolean reconnectNeeded = false;
	
	private int timeout = 5000;
	
	@Override
	public void run() {
	    timeout = agent.getAgentReconnectionTimeout()*1000;
	    
		while (enabled) {
			try {
				Thread.sleep(timeout);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			if(reconnectNeeded) {
			    try {
                    logger.info("Trying to reconnect");
                    agent.reconnect();
                    // Reconnection is done successfully
                    reconnectNeeded = false;
                }
                catch (Exception e) {
                    logger.info("Unable to reconnect. Will try again later");
                }
			}
			
			try {
			    agent.getServer().checkConnection();
			}
			catch (Exception exception) {
				exception.printStackTrace();
				
				/**
				 * Doing this trick because lookup still keeps the old object and when the TRMServer is back again the checkConnection method will not give any error.
				 * But in case if connection was lost and after some moment is back again - we need to trigger the agent registration on TRMServer
				 */
				reconnectNeeded = true;
			}
		}
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public Agent getAgent() {
		return agent;
	}

    public void stopConnectionChecker() {
        enabled = false;
    }

}
