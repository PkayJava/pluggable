package com.itrustcambodia.pluggable.migration;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Version;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.PluginRegistry;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

public abstract class AbstractPluginMigrator extends AbstractMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPluginMigrator.class);

    public final String getIdentity() {
        AbstractWebApplication application = getApplication();
        return application.getPluginMapping(this.getClass().getName());
    }

    @Override
    public boolean upgrade() {
        AbstractWebApplication application = getApplication();
        PluginRegistry pluginRegistry = application.getJdbcTemplate().queryForObject("select * from " + TableUtilities.getTableName(PluginRegistry.class) + " where " + PluginRegistry.IDENTITY + " = ? " + " order by " + PluginRegistry.VERSION + " desc limit 1", new EntityRowMapper<PluginRegistry>(PluginRegistry.class), getIdentity());
        Map<Double, Method> versions = new TreeMap<Double, Method>();
        for (Method method : getClass().getMethods()) {
            Version version = method.getAnnotation(Version.class);
            if (version != null) {
                if (version.value() > pluginRegistry.getVersion()) {
                    versions.put(version.value(), method);
                }
            }
        }
        for (Entry<Double, Method> entry : versions.entrySet()) {
            double version = entry.getKey();
            Method patch = entry.getValue();
            try {
                patch.invoke(this);
            } catch (Throwable e) {
                LOGGER.info("migration version {} method {} error due to this reason {}", new Object[] { version, patch.getName(), e.getMessage() });
                return false;
            }
            application.getJdbcTemplate().update("update " + TableUtilities.getTableName(PluginRegistry.class) + " set " + PluginRegistry.VERSION + " = ? where " + PluginRegistry.IDENTITY + " = ?", version, getIdentity());
        }
        return true;
    }
}
