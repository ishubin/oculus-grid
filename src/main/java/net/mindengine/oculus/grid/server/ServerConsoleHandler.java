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
package net.mindengine.oculus.grid.server;

public class ServerConsoleHandler {
	private Server server;

	public ServerConsoleHandler(Server server) {
		this.server = server;
	}

	public void printQueuedTasks() {

	}

	public void printTasks() {
		server.getTaskContainer().printTasks();
	}

	public void printAgents() {
		server.getAgentContainer().printAgents();
	}

	public void printTask(String id) {
		System.out.println("Printing task " + id);
	}

	public void exit() {
		quit();
	}

	public void quit() {
		server.quit();
	}
}
