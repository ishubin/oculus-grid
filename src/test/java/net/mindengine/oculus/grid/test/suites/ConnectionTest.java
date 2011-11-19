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
        
        //Verifying that agent has reconnected successfully
        assertEquals(1L, (long)agent.getAgentId().getId());
        assertNotNull(agent.getAgentId().getToken());
        assertNotSame(agent.getAgentId().getToken(), "");
        assertNotSame(agent.getAgentId().getToken(), oldToken);
    }
}
