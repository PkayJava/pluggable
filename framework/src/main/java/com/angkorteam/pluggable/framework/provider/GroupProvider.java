package com.angkorteam.pluggable.framework.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.angkorteam.pluggable.framework.mapper.GroupMapper;
import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

/**
 * @author Socheat KHAUV
 */
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
        List<Group> groups = jdbcTemplate.query("select * from " + TableUtilities.getTableName(Group.class) + " where " + Group.NAME + " like ? and " + Group.DISABLE + " = ?", new GroupMapper(), term + "%", false);

        response.addAll(groups);
    }

    @Override
    public Collection<Group> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Group.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Group> groups = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(Group.class) + " where " + Group.ID + " in (:" + Group.ID + ")", params,new GroupMapper());

        return groups;
    }
}
