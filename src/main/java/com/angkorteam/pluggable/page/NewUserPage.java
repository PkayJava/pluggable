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
import com.angkorteam.pluggable.entity.RoleUser;
import com.angkorteam.pluggable.entity.UserGroup;
import com.angkorteam.pluggable.provider.GroupProvider;
import com.angkorteam.pluggable.provider.RoleProvider;
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
@Mount("/nu")
@AuthorizeInstantiation(roles = { @com.angkorteam.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_NEW_USER", description = "Access New User Page") })
public final class NewUserPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 9069154930857964195L;

    @com.angkorteam.pluggable.widget.TextField(label = "Login", placeholder = "Login", order = 1)
    @NotNull
    @Unique(entity = AbstractUser.class, where = AbstractUser.LOGIN
            + " = :login")
    private String login;

    @NotNull
    @com.angkorteam.pluggable.widget.TextField(label = "Password", placeholder = "Password", order = 2)
    private String password;

    @com.angkorteam.pluggable.widget.CheckBox(label = "Disable", placeholder = "Disable Grant Access ?", order = 3)
    private boolean disable;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Roles", minimumInputLength = 1, provider = RoleProvider.class, order = 4)
    private Role[] roles;

    @com.angkorteam.pluggable.widget.Select2MultiChoice(label = "Groups", minimumInputLength = 1, provider = GroupProvider.class, order = 5)
    private Group[] groups;

    @com.angkorteam.pluggable.widget.Button(label = "Cancel", validate = false, order = 1)
    public Navigation cancelClick() {
        return new Navigation(UserManagementPage.class);

    }

    @com.angkorteam.pluggable.widget.Button(label = "Okay", validate = true, order = 2)
    public Navigation okayClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName(TableUtilities.getTableName(application
                .getUserEntity()));
        insert.usingGeneratedKeyColumns(AbstractUser.ID);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(AbstractUser.LOGIN, this.login);
        fields.put(AbstractUser.PASSWORD, this.password);
        fields.put(AbstractUser.DISABLE, this.disable);
        Long userId = insert.executeAndReturnKey(fields).longValue();

        if (roles != null && roles.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(RoleUser.class));
            for (Role role : roles) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(RoleUser.ROLE_ID, role.getId());
                pp.put(RoleUser.USER_ID, userId);
                mapping.execute(pp);
            }
        }

        if (groups != null && groups.length > 0) {
            SimpleJdbcInsert mapping = new SimpleJdbcInsert(jdbcTemplate);
            mapping.withTableName(TableUtilities.getTableName(UserGroup.class));
            for (Group group : groups) {
                Map<String, Object> pp = new HashMap<String, Object>();
                pp.put(UserGroup.USER_ID, userId);
                pp.put(UserGroup.GROUP_ID, group.getId());
                mapping.execute(pp);
            }
        }
        return new Navigation(UserManagementPage.class);
    }

    @Override
    public String getPageTitle() {
        return "New User";
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

}
