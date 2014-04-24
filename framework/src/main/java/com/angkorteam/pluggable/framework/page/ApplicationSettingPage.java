package com.angkorteam.pluggable.framework.page;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.reflections.ReflectionUtils;

import com.angkorteam.pluggable.framework.FrameworkConstants;
import com.angkorteam.pluggable.framework.core.AbstractWebApplication;
import com.angkorteam.pluggable.framework.core.Menu;
import com.angkorteam.pluggable.framework.utilities.FrameworkUtilities;
import com.angkorteam.pluggable.framework.validation.constraints.NotNull;
import com.angkorteam.pluggable.framework.validation.controller.Navigation;
import com.angkorteam.pluggable.framework.validation.type.ButtonType;
import com.angkorteam.pluggable.framework.validation.type.Setting;
import com.angkorteam.pluggable.framework.validation.type.TextFieldType;
import com.angkorteam.pluggable.framework.validator.LocalRepositoryValidator;
import com.angkorteam.pluggable.framework.wicket.authroles.Role;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.Roles;
import com.angkorteam.pluggable.framework.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.angkorteam.pluggable.framework.widget.Button;
import com.angkorteam.pluggable.framework.widget.CheckBox;
import com.angkorteam.pluggable.framework.widget.TextField;

/**
 * @author Socheat KHAUV
 */
@AuthorizeInstantiation(roles = { @Role(name = "ROLE_PAGE_SETTING", description = "Access Application Setting Page") })
public abstract class ApplicationSettingPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = 6086216013558631631L;

    @Setting(name = FrameworkConstants.SERVER_ADDRESS)
    @TextField(label = "Server Address", placeholder = "Server Address", order = 1, type = TextFieldType.URL)
    @NotNull
    private String serverAddress;

    @Setting(name = FrameworkConstants.LOCAL)
    @TextField(label = "Local", placeholder = "Local", order = 3)
    private String local;

    @Setting(name = FrameworkConstants.DEBUG)
    @CheckBox(label = "Debug Mode", placeholder = "Run in Debug Mode ?", order = 8)
    @NotNull
    private Boolean debugMode;

    @Setting(name = FrameworkConstants.DEPRECATED)
    @CheckBox(label = "Deprecated Mode", placeholder = "Allow Deprecated Mode ?", order = 9)
    @NotNull
    private Boolean deprecatedMode;

    public ApplicationSettingPage() {
        super();
        initializeInterceptor();
    }

    public ApplicationSettingPage(IModel<?> model) {
        super(model);
        initializeInterceptor();
    }

    public ApplicationSettingPage(PageParameters parameters) {
        super(parameters);
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                if (field.getType() != FileUpload.class
                        && field.getType() != FileUpload[].class) {
                    try {
                        FieldUtils.writeField(
                                field,
                                this,
                                application.select(setting.name(),
                                        field.getType()), true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        org.apache.wicket.markup.html.form.TextField<String> local = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("local");
        getForm().add(new LocalRepositoryValidator(local));

    }

    @Button(label = "Cancel", order = 1, type = ButtonType.DEFAULT, validate = false)
    public Navigation cancelClick() {
        return new Navigation(PluginManagementPage.class);
    }

    @Button(label = "Save", order = 2, type = ButtonType.DEFAULT, validate = true)
    public Navigation saveClick() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        if (field.getType() == FileUpload.class) {
                            try {
                                byte bytes[] = IOUtils
                                        .toByteArray(((FileUpload) value)
                                                .getInputStream());
                                ((FileUpload) value).closeStreams();
                                application.update(setting.name(), bytes);
                            } catch (IOException e) {
                            }
                        } else if (field.getType() == FileUpload[].class) {
                            List<byte[]> pp = new ArrayList<byte[]>();
                            for (FileUpload fileUpload : ((FileUpload[]) value)) {
                                try {
                                    byte[] bytes = IOUtils
                                            .toByteArray(fileUpload
                                                    .getInputStream());
                                    fileUpload.closeStreams();
                                    pp.add(bytes);
                                } catch (IOException e) {
                                }
                            }
                            application.update(setting.name(), pp);
                        } else {
                            application.update(setting.name(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }
        return new Navigation(application.getDashboardPage());
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        return FrameworkUtilities.getSecurityMenu(application, roles)
                .getChildren();
    }

    @Override
    public String getPageTitle() {
        return "Application Setting";
    }

}
