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
package net.mindengine.oculus.grid.service.exceptions;

public class IncorrectTaskException extends Exception {
	private static final long serialVersionUID = 1546389146424021247L;

    public IncorrectTaskException() {
        super();
    }

    public IncorrectTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTaskException(String message) {
        super(message);
    }

    public IncorrectTaskException(Throwable cause) {
        super(cause);
    }
	
	
}
