package com.angkorteam.pluggable.validator;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.quartz.CronExpression;

public class CronExpressionValidator implements IValidator<String> {

    /**
     * 
     */
    private static final long serialVersionUID = 9089737451064950952L;

    @Override
    public void validate(IValidatable<String> validatable) {
        if (validatable.getValue() != null && !"".equals(validatable.getValue())) {
            if (!CronExpression.isValidExpression(validatable.getValue())) {
                ValidationError error = new ValidationError(this);
                error.setVariable("input", validatable.getValue());
                validatable.error(error);
            }
        }
    }

}
