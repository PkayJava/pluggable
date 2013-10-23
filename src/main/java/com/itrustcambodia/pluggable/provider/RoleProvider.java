package com.itrustcambodia.pluggable.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

/**
 * @author Socheat KHAUV
 */
public class RoleProvider extends TextChoiceProvider<Role> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    public RoleProvider() {
    }

    @Override
    protected String getDisplayText(Role choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Role choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Role> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Role> roles = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Role.class) + " where " + Role.NAME + " like ? and " + Role.DISABLE + " = ?", new EntityRowMapper<Role>(Role.class), term + "%", false);
        response.addAll(roles);
    }

    @Override
    public Collection<Role> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Role.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Role> roles = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Role.class) + " where " + Role.ID + " in (:" + Role.ID + ")", params, new EntityRowMapper<Role>(Role.class));

        return roles;
    }
}
