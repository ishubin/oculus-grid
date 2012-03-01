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
