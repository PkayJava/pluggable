package com.angkorteam.pluggable.framework.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.core.Mount;
import com.angkorteam.pluggable.framework.database.EntityRowMapper;
import com.angkorteam.pluggable.framework.entity.AbstractUser;
import com.angkorteam.pluggable.framework.entity.Group;
import com.angkorteam.pluggable.framework.entity.RoleGroup;
import com.angkorteam.pluggable.framework.entity.UserGroup;
import com.angkorteam.pluggable.framework.provider.RoleProvider;
import com.angkorteam.pluggable.framework.provider.UserProvider;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.utilities.TableUtilities;
import com.angkorteam.pluggable.framework.validation.controller.Navigation;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/eg")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_GROUP", description = "Access Edit Group Page") })
public final class EditGroupPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    private Long groupId;

    @com.angkorteam.pluggable.framework.widget.LabelField(label = "Name", order = 1)
    private String name;

    @com.angkorteam.pluggable.framework.widget.TextField(label = "Description", placeholder = "Description", order = 2)
    private String description;

    @com.angkorteam.pluggable.framework.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.angkorteam.pluggable.framework.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, order = 4, provider = RoleProvider.class)
    private com.angkorteam.pluggable.framework.entity.Role[] roles;

    @com.angkorteam.pluggable.framework.widget.Select2MultiChoice(label = "Users", minimumInputLength = 1, order = 5, provider = UserProvider.class)
    private AbstractUser[] users;

    public EditGroupPage(PageParameters parameters) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        groupId = parameters.get("groupId").toLong();

        Group group = jdbcTemplate.queryForObject("select * from "
                + TableUtilities.getTableName(Group.class) + " where "
                + Group.ID + " = ?", new EntityRowMapper<Group>(Group.class),
                groupId);

        this.name = group.getName();

        this.description = group.getDescription();
        this.disable = group.isDisable();

        StringBuffer ddl = null;
        ddl = new StringBuffer();
        ddl.append("select role.* from "
                + TableUtilities
                        .getTableName(com.angkorteam.pluggable.framework.entity.Role.class)
                + " role ");
        ddl.append("inner join " + TableUtilities.getTableName(RoleGroup.class)
                + " role_group on role."
                + com.angkorteam.pluggable.framework.entity.Role.ID
                + " = role_group." + RoleGroup.ROLE_ID + " ");
        ddl.append("where role_group." + RoleGroup.GROUP_ID + " = ?");
        List<com.angkorteam.pluggable.framework.entity.Role> roles = jdbcTemplate
                .query(ddl.toString(),
                        new EntityRowMapper<com.angkorteam.pluggable.framework.entity.Role>(
                                com.angkorteam.pluggable.framework.entity.Role.class),
                        groupId);
        this.roles = roles
                .toArray(new com.angkorteam.pluggable.framework.entity.Role[roles
                        .size()]);

        ddl = new StringBuffer();
        ddl.append("select user.* from "
                + TableUtilities.getTableName(application.getUserEntity())
                + " user ");
        ddl.append("inner join " + TableUtilities.getTableName(UserGroup.class)
                + " user_group on user." + AbstractUser.ID + " = user_group."
                + UserGroup.USER_ID + " ");
        ddl.append("where user_group." + UserGroup.GROUP_ID + " = ?");
        List<AbstractUser> users = jdbcTemplate.query(ddl.toString(),
                new EntityRowMapper<AbstractUser>(AbstractUser.class), groupId);
        this.users = users.toArray(new AbstractUser[users.size()]);
    }

    @com.angkorteam.pluggable.framework.widget.Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return new Navigation(application.getGroupManagementPage());
    }

    @com.angkorteam.pluggable.framework.widget.Button(label = "Delete", validate = false, order = 2)
    public Navigation deleteClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate
                .update("delete from "
                        + TableUtilities.getTableName(UserGroup.class)
                        + " where " + UserGroup.GROUP_ID + " = ?", this.groupId);
        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(Group.class)
                        + " where " + Group.ID + " = ?", this.groupId);
        jdbcTemplate
                .update("delete from "
                        + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.GROUP_ID + " = ?", this.groupId);
        return new Navigation(application.getGroupManagementPage());
    }

    @com.angkorteam.pluggable.framework.widget.Button(label = "Okay", validate = true, order = 3)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        StringBuffer ddl = new StringBuffer();
        ddl.append("update " + TableUtilities.getTableName(Group.class)
                + " set " + Group.DESCRIPTION + " = ?, " + Group.DISABLE
                + " = ? where " + Group.ID + " = ?");
        jdbcTemplate.update(ddl.toString(), this.description, this.disable,
                this.groupId);

        jdbcTemplate
                .update("delete from "
                        + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.GROUP_ID + " = ?", this.groupId);

        if (roles != null && roles.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleGroup.class));
            for (com.angkorteam.pluggable.framework.entity.Role role : roles) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleGroup.ROLE_ID, role.getId());
                pp.put(RoleGroup.GROUP_ID, this.groupId);
                mapping.execute(pp);
            }
        }

        jdbcTemplate
                .update("delete from "
                        + TableUtilities.getTableName(UserGroup.class)
                        + " where " + UserGroup.GROUP_ID + " = ?", this.groupId);
        if (users != null && users.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(UserGroup.class));
            for (AbstractUser user : users) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(UserGroup.USER_ID, user.getId());
                pp.put(UserGroup.GROUP_ID, this.groupId);
                mapping.execute(pp);
            }
        }
        return new Navigation(application.getGroupManagementPage());
    }

    @Override
    public String getPageTitle() {
        return "Edit Group";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
