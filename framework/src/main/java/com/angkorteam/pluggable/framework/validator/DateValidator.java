package com.angkorteam.pluggable.framework.validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * @author Socheat KHAUV
 */
public class DateValidator implements IValidator<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -8682334093480730873L;

    private String format;

    public DateValidator(String format) {
        this.format = format;
    }

    @Override
    public void validate(IValidatable<String> validatable) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(this.format);
        if (validatable.getValue() != null && !"".equals(validatable.getValue())) {
            try {
                dateFormat.parse(validatable.getValue());
            } catch (ParseException e) {
                ValidationError error = new ValidationError(this);
                error.setVariable("input", validatable.getValue());
                error.setVariable("pattern", this.format);
                validatable.error(error);
            }
        }
    }

}
