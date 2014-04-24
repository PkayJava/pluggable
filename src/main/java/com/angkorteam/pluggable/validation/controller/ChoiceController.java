package com.angkorteam.pluggable.validation.controller;

import java.util.Map;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

/**
 * 
 * @author Socheat KHAUV
 * 
 */
public class ChoiceController implements IChoiceRenderer<Map<String, String>> {

    /**
     * 
     */
    private static final long serialVersionUID = 7726792946366920943L;

    @Override
    public Object getDisplayValue(Map<String, String> object) {
        return object.get("display");
    }

    @Override
    public String getIdValue(Map<String, String> object, int index) {
        return object.get("value");
    }

}
