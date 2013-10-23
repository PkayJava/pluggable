package com.itrustcambodia.pluggable.page;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.reflections.ReflectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import com.itrustcambodia.pluggable.PluggableConstants;
import com.itrustcambodia.pluggable.core.AbstractWebApplication;
import com.itrustcambodia.pluggable.core.Menu;
import com.itrustcambodia.pluggable.core.Mount;
import com.itrustcambodia.pluggable.entity.AbstractUser;
import com.itrustcambodia.pluggable.entity.Group;
import com.itrustcambodia.pluggable.layout.AbstractLayout;
import com.itrustcambodia.pluggable.layout.InstallationLayout;
import com.itrustcambodia.pluggable.utilities.FrameworkUtilities;
import com.itrustcambodia.pluggable.utilities.GroupUtilities;
import com.itrustcambodia.pluggable.utilities.SecurityUtilities;
import com.itrustcambodia.pluggable.utilities.TableUtilities;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.type.Choice;
import com.itrustcambodia.pluggable.validation.type.Setting;
import com.itrustcambodia.pluggable.validation.type.TextFieldType;
import com.itrustcambodia.pluggable.validator.AmazonS3RepositoryValidator;
import com.itrustcambodia.pluggable.validator.LocalRepositoryValidator;
import com.itrustcambodia.pluggable.wicket.authroles.authorization.strategies.role.Roles;
import com.itrustcambodia.pluggable.widget.Button;

/**
 * @author Socheat KHAUV
 */
@Mount("/s")
public class InstallationPage extends KnownPage {

    private static final long serialVersionUID = -8037168822749291356L;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Login", placeholder = "Login", order = -1)
    @NotNull
    private String login;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Password", placeholder = "Password", order = 0, type = TextFieldType.PASSWORD)
    @NotNull
    private String password;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Server Address", placeholder = "Server Address", order = 1, type = TextFieldType.URL)
    @NotNull
    private String serverAddress;

    @com.itrustcambodia.pluggable.widget.DropDownChoice(label = "Repository", order = 2, choices = { @Choice(value = PluggableConstants.REPOSITORY_LOCAL, display = PluggableConstants.REPOSITORY_LOCAL), @Choice(value = PluggableConstants.REPOSITORY_AMAZON_S3, display = PluggableConstants.REPOSITORY_AMAZON_S3) })
    @NotNull
    private String repository;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Local", placeholder = "Local", order = 3)
    private String local;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Bucket Name", placeholder = "Bucket Name", order = 4)
    private String bucketName;

    @com.itrustcambodia.pluggable.widget.TextField(label = "Bucket Path", placeholder = "Bucket Path", order = 5, type = TextFieldType.URL)
    private String bucketPath;

    @com.itrustcambodia.pluggable.widget.TextField(label = "AWS Access Key", placeholder = "AWS Access Key", order = 6)
    private String accessKey;

    @com.itrustcambodia.pluggable.widget.TextField(label = "AWS Secret Key", placeholder = "AWS Secret Key", order = 7)
    private String secretKey;

    public InstallationPage() {
        initializeInterceptor();
        this.local = FileUtils.getTempDirectoryPath();
        this.serverAddress = FrameworkUtilities.getServerAddress((HttpServletRequest) getRequest().getContainerRequest());
    }

    private void initializeInterceptor() {
        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        for (Field field : ReflectionUtils.getAllFields(this.getClass())) {
            if (field.getAnnotation(Setting.class) != null) {
                Setting setting = field.getAnnotation(Setting.class);
                if (field.getType() != FileUpload.class && field.getType() != FileUpload[].class) {
                    try {
                        FieldUtils.writeField(field, this, application.select(setting.name(), field.getType()), true);
                    } catch (IllegalAccessException e) {
                    }
                }
            }
        }

        org.apache.wicket.markup.html.form.DropDownChoice<String> repository = (org.apache.wicket.markup.html.form.DropDownChoice<String>) getFormComponent("repository");
        org.apache.wicket.markup.html.form.TextField<String> local = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("local");
        getForm().add(new LocalRepositoryValidator(repository, local));

        org.apache.wicket.markup.html.form.TextField<String> accessKey = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("accessKey");
        org.apache.wicket.markup.html.form.TextField<String> secretKey = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("secretKey");
        org.apache.wicket.markup.html.form.TextField<String> bucketName = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("bucketName");
        org.apache.wicket.markup.html.form.TextField<String> bucketPath = (org.apache.wicket.markup.html.form.TextField<String>) getFormComponent("bucketPath");
        getForm().add(new AmazonS3RepositoryValidator(repository, accessKey, secretKey, bucketName, bucketPath));
    }

    @Override
    public String getPageTitle() {
        return "Installation";
    }

    @Override
    public AbstractLayout requestLayout(String id) {
        return new InstallationLayout(id);
    }

    @Button(label = "Okay", validate = true)
    public void okayClick() {

        AbstractWebApplication application = (AbstractWebApplication) getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate();

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
        insert.withTableName(TableUtilities.getTableName(application.getUserEntity()));
        insert.usingGeneratedKeyColumns(AbstractUser.ID);

        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(AbstractUser.LOGIN, this.login);
        fields.put(AbstractUser.PASSWORD, this.password);
        fields.put(AbstractUser.DISABLE, false);

        Long userId = insert.executeAndReturnKey(fields).longValue();

        Group group = GroupUtilities.createGroup(jdbcTemplate, AbstractWebApplication.SUPER_ADMIN_GROUP, AbstractWebApplication.SUPER_ADMIN_GROUP, false);
        SecurityUtilities.grantAccess(jdbcTemplate, group, userId);

        application.update(PluggableConstants.REPOSITORY, this.repository);
        application.update(PluggableConstants.LOCAL, this.local);
        application.update(PluggableConstants.AWS_S3_ACCESS_KEY, this.accessKey);
        application.update(PluggableConstants.AWS_S3_SECRET_KEY, this.secretKey);
        application.update(PluggableConstants.AWS_S3_BUCKET_NAME, this.bucketName);
        if (bucketPath != null && !"".equals(bucketPath)) {
            application.update(PluggableConstants.AWS_S3_BUCKET_PATH, this.bucketPath.endsWith("/") ? this.bucketPath.substring(0, this.bucketPath.length() - 1) : this.bucketPath);
        }
        application.update(PluggableConstants.SERVER_ADDRESS, this.serverAddress);
        application.update(PluggableConstants.DEBUG, false);
        application.update(PluggableConstants.DEPRECATED, false);

        setResponsePage(MigrationPage.class);
    }

    @Override
    public List<Menu> getPageMenus(Roles roles) {
        return null;
    }

}
