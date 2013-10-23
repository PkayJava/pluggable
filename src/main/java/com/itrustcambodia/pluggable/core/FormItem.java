package com.itrustcambodia.pluggable.core;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.wicket.markup.html.form.upload.FileUpload;

public class FormItem<T> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8022731765437578413L;

    private String label;

    private String placeholder;

    private Object userInput;

    private boolean delete;

    private String inputName;

    private List<T> list;

    private String pattern;

    private Class<?> classType;

    private boolean required;

    private int type;

    public static abstract class Type {

        public static final int INPUT_EMAIL = 1;
        public static final int INPUT_URL = 2;
        public static final int INPUT_TEXT = 3;
        public static final int INPUT_PASSWORD = 4;
        public static final int INPUT_NUMBER = 5;
        public static final int INPUT_DECIMAL = 6;

        // public static final int TEXTFIELD_DATETIME = 7;
        // public static final int INPUT_TIME = 9;
        public static final int INPUT_DATETIME = 8;

        public static final int INPUT_FILE = 15;

        public static final int TOGGLE_CHOICE = 10;

        public static final int SINGLE_CHOICE_RADIO = 13;
        public static final int SINGLE_CHOICE_DROPDOWN = 14;

        public static final int MULTIPLE_CHOICE_CHECK = 11;
        public static final int MULTIPLE_CHOICE_LIST = 12;

    }

    private FormItem() {
    }

    public static FormItem<Boolean> toggleChoice(String inputName, String label, String placeholder) {
        FormItem<Boolean> formItem = new FormItem<Boolean>();
        formItem.label = label;
        formItem.type = Type.TOGGLE_CHOICE;
        formItem.inputName = inputName;
        formItem.placeholder = placeholder;
        formItem.classType = Boolean.class;
        return formItem;
    }

    public static FormItem<FileUpload> fileInput(String inputName, String label) {
        FormItem<FileUpload> formItem = new FormItem<FileUpload>();
        formItem.label = label;
        formItem.type = Type.INPUT_FILE;
        formItem.inputName = inputName;
        formItem.classType = FileUpload.class;
        return formItem;
    }

    public static FormItem<String> emailInput(String inputName, String label, String placeholder) {
        return emailInput(inputName, label, placeholder, false);
    }

    public static FormItem<String> emailInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<String> formItem = new FormItem<String>();
        formItem.label = label;
        formItem.required = required;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_EMAIL;
        formItem.inputName = inputName;
        formItem.classType = String.class;
        return formItem;
    }

    public static FormItem<String> passwordInput(String inputName, String label, String placeholder) {
        return passwordInput(inputName, label, placeholder, false);
    }

    public static FormItem<String> passwordInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<String> formItem = new FormItem<String>();
        formItem.label = label;
        formItem.required = required;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_PASSWORD;
        formItem.inputName = inputName;
        formItem.classType = String.class;
        return formItem;
    }

    public static FormItem<Date> datetimeInput(String inputName, String label, String placeholder, String pattern) {
        return datetimeInput(inputName, label, placeholder, pattern, false);
    }

    public static FormItem<Date> datetimeInput(String inputName, String label, String placeholder, String pattern, boolean required) {
        FormItem<Date> formItem = new FormItem<Date>();
        formItem.label = label;
        formItem.required = required;
        formItem.pattern = pattern;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_DATETIME;
        formItem.inputName = inputName;
        formItem.classType = Date.class;
        return formItem;
    }

    public static FormItem<String> textInput(String inputName, String label, String placeholder) {
        return textInput(inputName, label, placeholder, false);
    }

    public static FormItem<String> textInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<String> formItem = new FormItem<String>();
        formItem.label = label;
        formItem.required = required;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_TEXT;
        formItem.inputName = inputName;
        formItem.classType = String.class;
        return formItem;
    }

    public static FormItem<String> urlInput(String inputName, String label, String placeholder) {
        return urlInput(inputName, label, placeholder, false);
    }

    public static FormItem<String> urlInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<String> formItem = new FormItem<String>();
        formItem.label = label;
        formItem.required = required;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_URL;
        formItem.inputName = inputName;
        formItem.classType = String.class;
        return formItem;
    }

    public static FormItem<Long> numberInput(String inputName, String label, String placeholder) {
        return numberInput(inputName, label, placeholder, false);
    }

    public static FormItem<Long> numberInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<Long> formItem = new FormItem<Long>();
        formItem.label = label;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_NUMBER;
        formItem.inputName = inputName;
        formItem.classType = Long.class;
        formItem.required = required;
        return formItem;
    }

    public static FormItem<Double> decimalInput(String inputName, String label, String placeholder) {
        return decimalInput(inputName, label, placeholder, false);
    }

    public static FormItem<Double> decimalInput(String inputName, String label, String placeholder, boolean required) {
        FormItem<Double> formItem = new FormItem<Double>();
        formItem.label = label;
        formItem.placeholder = placeholder;
        formItem.type = Type.INPUT_DECIMAL;
        formItem.inputName = inputName;
        formItem.classType = Double.class;
        formItem.required = required;
        return formItem;
    }

    public static <T> FormItem<T> multipleChoice(String inputName, String label, List<T> list, Class<T> clazz) {
        return multipleChoice(inputName, label, list, clazz, false);
    }

    public static <T> FormItem<T> multipleChoice(String inputName, String label, List<T> list, Class<T> clazz, boolean required) {
        FormItem<T> formItem = new FormItem<T>();
        formItem.label = label;
        if (list.size() > 4) {
            formItem.type = Type.MULTIPLE_CHOICE_LIST;
        } else {
            formItem.type = Type.MULTIPLE_CHOICE_CHECK;
        }
        formItem.list = list;
        formItem.inputName = inputName;
        formItem.classType = List.class;
        formItem.required = required;
        return formItem;
    }

    public static <T> FormItem<T> singleChoice(String inputName, String label, List<T> list, Class<T> clazz) {
        return singleChoice(inputName, label, list, clazz, false);
    }

    public static <T> FormItem<T> singleChoice(String inputName, String label, List<T> list, Class<T> clazz, boolean required) {
        FormItem<T> formItem = new FormItem<T>();
        formItem.label = label;
        if (list.size() > 4) {
            formItem.type = Type.SINGLE_CHOICE_DROPDOWN;
        } else {
            formItem.type = Type.SINGLE_CHOICE_RADIO;
        }
        formItem.list = list;
        formItem.inputName = inputName;
        formItem.required = required;
        formItem.classType = clazz;
        return formItem;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public void setClassType(Class<T> classType) {
        this.classType = classType;
    }

    public Object getUserInput() {
        return userInput;
    }

    public void setUserInput(Object userInput) {
        this.userInput = userInput;
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getLabel() {
        return label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public int getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }
}
