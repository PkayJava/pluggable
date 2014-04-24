package com.angkorteam.pluggable.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.core.Menu;
import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.database.EntityRowMapper;
import com.angkorteam.pluggable.entity.AbstractUser;
import com.angkorteam.pluggable.entity.Group;
import com.angkorteam.pluggable.entity.Role;
import com.angkorteam.pluggable.entity.RoleGroup;
import com.angkorteam.pluggable.entity.RoleUser;
import com.angkorteam.pluggable.provider.GroupProvider;
import com.angkorteam.pluggable.provider.UserProvider;
import com.angkorteam.pluggable.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.utilities.TableUtilities;
import com.angkorteam.pluggable.validation.controller.Navigation;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.angkorteam.pluggable.widget.LabelField;

/**
 * @author Socheat KHAUV
 */
@Mount("/er")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_EDIT_ROLE", description = "Access Edit Role Page") })
public final class EditRolePage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    private Long roleId;

    @LabelField(label = "Name", order = 1)
    private String name;

    @LabelField(label = "Description", order = 2)
    private String description;

    @com.angkorteam.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Groups", minimumInputLength = 1, order = 4, provider = GroupProvider.class)
    private Group[] groups;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Groups", minimumInputLength = 1, order = 4, provider = UserProvider.class)
    private AbstractUser[] users;

    public EditRolePage(PageParameters parameters) {

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        roleId = parameters.get("roleId").toLong();

        Role role = jdbcTemplate.queryForObject("select * from "
                + TableUtilities.getTableName(Role.class) + " where " + Role.ID
                + " = ?", new EntityRowMapper<Role>(Role.class), roleId);

        this.name = role.getName();
        this.description = role.getDescription();
        this.disable = role.isDisable();

        StringBuffer ddl = null;
        ddl = new StringBuffer();
        ddl.append("select `group`.* from "
                + TableUtilities.getTableName(Group.class) + " `group` ");
        ddl.append("inner join " + TableUtilities.getTableName(RoleGroup.class)
                + " role_group on `group`." + Group.ID + " = role_group."
                + RoleGroup.GROUP_ID + " ");
        ddl.append("where role_group." + RoleGroup.ROLE_ID + " = ?");
        List<Group> groups = jdbcTemplate.query(ddl.toString(),
                new EntityRowMapper<Group>(Group.class), roleId);
        this.groups = groups.toArray(new Group[groups.size()]);

        ddl = new StringBuffer();
        ddl.append("select user.* from "
                + TableUtilities.getTableName(AbstractUser.class) + " user ");
        ddl.append("inner join " + TableUtilities.getTableName(RoleUser.class)
                + " role_user on user." + AbstractUser.ID + " = role_user."
                + RoleUser.USER_ID + " ");
        ddl.append("where role_user." + RoleUser.ROLE_ID + " = ?");
        List<AbstractUser> users = jdbcTemplate.query(ddl.toString(),
                new EntityRowMapper<AbstractUser>(AbstractUser.class), roleId);
        this.users = users.toArray(new AbstractUser[users.size()]);
    }

    @com.angkorteam.pluggable.widget.Button(label = "Cancel", order = 1, validate = false)
    public Navigation cancelClick() {
        return new Navigation(RoleManagementPage.class);
    }

    @com.angkorteam.pluggable.widget.Button(label = "Okay", order = 2, validate = true)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();
        StringBuffer ddl = new StringBuffer();
        ddl.append("update " + TableUtilities.getTableName(Role.class)
                + " set " + Role.DISABLE + " = ? where " + Role.ID + " = ?");
        jdbcTemplate.update(ddl.toString(), this.disable, this.roleId);

        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleGroup.class)
                        + " where " + RoleGroup.ROLE_ID + " = ?", this.roleId);

        if (groups != null && groups.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleGroup.class));
            for (Group group : groups) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleGroup.ROLE_ID, this.roleId);
                pp.put(RoleGroup.GROUP_ID, group.getId());
                mapping.execute(pp);
            }
        }

        jdbcTemplate.update(
                "delete from " + TableUtilities.getTableName(RoleUser.class)
                        + " where " + RoleUser.ROLE_ID + " = ?", this.roleId);
        if (users != null && users.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleUser.class));
            for (AbstractUser user : users) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleUser.USER_ID, user.getId());
                pp.put(RoleUser.ROLE_ID, this.roleId);
                mapping.execute(pp);
            }
        }
        return new Navigation(RoleManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "Edit Role";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
