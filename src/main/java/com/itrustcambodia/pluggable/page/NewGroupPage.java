package com.itrustcambodia.pluggable.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.entity.Role;
import com.itrustcambodia.pluggable.entity.RoleGroup;
import com.itrustcambodia.pluggable.entity.UserGroup;
import com.itrustcambodia.pluggable.provider.RoleProvider;
import com.itrustcambodia.pluggable.provider.UserProvider;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.constraints.Unique;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/ng")
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_NEW_GROUP", description = "Access New Group Page") })
public final class NewGroupPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    @NotNull
    @com.itrustcambodia.pluggable.widget.TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Group.class, where = Group.NAME + " = :name")
    private String name;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Description", placeholder = "Description", order = 2)
    private String description;

    @com.itrustcambodia.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, order = 4, provider = RoleProvider.class)
    private Role[] roles;

    @com.itrustcambodia.pluggable.widget.Select2MultiChoice(label = "Users", minimumInputLength = 1, order = 5, provider = UserProvider.class)
    private AbstractUser[] users;

    @com.itrustcambodia.pluggable.widget.Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return new Navigation(application.getGroupManagementPage());
    }

    @com.itrustcambodia.pluggable.widget.Button(label = "Okay", validate = true, order = 2)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName(TableUtilities.getTableName(Group.class));
        insert.usingGeneratedKeyColumns(Group.ID);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(Group.NAME, this.name);
        fields.put(Group.DESCRIPTION, this.description);
        fields.put(Group.DISABLE, this.disable);
        Long groupId = insert.executeAndReturnKey(fields).longValue();

        if (roles != null && roles.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleGroup.class));
            for (Role role : roles) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleGroup.ROLE_ID, role.getId());
                pp.put(RoleGroup.GROUP_ID, groupId);
                mapping.execute(pp);
            }
        }

        if (users != null && users.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(UserGroup.class));
            for (AbstractUser user : users) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(UserGroup.USER_ID, user.getId());
                pp.put(UserGroup.GROUP_ID, groupId);
                mapping.execute(pp);
            }
        }
        return new Navigation(application.getGroupManagementPage());
    }

    @Override
    public String getPageTitle() {
        return "New Group";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
