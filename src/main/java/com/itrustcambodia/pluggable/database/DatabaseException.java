package com.itrustcambodia.pluggable.database;

public class DatabaseException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -5634720746247784498L;

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException() {
        super();
    }

}
