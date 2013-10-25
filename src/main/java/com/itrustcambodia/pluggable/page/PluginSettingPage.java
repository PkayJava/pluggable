package com.itrustcambodia.pluggable.page;

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

import com.itrustcambodia.pluggable.core.AbstractPlugin;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.entity.PluginRegistry;
import com.itrustcambodia.pluggable.migration.AbstractPluginMigrator;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.controller.Navigation;
import com.itrustcambodia.pluggable.validation.type.Setting;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import com.itrustcambodia.pluggable.widget.Button;

/**
 * @author Socheat KHAUV
 */
@AuthorizeInstantiation(roles = { @com.itrustcambodia.pluggable.wicket.authroles.Role(name = "ROLE_PAGE_PLUGIN_SETTING", description = "Access Plugin Setting Page") })
public abstract class PluginSettingPage extends KnownPage {

    /**
     * 
     */
    private static final long serialVersionUID = -5187283242246199367L;

    public PluginSettingPage() {
        super();
        initializeInterceptor();
    }

    public PluginSettingPage(IModel<?> model) {
        super(model);
        initializeInterceptor();
    }

    public PluginSettingPage(PageParameters parameters) {
        super(parameters);
        initializeInterceptor();
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());

        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                if (field.getType() != FileUpload.class && field.getType() != FileUpload[].class) {
                    try {
                        FieldUtils.writeField(field, this, application.select(identity, setting.name(), field.getType()), true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        AbstractPlugin plugin = application.getPlugin(identity);
        if (!plugin.isMigrated()) {
            getFormButton("dashboard").setVisible(false);
        } else {
            getFormButton("dashboard").setVisible(plugin.isActivated());
        }

        getFormButton("upgrade").setVisible(!plugin.isMigrated());

        getFormButton("deactivate").setVisible(plugin.isActivated());

        if (plugin.isMigrated()) {
            getFormButton("activate").setVisible(!plugin.isActivated());
        } else {
            getFormButton("activate").setVisible(false);
        }
    }

    @Button(label = "Goto Dashboard", validate = false, order = 1)
    public Navigation dashboard() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());
        AbstractPlugin plugin = application.getPlugin(identity);
        return new Navigation(plugin.getDashboardPage());
    }

    @Button(label = "Save", validate = true, order = 2)
    public Navigation save() {

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());
        AbstractPlugin plugin = application.getPlugin(identity);

        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                try {
                    Object value = FieldUtils.readField(field, this, true);
                    if (value != null) {
                        if (field.getType() == FileUpload.class) {
                            try {
                                byte bytes[] = IOUtils.toByteArray(((FileUpload) value).getInputStream());
                                ((FileUpload) value).closeStreams();
                                application.update(identity, setting.name(), bytes);
                            } catch (IOException e) {
                            }
                        } else if (field.getType() == FileUpload[].class) {
                            List<byte[]> pp = new ArrayList<byte[]>();
                            for (FileUpload fileUpload : ((FileUpload[]) value)) {
                                try {
                                    byte[] bytes = IOUtils.toByteArray(fileUpload.getInputStream());
                                    fileUpload.closeStreams();
                                    pp.add(bytes);
                                } catch (IOException e) {
                                }
                            }
                            application.update(identity, setting.name(), pp);
                        } else {
                            application.update(identity, setting.name(), value);
                        }
                    }
                } catch (IllegalAccessException e) {
                }
            }
        }
        return new Navigation(plugin.getSettingPage());
    }

    @Button(label = "Activate", validate = true, order = 3)
    public Navigation activate() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());
        AbstractPlugin plugin = application.getPlugin(identity);
        if (plugin.activate(application)) {
            application.getJdbcTemplate().update("update " + TableUtilities.getTableName(PluginRegistry.class) + " set " + PluginRegistry.ACTIVATED + " = ? where " + PluginRegistry.IDENTITY + " = ?", true, identity);
        }
        return new Navigation(plugin.getSettingPage());

    }

    @Button(label = "Deactivate", validate = true, order = 4)
    public Navigation deactivate() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());
        AbstractPlugin plugin = application.getPlugin(identity);
        plugin.deactivate();
        application.getJdbcTemplate().update("update " + TableUtilities.getTableName(PluginRegistry.class) + " set " + PluginRegistry.ACTIVATED + " = ? where " + PluginRegistry.IDENTITY + " = ?", false, identity);
        return new Navigation(plugin.getSettingPage());
    }

    @Button(label = "Upgrade", validate = true, order = 5)
    public Navigation upgrade() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        String identity = application.getPluginMapping(this.getClass().getName());

        AbstractPlugin plugin = application.getPlugin(identity);

        AbstractPluginMigrator migrator = (AbstractPluginMigrator) application.getBean(plugin.getMigrator().getName());
        migrator.upgrade();

        return new Navigation(plugin.getSettingPage());
    }

    @Button(label = "Cancel", validate = false, order = -1)
    public Navigation cancel() {
        return new Navigation(PluginManagementPage.class);
    }

}
