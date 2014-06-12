package com.angkorteam.pluggable.framework.mapper;

import com.angkorteam.pluggable.framework.database.EntityMapper;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Role;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by socheat on 12/06/14.
 */
public class AbstractUserMapper extends EntityMapper<AbstractUser> {

    public AbstractUserMapper() {
        super(AbstractUser.class);
    }

    @Override
    public AbstractUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        AbstractUser entity = new AbstractUser();
        entity.setId(rs.getLong(AbstractUser.ID));
        entity.setLogin(rs.getString(AbstractUser.LOGIN));
        entity.setPassword(rs.getString(AbstractUser.PASSWORD));
        entity.setDisable(rs.getBoolean(AbstractUser.DISABLE));
        return entity;
    }
}
