package com.angkorteam.pluggable.migration;

import com.angkorteam.pluggable.core.AbstractWebApplication;

/**
 * @author Socheat KHAUV
 */
public interface IMigrator {

    boolean upgrade();

    double getVersion();

    public AbstractWebApplication getApplication();

}
