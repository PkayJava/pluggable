package com.itrustcambodia.pluggable.migration;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;

/**
 * @author Socheat KHAUV
 */
public interface IMigrator {

    boolean upgrade();

    double getVersion();

    public AbstractWebApplication getApplication();

}
