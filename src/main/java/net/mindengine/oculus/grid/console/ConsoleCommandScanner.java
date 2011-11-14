package net.mindengine.oculus.grid.console;

import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * Reads commands from console and executes them by invoking the methods of
 * {@link #handler} via reflection. Used for debugging in TRMServer.
 * 
 * @author Ivan Shubin
 * 
 */
public class ConsoleCommandScanner extends Thread {
	private Object handler;
	private boolean switchOff = false;

	/**
	 * Starts reading the console input. This method never ends as it uses
	 * infinite loop.
	 */
	@Override
	public void run() {
		// Console console = System.console();
		Scanner scanner = new Scanner(System.in);
		while (!switchOff) {
			String command = scanner.nextLine();
			// String command = console.readLine();

			String[] cmd = command.split(" ");
			if (cmd.length > 0) {
				try {
					String methodName = cmd[0];
					String params[] = new String[cmd.length - 1];
					for (int i = 0; i < params.length; i++) {
						params[i] = cmd[i + 1];
					}

					invoke(methodName, params);
				}
				catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public void invoke(String methodName, Object[] parameters) throws Exception {
		Class<?> clazz = handler.getClass();
		Class<?> params[] = new Class<?>[parameters.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = String.class;
		}
		Method m = clazz.getMethod(methodName, params);
		m.invoke(handler, parameters);
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}

	public Object getHandler() {
		return handler;
	}

	public void setSwitchOff(boolean switchOff) {
		this.switchOff = switchOff;
	}

	public boolean isSwitchOff() {
		return switchOff;
	}
}