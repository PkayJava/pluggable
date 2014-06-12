package com.angkorteam.pluggable.framework.test;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Persistence;
import javax.persistence.PersistenceUnits;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class Pep {
    public static void main(String[] args){
        Persistence.createEntityManagerFactory("jpa");
    }
}
