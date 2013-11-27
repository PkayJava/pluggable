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
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.entity.RoleUser;
import com.itrustcambodia.pluggable.entity.UserGroup;
import com.itrustcambodia.pluggable.provider.GroupProvider;
import com.itrustcambodia.pluggable.provider.RoleProvider;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;

/**
 * @author Socheat KHAUV
 */
@Mount("/eu")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_EDIT_USER", description = "Access Edit User Page") })
public class EditUserPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    private Long userId;

    @com.itrustcambodia.pluggable.widget.LabelField(label = "Login", order = 1)
    private String login;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Password", placeholder = "Password", order = 2)
    @NotNull
    private String password;

    @com.itrustcambodia.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, provider = RoleProvider.class, order = 4)
    private Role[] roles;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Groups", minimumInputLength = 1, provider = GroupProvider.class, order = 5)
    private Group[] groups;

    public EditUserPage(PageParameters parameters) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        long userId = parameters.get("userId").toLong();

        Map<String, Object> user = jdbcTemplate.queryForMap("select * from " + TableUtilities.getTableName(application.getUserEntity()) + " where " + AbstractUser.ID + " = ?", userId);

        this.userId = userId;
        this.login = (String) user.get(AbstractUser.LOGIN);
        this.password = (String) user.get(AbstractUser.PASSWORD);
        if (user.get(AbstractUser.DISABLE) != null) {
            this.disable = (Boolean) user.get(AbstractUser.DISABLE);
        }

        StringBuffer ddl = null;
        ddl = new StringBuffer();
        ddl.append("select role.* from " + TableUtilities.getTableName(Role.class) + " role ");
        ddl.append("inner join " + TableUtilities.getTableName(RoleUser.class) + " role_user on role." + Role.ID + " = role_user." + RoleUser.ROLE_ID + " ");
        ddl.append("where role_user." + RoleUser.USER_ID + " = ?");
        List<Role> roles = jdbcTemplate.query(ddl.toString(), new EntityRowMapper<Role>(Role.class), userId);
        this.roles = roles.toArray(new Role[roles.size()]);

        ddl = new StringBuffer();
        ddl.append("select `group`.* from " + TableUtilities.getTableName(Group.class) + " `group` ");
        ddl.append("inner join " + TableUtilities.getTableName(UserGroup.class) + " user_group on `group`." + Group.ID + " = user_group." + UserGroup.GROUP_ID + " ");
        ddl.append("where user_group." + UserGroup.USER_ID + " = ?");
        List<Group> groups = jdbcTemplate.query(ddl.toString(), new EntityRowMapper<Group>(Group.class), userId);
        this.groups = groups.toArray(new Group[groups.size()]);
    }

    @Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return new Navigation(application.getUserManagementPage());
    }

    @Button(label = "Delete", validate = false, order = 2)
    public Navigation deleteClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        jdbcTemplate.update("delete from " + TableUtilities.getTableName(UserGroup.class) + " where " + UserGroup.USER_ID + " = ?", this.userId);
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(AbstractUser.class) + " where " + AbstractUser.ID + " = ?", this.userId);
        jdbcTemplate.update("delete from " + TableUtilities.getTableName(RoleUser.class) + " where " + RoleUser.USER_ID + " = ?", this.userId);

        return new Navigation(application.getUserManagementPage());
    }

    @Button(label = "Okay", validate = true, order = 3)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        StringBuffer ddl = new StringBuffer();
        ddl.append("update " + TableUtilities.getTableName(application.getUserEntity()) + " set " + AbstractUser.PASSWORD + " = ?, " + AbstractUser.DISABLE + " = ? where " + AbstractUser.ID + " = ?");
        jdbcTemplate.update(ddl.toString(), this.password, this.disable, this.userId);

        jdbcTemplate.update("delete from " + TableUtilities.getTableName(RoleUser.class) + " where " + RoleUser.USER_ID + " = ?", this.userId);

        if (roles != null && roles.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleUser.class));
            for (Role role : roles) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleUser.ROLE_ID, role.getId());
                pp.put(RoleUser.USER_ID, this.userId);
                mapping.execute(pp);
            }
        }

        jdbcTemplate.update("delete from " + TableUtilities.getTableName(UserGroup.class) + " where " + UserGroup.USER_ID + " = ?", this.userId);
        if (groups != null && groups.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(UserGroup.class));
            for (Group group : groups) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(UserGroup.USER_ID, this.userId);
                pp.put(UserGroup.GROUP_ID, group.getId());
                mapping.execute(pp);
            }
        }
        return new Navigation(application.getUserManagementPage());
    }

    @Override
    public String getPageTitle() {
        return "Edit User";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles).getChildren();
    }

}
