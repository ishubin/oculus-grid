package net.mindengine.oculus.grid.client;

import net.mindengine.jeremy.exceptions.ConnectionError;
import net.mindengine.jeremy.exceptions.RemoteObjectIsNotFoundException;
import net.mindengine.jeremy.registry.Lookup;
import net.mindengine.oculus.grid.GridUtils;
import net.mindengine.oculus.grid.service.ClientServerRemoteInterface;

public class GridClient {
    private String serverHost;
    private Integer serverPort;
    private String serverName;
    
    ClientServerRemoteInterface server = null;
    
    public GridClient() {
    }
    
	public GridClient(String serverHost, Integer serverPort) {
        super();
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public ClientServerRemoteInterface getServer() throws RemoteObjectIsNotFoundException, ConnectionError {
        if(server==null) {
            Lookup lookup = GridUtils.createDefaultLookup();
            lookup.setUrl("http://"+serverHost+":"+serverPort);
            server = lookup.getRemoteObject(serverName, ClientServerRemoteInterface.class);
        }
        return server;
	}

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }
}
