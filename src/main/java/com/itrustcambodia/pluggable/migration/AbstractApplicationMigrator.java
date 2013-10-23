package com.itrustcambodia.pluggable.migration;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Version;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.ApplicationRegistry;
import com.itrustcambodia.pluggable.utilities.TableUtilities;

public abstract class AbstractApplicationMigrator extends AbstractMigrator {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplicationMigrator.class);

    @Override
    public boolean upgrade() {
        AbstractWebApplication application = getApplication();
        ApplicationRegistry applicationRegistry = application.getJdbcTemplate().queryForObject("select * from " + TableUtilities.getTableName(ApplicationRegistry.class) + " order by " + ApplicationRegistry.VERSION + " desc limit 1", new EntityRowMapper<ApplicationRegistry>(ApplicationRegistry.class));
        Map<Double, Method> versions = new TreeMap<Double, Method>();
        for (Method method : getClass().getMethods()) {
            Version version = method.getAnnotation(Version.class);
            if (version != null) {
                if (version.value() > applicationRegistry.getVersion()) {
                    versions.put(version.value(), method);
                }
            }
        }
        SimpleJdbcInsert insert = new SimpleJdbcInsert(application.getJdbcTemplate());
        insert.withTableName(TableUtilities.getTableName(ApplicationRegistry.class));
        for (Entry<Double, Method> entry : versions.entrySet()) {
            double version = entry.getKey();
            Method patch = entry.getValue();
            try {
                patch.invoke(this);
            } catch (Throwable e) {
                e.printStackTrace();
                LOGGER.info("migration version {} method {} error due to this reason {}", new Object[] { version, patch.getName(), e.getMessage() });
                return false;
            }
            Map<String, Object> fields = new HashMap<String, Object>();
            fields.put(ApplicationRegistry.VERSION, version);
            fields.put(ApplicationRegistry.UPGRADE_DATE, new Date());
            insert.execute(fields);
        }
        return true;
    }

}
