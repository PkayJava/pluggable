package com.itrustcambodia.pluggable.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

public class GroupProvider extends TextChoiceProvider<Group> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupProvider.class);

    public GroupProvider() {
    }

    @Override
    protected String getDisplayText(Group choice) {
        return choice.getName();
    }

    @Override
    protected Object getId(Group choice) {
        return choice.getId();
    }

    @Override
    public void query(String term, int page, Response<Group> response) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();
        List<Group> groups = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Group.class) + " where " + Group.NAME + " like ? and " + Group.DISABLE + " = ?", new EntityRowMapper<Group>(Group.class), term + "%", false);

        response.addAll(groups);
    }

    @Override
    public Collection<Group> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Group.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Group> groups = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Group.class) + " where " + Group.ID + " in (:" + Group.ID + ")", params, new EntityRowMapper<Group>(Group.class));

        return groups;
    }
}
