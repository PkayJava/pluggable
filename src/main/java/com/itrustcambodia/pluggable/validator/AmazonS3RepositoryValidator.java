package com.itrustcambodia.pluggable.validator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.itrustcambodia.pluggable.PluggableConstants;

public class AmazonS3RepositoryValidator extends AbstractFormValidator {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** form components to be checked. */
    private final FormComponent<?>[] components;

    private DropDownChoice<String> repository;

    private TextField<String> accessKey;

    private TextField<String> secretKey;

    private TextField<String> bucketPath;

    private TextField<String> bucketName;

    /**
     * Construct.
     * 
     * @param formComponent1
     *            a form component
     * @param local
     *            a form component
     */
    public AmazonS3RepositoryValidator(DropDownChoice<String> repository, TextField<String> accessKey, TextField<String> secretKey, TextField<String> bucketName, TextField<String> bucketPath) {
        if (repository == null) {
            throw new IllegalArgumentException("argument formComponent1 cannot be null");
        }
        if (accessKey == null) {
            throw new IllegalArgumentException("argument formComponent2 cannot be null");
        }
        if (secretKey == null) {
            throw new IllegalArgumentException("argument formComponent3 cannot be null");
        }
        if (bucketName == null) {
            throw new IllegalArgumentException("argument formComponent4 cannot be null");
        }
        if (bucketPath == null) {
            throw new IllegalArgumentException("argument formComponent5 cannot be null");
        }
        this.repository = repository;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.bucketName = bucketName;
        this.bucketPath = bucketPath;
        components = new FormComponent[] { repository, accessKey, secretKey, bucketName, bucketPath };
    }

    /**
     * @see org.apache.wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
     */
    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return components;
    }

    /**
     * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
     */
    @Override
    public void validate(Form<?> form) {

        String value = repository.getInput();
        if (PluggableConstants.REPOSITORY_AMAZON_S3.equals(value)) {
            boolean error = false;
            if (accessKey.getInput() == null || "".equals(accessKey.getInput())) {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("input", accessKey.getInput());
                error(accessKey, resourceKey() + ".accessKey", variables);
                error = true;
            }
            if (secretKey.getInput() == null || "".equals(secretKey.getInput())) {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("input", secretKey.getInput());
                error(secretKey, resourceKey() + ".secretKey", variables);
                error = true;
            }
            if (bucketName.getInput() == null || "".equals(bucketName.getInput())) {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("input", bucketName.getInput());
                error(bucketName, resourceKey() + ".bucketName");
                error = true;
            }
            if (bucketPath.getInput() == null || "".equals(bucketPath.getInput())) {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("input", bucketPath.getInput());
                error(bucketPath, resourceKey() + ".bucketPath");
                error = true;
            }
            if (!error) {
                BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey.getInput(), secretKey.getInput());
                AmazonS3Client client = new AmazonS3Client(credentials);

                AmazonS3Client anonymousClient = new AmazonS3Client(new AnonymousAWSCredentials());

                File file = new File(FileUtils.getTempDirectoryPath(), "text.txt");
                try {
                    FileUtils.write(file, String.valueOf(System.currentTimeMillis()));
                } catch (IOException e) {
                }
                try {
                    client.putObject(bucketName.getInput(), "text.txt", file);
                    try {
                        anonymousClient.getObject(bucketName.getInput(), "text.txt");
                    } catch (AmazonClientException e) {
                        Map<String, Object> variables = new HashMap<String, Object>();
                        variables.put("input", bucketName.getInput());
                        error(bucketPath, resourceKey() + ".anonymousRead", variables);
                    }

                    try {
                        client.deleteObject(bucketName.getInput(), "text.txt");
                    } catch (AmazonClientException e) {
                        Map<String, Object> variables = new HashMap<String, Object>();
                        variables.put("input", bucketPath.getInput());
                        error(bucketPath, resourceKey() + ".delete", variables);
                    }
                } catch (AmazonClientException e) {
                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put("input", bucketPath.getInput());
                    error(bucketPath, resourceKey() + ".write", variables);
                }
            }
        }
    }
}
