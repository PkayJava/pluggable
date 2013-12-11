package com.itrustcambodia.pluggable.json;

import java.io.Serializable;

/**
 * @author Socheat KHAUV
 */
public class VersionForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4607244136868093499L;

    private double version;

    private String description;

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
