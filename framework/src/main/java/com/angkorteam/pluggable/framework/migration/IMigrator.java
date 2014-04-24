package com.angkorteam.pluggable.framework.migration;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;

/**
 * @author Socheat KHAUV
 */
public interface IMigrator {

    boolean upgrade();

    double getVersion();

    public AbstractWebApplication getApplication();

}
