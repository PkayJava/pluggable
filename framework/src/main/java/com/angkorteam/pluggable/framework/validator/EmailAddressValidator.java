package com.angkorteam.pluggable.framework.validator;

import org.apache.wicket.extensions.validation.validator.RfcCompliantEmailAddressValidator;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;

/**
 * @author Socheat KHAUV
 */
public class EmailAddressValidator extends RfcCompliantEmailAddressValidator {

    /**
     * 
     */
    private static final long serialVersionUID = -9032543071235703119L;

    @Override
    protected ValidationError decorate(ValidationError error, IValidatable<String> validatable) {
        error.setVariable("input", validatable.getValue());
        return error;
    }

}
