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
* along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
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
