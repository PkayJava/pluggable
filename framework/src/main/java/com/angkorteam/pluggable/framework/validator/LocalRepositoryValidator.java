package com.angkorteam.pluggable.framework.validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

import com.angkorteam.pluggable.framework.FrameworkConstants;

/**
 * @author Socheat KHAUV
 */
public class LocalRepositoryValidator extends AbstractFormValidator {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** form components to be checked. */
    private final FormComponent<?>[] components;

    private DropDownChoice<String> repository;

    private TextField<String> local;

    /**
     * Construct.
     * 
     * @param formComponent1
     *            a form component
     * @param local
     *            a form component
     */
    public LocalRepositoryValidator(DropDownChoice<String> repository, TextField<String> local) {
        if (repository == null) {
            throw new IllegalArgumentException("argument formComponent1 cannot be null");
        }
        if (local == null) {
            throw new IllegalArgumentException("argument formComponent2 cannot be null");
        }
        this.repository = repository;
        this.local = local;
        components = new FormComponent[] { repository, local };
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
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("input", local.getInput());

        String value = (String) repository.getInput();
        if (FrameworkConstants.REPOSITORY_LOCAL.equals(value)) {
            if (local.getInput() == null || "".equals(local.getInput())) {
                error(local, resourceKey() + ".empty", variables);
            } else {
                File file = new File(local.getInput());
                if (file.isDirectory()) {
                    if (!file.canWrite() || !file.canRead()) {
                        error(local, resourceKey() + ".access", variables);
                    }
                } else {
                    error(local, resourceKey() + ".directory", variables);
                }
            }
        }
    }
}
