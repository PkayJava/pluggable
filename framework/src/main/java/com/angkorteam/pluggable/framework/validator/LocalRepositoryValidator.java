package com.angkorteam.pluggable.framework.validator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;

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

    private TextField<String> local;

    /**
     * Construct.
     * 
     * @param formComponent1
     *            a form component
     * @param local
     *            a form component
     */
    public LocalRepositoryValidator(TextField<String> local) {
        if (local == null) {
            throw new IllegalArgumentException(
                    "argument formComponent2 cannot be null");
        }
        this.local = local;
        components = new FormComponent[] { local };
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
