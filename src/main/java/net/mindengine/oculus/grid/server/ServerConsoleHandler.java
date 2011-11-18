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
