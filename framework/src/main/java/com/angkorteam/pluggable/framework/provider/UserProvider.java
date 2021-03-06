package com.angkorteam.pluggable.framework.provider;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.angkorteam.pluggable.framework.mapper.AbstractUserMapper;
import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.vaynberg.wicket.select2.Response;
import com.vaynberg.wicket.select2.TextChoiceProvider;

/**
 * @author Socheat KHAUV
 */
public class UserProvider extends TextChoiceProvider<AbstractUser> {

    /**
     * 
     */
    private static final long serialVersionUID = -200059333918010136L;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserProvider.class);

    public UserProvider() {
    }

    @Override
    protected String getDisplayText(AbstractUser choice) {
        return choice.getLogin();
    }

    @Override
    protected Object getId(AbstractUser choice) {
        return String.valueOf(choice.getId());
    }

    @Override
    public void query(String term, int page, Response<AbstractUser> response) {
        AbstractWebApplication application = (AbstractWebApplication) Application.get();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        List<AbstractUser> users = jdbcTemplate.query("select * from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.LOGIN + " like ? and " + AbstractUser.DISABLE + " = ?", new AbstractUserMapper(), term + "%", false);
        response.addAll(users);
    }

    @Override
    public Collection<AbstractUser> toChoices(Collection<String> ids) {
        JdbcTemplate jdbcTemplate = ((AbstractWebApplication) Application.get()).getJdbcTemplate();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(AbstractUser.ID, ids);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<AbstractUser> users = namedParameterJdbcTemplate.query("select * from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.ID + " in (:" + AbstractUser.ID + ")", params, new AbstractUserMapper());

        return users;
    }
}
