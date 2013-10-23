package com.itrustcambodia.pluggable.error;

/**
 * @author Socheat KHAUV
 */
public class PluginException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -2170217807204016623L;

    private String identity;

    public PluginException(String identity, String message) {
        super(message);
        this.identity = identity;

    }

    public String getIdentity() {
        return identity;
    }

}
