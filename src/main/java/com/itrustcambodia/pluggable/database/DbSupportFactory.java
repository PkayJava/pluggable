package com.itrustcambodia.pluggable.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;

import com.itrustcambodia.pluggable.database.mysql.MySQLDbSupport;

public class DbSupportFactory {
    /**
     * Logger.
     */
    // private static final Log LOG = LogFactory.getLog(DbSupportFactory.class);

    /**
     * Prevent instantiation.
     */
    private DbSupportFactory() {
        // Do nothing
    }

    /**
     * Initializes the appropriate DbSupport class for the database product used
     * by the data source.
     * 
     * @param connection
     *            The Jdbc connection to use to query the database.
     * @return The appropriate DbSupport class.
     */
    public static DbSupport createDbSupport(JdbcTemplate jdbcTemplate) {
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
        String databaseProductName = getDatabaseProductName(connection);
        JdbcUtils.closeConnection(connection);

        if (databaseProductName.startsWith("Apache Derby")) {
            // return new DerbyDbSupport(connection);
            return null;
        }
        if (databaseProductName.startsWith("H2")) {
            // return new H2DbSupport(connection);
            return null;
        }
        if (databaseProductName.contains("HSQL Database Engine")) {
            // For regular Hsql and the Google Cloud SQL local default DB.
            // return new HsqlDbSupport(connection);
            return null;
        }
        if (databaseProductName.startsWith("Microsoft SQL Server")) {
            // return new SQLServerDbSupport(connection);
            return null;
        }
        if (databaseProductName.contains("MySQL")) {
            // For regular MySQL and Google Cloud SQL.
            // Google Cloud SQL returns different names depending on the
            // environment and the SDK version.
            // ex.: Google SQL Service/MySQL
            return new MySQLDbSupport(jdbcTemplate);
        }
        if (databaseProductName.startsWith("Oracle")) {
            // return new OracleDbSupport(connection);
            return null;
        }
        if (databaseProductName.startsWith("PostgreSQL")) {
            // return new PostgreSQLDbSupport(connection);
            return null;
        }
        if (databaseProductName.startsWith("DB2")) {
            // DB2 also returns the OS it's running on.
            // ex.: DB2/NT
            // return new DB2DbSupport(connection);
            return null;
        }

        throw new DatabaseException("Unsupported Database: " + databaseProductName);
    }

    /**
     * Retrieves the name of the database product.
     * 
     * @param connection
     *            The connection to use to query the database.
     * @return The name of the database product. Ex.: Oracle, MySQL, ...
     */
    private static String getDatabaseProductName(Connection connection) {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            if (databaseMetaData == null) {
                throw new DatabaseException("Unable to read database metadata while it is null!");
            }

            String databaseProductName = databaseMetaData.getDatabaseProductName();
            if (databaseProductName == null) {
                throw new DatabaseException("Unable to determine database. Product name is null.");
            }

            int databaseMajorVersion = databaseMetaData.getDatabaseMajorVersion();
            int databaseMinorVersion = databaseMetaData.getDatabaseMinorVersion();

            return databaseProductName + " " + databaseMajorVersion + "." + databaseMinorVersion;
        } catch (SQLException e) {
            throw new DatabaseException("Error while determining database product name", e);
        }
    }

}