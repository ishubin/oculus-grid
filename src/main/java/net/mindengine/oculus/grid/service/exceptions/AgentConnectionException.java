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
