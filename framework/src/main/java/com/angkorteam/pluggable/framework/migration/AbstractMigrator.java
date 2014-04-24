package com.angkorteam.pluggable.framework.migration;

import java.lang.reflect.Method;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Version;

/**
 * @author Socheat KHAUV
 */
public abstract class AbstractMigrator implements IMigrator {

    private AbstractWebApplication application;

    private double version = 0;

    @Override
    public double getVersion() {
        if (this.version == 0) {
            for (Method method : getClass().getMethods()) {
                Version version = method.getAnnotation(Version.class);
                if (version != null) {
                    if (version.value() > this.version) {
                        this.version = version.value();
                    }
                }
            }
        }
        return this.version;
    }

    public void setApplication(AbstractWebApplication application) {
        this.application = application;
    }

    public AbstractWebApplication getApplication() {
        return application;
    }

}
