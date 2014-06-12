package javax.persistence;

import com.angkorteam.pluggable.framework.database.DbSupport;
import com.angkorteam.pluggable.framework.database.JdbcTable;
import com.angkorteam.pluggable.framework.database.Schema;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.sql.SQLException;

/**
 * MySQL-specific table.
 */
@java.lang.annotation.Target({ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface Exclude {
}