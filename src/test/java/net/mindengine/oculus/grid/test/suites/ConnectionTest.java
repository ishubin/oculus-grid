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
package net.mindengine.oculus.grid.test.suites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.agent.Agent;
import net.mindengine.oculus.grid.server.Server;

import org.junit.Test;

public class ConnectionTest {

    
    private class ErrorContainer {
        private Exception exception;

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public Exception getException() {
            return exception;
        }
    }
    
    @SuppressWarnings("deprecation")
    @Test
    public void agentCanReconnectToServer() throws Exception {
        final ErrorContainer errorContainer = new ErrorContainer();
        final Server server = new Server();
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer(8090, "server");
                } catch (Exception e) {
                    errorContainer.setException(e);
                }
            }
        });
        serverThread.start();
        Thread.sleep(2000);
        if(errorContainer.getException()!=null) {
            throw errorContainer.getException();
        }
        
        final Agent agent = new Agent();
        Properties properties = new Properties();
        properties.load(new FileReader(new File(GridUtils.getMandatoryResourceFile(Agent.class, "/agent.properties"))));
        agent.setProperties(properties);
        agent.setAgentHost("localhost");
        agent.setAgentName("Agent");
        agent.setAgentPort(8091);
        agent.setAgentRemoteName("agent");
        agent.setServerHost("localhost");
        agent.setServerName("server");
        agent.setServerPort(8090);
        agent.setAgentReconnectionTimeout(1);
        
        Thread agentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agent.startAgent();
                } catch (Exception e) {
                    errorContainer.setException(e);
                }
            }
        });
        agentThread.start();
        
        Thread.sleep(3000);
        if(errorContainer.getException()!=null) {
            throw errorContainer.getException();
        }
        
        assertEquals((long)agent.getAgentId().getId(), 1L);
        assertNotNull(agent.getAgentId().getToken());
        assertNotSame(agent.getAgentId().getToken(), "");
        
        String oldToken = agent.getAgentId().getToken();
        
        //Stopping server
        server.stopServer();
        serverThread.stop();
        Thread.sleep(2000);
        
        //Starting server again
        final Server server2 = new Server();
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server2.startServer(8090, "server");
                } catch (Exception e) {
                    errorContainer.setException(e);
                }
            }
        });
        serverThread.start();
        Thread.sleep(2000);
        if(errorContainer.getException()!=null) {
            throw errorContainer.getException();
        }
        Thread.sleep(2000);
        
        server2.stopServer();
        serverThread.stop();
        agent.stopAgent();
        agentThread.stop();
        
        //Verifying that agent has reconnected successfully
        assertEquals(1L, (long)agent.getAgentId().getId());
        assertNotNull(agent.getAgentId().getToken());
        assertNotSame(agent.getAgentId().getToken(), "");
        assertNotSame(agent.getAgentId().getToken(), oldToken);
        
    }
    
}
