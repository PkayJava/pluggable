package com.itrustcambodia.pluggable.migration;

import java.lang.reflect.Method;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Version;

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
