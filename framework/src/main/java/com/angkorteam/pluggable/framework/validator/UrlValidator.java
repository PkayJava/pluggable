package com.angkorteam.pluggable.framework.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

/**
 * @author Socheat KHAUV
 */
public class UrlValidator extends org.apache.wicket.validation.validator.UrlValidator {

    /**
     * 
     */
    private static final long serialVersionUID = -8804288373103388884L;

    @Override
    protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
        error.setVariable("input", validatable.getValue());
        return error;
    }

}
