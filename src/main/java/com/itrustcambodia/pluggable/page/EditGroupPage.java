package com.itrustcambodia.pluggable.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.database.EntityRowMapper;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.RoleGroup;
import com.itrustcambodia.pluggable.entity.UserGroup;
import com.itrustcambodia.pluggable.provider.RoleProvider;
import com.itrustcambodia.pluggable.provider.UserProvider;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.Role;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

@Mount("/eg")
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_EDIT_GROUP", description = "Access Edit Group Page") })
public class EditGroupPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    private Long groupId;

    @com.itrustcambodia.pluggable.widget.LabelField(label = "Name", order = 1)
    private String name;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Description", placeholder = "Description", order = 2)
    private String description;

    @com.itrustcambodia.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, order = 4, provider = RoleProvider.class)
    private com.itrustcambodia.pluggable.entity.Role[] roles;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Users", minimumInputLength = 1, order = 5, provider = UserProvider.class)
    private AbstractUser[] users;

    public EditGroupPage(PageParameters parameters) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        groupId = parameters.get("groupId").toLong();

        Group group = jdbcTemplate.queryForObject("select * from " + TableUtilities.getTableName(Group.class) + " where " + Group.ID + " = ?", new EntityRowMapper<Group>(Group.class), groupId);

        this.name = group.getName();

        this.description = group.getDescription();
        this.disable = group.isDisable();

        StringBuffer ddl = null;
        ddl = new StringBuffer();
        ddl.append("select role.* from " + TableUtilities.getTableName(com.itrustcambodia.pluggable.entity.Role.class) + " role ");
        ddl.append("inner join " + TableUtilities.getTableName(RoleGroup.class) + " role_group on role." + com.itrustcambodia.pluggable.entity.Role.ID + " = role_group." + RoleGroup.ROLE_ID + " ");
        ddl.append("where role_group." + RoleGroup.GROUP_ID + " = ?");
        List<com.itrustcambodia.pluggable.entity.Role> roles = jdbcTemplate.query(ddl.toString(), new EntityRowMapper<com.itrustcambodia.pluggable.entity.Role>(com.itrustcambodia.pluggable.entity.Role.class), groupId);
        this.roles = roles.toArray(new com.itrustcambodia.pluggable.entity.Role[roles.size()]);

        ddl = new StringBuffer();
        ddl.append("select user.* from " + TableUtilities.getTableName(application.getUserEntity()) + " user ");
        ddl.append("inner join " + TableUtilities.getTableName(UserGroup.class) + " user_group on user." + AbstractUser.ID + " = user_group." + UserGroup.USER_ID + " ");
        ddl.append("where user_group." + UserGroup.GROUP_ID + " = ?");
        List<AbstractUser> users = jdbcTemplate.query(ddl.toString(), new EntityRowMapper<AbstractUser>(AbstractUser.class), groupId);
        this.users = users.toArray(new AbstractUser[users.size()]);
    }

    @com.itrustcambodia.pluggable.widget.Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        return new Navigation(GroupManagementPage.class);
    }

    @com.itrustcambodia.pluggable.widget.Button(label = "Delete", validate = false, order = 2)
    public Navigation deleteClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(UserGroup.class) + " where " + UserGroup.GROUP_ID + " = ?", this.groupId);
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(Group.class) + " where " + Group.ID + " = ?", this.groupId);
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(RoleGroup.class) + " where " + RoleGroup.GROUP_ID + " = ?", this.groupId);
        return new Navigation(GroupManagementPage.class);
    }

    @com.itrustcambodia.pluggable.widget.Button(label = "Okay", validate = true, order = 3)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        StringBuffer ddl = new StringBuffer();
        ddl.append("update " + TableUtilities.getTableName(Group.class) + " set " + Group.DESCRIPTION + " = ?, " + Group.DISABLE + " = ? where " + Group.ID + " = ?");
        jdbcTemplate.update(ddl.toString(), this.description, this.disable, this.groupId);

        jdbcTemplate.update("delete from " + TableUtilities.getTableName(RoleGroup.class) + " where " + RoleGroup.GROUP_ID + " = ?", this.groupId);

        if (roles != null && roles.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleGroup.class));
            for (com.itrustcambodia.pluggable.entity.Role role : roles) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleGroup.ROLE_ID, role.getId());
                pp.put(RoleGroup.GROUP_ID, this.groupId);
                mapping.execute(pp);
            }
        }

        jdbcTemplate.update("delete from " + TableUtilities.getTableName(UserGroup.class) + " where " + UserGroup.GROUP_ID + " = ?", this.groupId);
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
        return new Navigation(GroupManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Group";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
