package com.angkorteam.pluggable.page;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.angkorteam.pluggable.core.AbstractWebApplication;
import com.angkorteam.pluggable.core.Menu;
import com.angkorteam.pluggable.core.Mount;
import com.angkorteam.pluggable.entity.AbstractUser;
import com.angkorteam.pluggable.entity.Group;
import com.angkorteam.pluggable.entity.Role;
import com.angkorteam.pluggable.entity.RoleGroup;
import com.angkorteam.pluggable.entity.UserGroup;
import com.angkorteam.pluggable.provider.RoleProvider;
import com.angkorteam.pluggable.provider.UserProvider;
import com.angkorteam.pluggable.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.utilities.TableUtilities;
import com.angkorteam.pluggable.validation.constraints.NotNull;
import com.angkorteam.pluggable.validation.constraints.Unique;
import com.angkorteam.pluggable.validation.controller.Navigation;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;

/**
 * @author Socheat KHAUV
 */
@Mount("/ng")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_NEW_GROUP", description = "Access New Group Page") })
public final class NewGroupPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    @NotNull
    @com.angkorteam.pluggable.widget.TextField(label = "Name", placeholder = "Name", order = 1)
    @Unique(entity = Group.class, where = Group.NAME + " = :name")
    private String name;

    @com.angkorteam.pluggable.widget.TextField(label = "Description", placeholder = "Description", order = 2)
    private String description;

    @com.angkorteam.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, order = 4, provider = RoleProvider.class)
    private Role[] roles;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Users", minimumInputLength = 1, order = 5, provider = UserProvider.class)
    private AbstractUser[] users;

    @com.angkorteam.pluggable.widget.Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return new Navigation(application.getGroupManagementPage());
    }

    @com.angkorteam.pluggable.widget.Button(label = "Okay", validate = true, order = 2)
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
