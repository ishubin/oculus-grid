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
package net.mindengine.oculus.grid.test.suites;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import net.mindengine.oculus.grid.GridProperties;
import net.mindengine.oculus.grid.agent.Agent;
import net.mindengine.oculus.grid.domain.agent.AgentInformation;
import net.mindengine.oculus.grid.server.Server;
import net.mindengine.oculus.grid.service.exceptions.AgentConnectionException;
import net.mindengine.oculus.grid.storage.DefaultAgentStorage;
import net.mindengine.oculus.grid.storage.DefaultGridStorage;

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
    
    @Test
    public void agentWithAlreadyUsedNameShouldNotBeAbleToRegister() throws Exception {
        final ErrorContainer errorContainer = new ErrorContainer();
        final Server server = new Server();
        server.setStorage(new DefaultGridStorage());
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer(9010, "server");
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
        
        final Agent agent1 = createSampleAgent();
        Thread agentThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agent1.startAgent();
                } catch (Exception e) {
                    errorContainer.setException(e);
                }
            }
        });
        agentThread1.start();
        
        Thread.sleep(3000);
        if(errorContainer.getException()!=null) {
            throw errorContainer.getException();
        }
        
        final Agent agent2 = createSampleAgent();
        agent2.getAgentInformation().setPort(8093);
        Thread agentThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    agent2.startAgent();
                } catch (Exception e) {
                    errorContainer.setException(e);
                }
            }
        });
        agentThread2.start();
        
        Thread.sleep(3000);
        assertNotNull(errorContainer.getException());
        assertEquals(AgentConnectionException.class, errorContainer.getException().getClass());
        AgentConnectionException ace = (AgentConnectionException) errorContainer.getException();
        assertEquals("Agent with such name ('Agent') is already registered in Grid", ace.getMessage());
        
        assertEquals((long)agent1.getAgentId().getId(), 1L);
        assertNotNull(agent1.getAgentId().getToken());
        assertNotSame(agent1.getAgentId().getToken(), "");
        
        assertNull(agent2.getAgentId());
        
        agent2.stopAgent();
        agent1.stopAgent();
        server.stopServer();
    }        
    
    private Agent createSampleAgent() throws FileNotFoundException, IOException {
        Agent agent = new Agent();
        Properties properties = new Properties();
        properties.load(new FileReader(new File("grid.agent.properties")));
        
        AgentInformation agentInformation = new AgentInformation();
        agentInformation.setHost("localhost");
        agentInformation.setName("Agent");
        agentInformation.setPort(8091);
        agentInformation.setRemoteName("agent");
        agent.setAgentInformation(agentInformation);
        
        agent.setServerHost("localhost");
        agent.setServerName("server");
        agent.setServerPort(9010);
        agent.setAgentReconnectionTimeout(1);
        DefaultAgentStorage storage = new DefaultAgentStorage();
        storage.setStoragePath(properties.getProperty(GridProperties.STORAGE_PATH));
        agent.setStorage(storage);
        return agent;
    }

    @SuppressWarnings("deprecation")
    @Test
    public void agentCanReconnectToServer() throws Exception {
        final ErrorContainer errorContainer = new ErrorContainer();
        final Server server = new Server();
        server.setStorage(new DefaultGridStorage());
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.startServer(9020, "server");
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
        properties.load(new FileReader(new File("grid.agent.properties")));
        
        AgentInformation agentInformation = new AgentInformation();
        agentInformation.setHost("localhost");
        agentInformation.setName("Agent");
        agentInformation.setPort(8092);
        agentInformation.setRemoteName("agent");
        agent.setAgentInformation(agentInformation);
        
        agent.setServerHost("localhost");
        agent.setServerName("server");
        agent.setServerPort(9020);
        agent.setAgentReconnectionTimeout(1);
        DefaultAgentStorage storage = new DefaultAgentStorage();
        storage.setStoragePath(properties.getProperty(GridProperties.STORAGE_PATH));
        agent.setStorage(storage);
        
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
        server2.setStorage(new DefaultGridStorage());
        serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server2.startServer(9020, "server");
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
