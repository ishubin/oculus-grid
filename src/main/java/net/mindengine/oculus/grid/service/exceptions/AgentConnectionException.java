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
package net.mindengine.oculus.grid.service.exceptions;

public class AgentConnectionException extends RuntimeException{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public AgentConnectionException() {
        super();
    }

    public AgentConnectionException(String paramString, Throwable paramThrowable) {
        super(paramString, paramThrowable);
    }

    public AgentConnectionException(String paramString) {
        super(paramString);
    }

    public AgentConnectionException(Throwable paramThrowable) {
        super(paramThrowable);
    }

}
