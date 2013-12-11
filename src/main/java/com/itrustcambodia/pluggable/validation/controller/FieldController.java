package com.itrustcambodia.pluggable.validation.controller;

import java.io.Serializable;

import com.itrustcambodia.pluggable.validation.constraints.DecimalMax;
import com.itrustcambodia.pluggable.validation.constraints.DecimalMin;
import com.itrustcambodia.pluggable.validation.constraints.Digits;
import com.itrustcambodia.pluggable.validation.constraints.Future;
import com.itrustcambodia.pluggable.validation.constraints.Max;
import com.itrustcambodia.pluggable.validation.constraints.Min;
import com.itrustcambodia.pluggable.validation.constraints.NotNull;
import com.itrustcambodia.pluggable.validation.constraints.Past;
import com.itrustcambodia.pluggable.validation.constraints.Pattern;
import com.itrustcambodia.pluggable.validation.constraints.Size;
import com.itrustcambodia.pluggable.validation.constraints.Unique;
import com.itrustcambodia.pluggable.widget.CheckBox;
import com.itrustcambodia.pluggable.widget.CheckBoxMultipleChoice;
import com.itrustcambodia.pluggable.widget.DropDownChoice;
import com.itrustcambodia.pluggable.widget.FileUploadField;
import com.itrustcambodia.pluggable.widget.ImageField;
import com.itrustcambodia.pluggable.widget.LabelField;
import com.itrustcambodia.pluggable.widget.ListMultipleChoice;
import com.itrustcambodia.pluggable.widget.MultiFileUploadField;
import com.itrustcambodia.pluggable.widget.RadioChoice;
import com.itrustcambodia.pluggable.widget.Select2Choice;
import com.itrustcambodia.pluggable.widget.Select2MultiChoice;
import com.itrustcambodia.pluggable.widget.TextArea;
import com.itrustcambodia.pluggable.widget.TextField;

/**
 * 
 * @author Socheat KHAUV
 * 
 */
public class FieldController implements Comparable<FieldController>,
        Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4112265688170083562L;

    private TextField textField;

    private ImageField imageField;

    private CheckBox checkBox;

    private LabelField labelField;

    private Select2MultiChoice select2MultiChoice;

    private Select2Choice select2Choice;

    private CheckBoxMultipleChoice checkBoxMultipleChoice;

    private ListMultipleChoice listMultipleChoice;

    private FileUploadField fileUploadField;

    private MultiFileUploadField multiFileUploadField;

    private RadioChoice radioChoice;

    private TextArea textArea;

    private DropDownChoice dropDownChoice;

    private String name;

    private NotNull notNull;

    private Size size;

    private Unique unique;

    private Pattern pattern;

    private Past past;

    private Min min;

    private Max max;

    private Future future;

    private DecimalMax decimalMax;

    private DecimalMin decimalMin;

    private Class<?> type;

    private Class<?> elementType;

    private Digits digits;

    private double order;

    public NotNull getNotNull() {
        return notNull;
    }

    public void setNotNull(NotNull notNull) {
        this.notNull = notNull;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Past getPast() {
        return past;
    }

    public void setPast(Past past) {
        this.past = past;
    }

    public Min getMin() {
        return min;
    }

    public void setMin(Min min) {
        this.min = min;
    }

    public Max getMax() {
        return max;
    }

    public void setMax(Max max) {
        this.max = max;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public DecimalMax getDecimalMax() {
        return decimalMax;
    }

    public void setDecimalMax(DecimalMax decimalMax) {
        this.decimalMax = decimalMax;
    }

    public DecimalMin getDecimalMin() {
        return decimalMin;
    }

    public void setDecimalMin(DecimalMin decimalMin) {
        this.decimalMin = decimalMin;
    }

    public Digits getDigits() {
        return digits;
    }

    public void setDigits(Digits digits) {
        this.digits = digits;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public CheckBoxMultipleChoice getCheckBoxMultipleChoice() {
        return checkBoxMultipleChoice;
    }

    public void setCheckBoxMultipleChoice(
            CheckBoxMultipleChoice checkBoxMultipleChoice) {
        this.checkBoxMultipleChoice = checkBoxMultipleChoice;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public void setElementType(Class<?> elementType) {
        this.elementType = elementType;
    }

    public ListMultipleChoice getListMultipleChoice() {
        return listMultipleChoice;
    }

    public void setListMultipleChoice(ListMultipleChoice listMultipleChoice) {
        this.listMultipleChoice = listMultipleChoice;
    }

    public FileUploadField getFileUploadField() {
        return fileUploadField;
    }

    public void setFileUploadField(FileUploadField fileUploadField) {
        this.fileUploadField = fileUploadField;
    }

    public MultiFileUploadField getMultiFileUploadField() {
        return multiFileUploadField;
    }

    public void setMultiFileUploadField(
            MultiFileUploadField multiFileUploadField) {
        this.multiFileUploadField = multiFileUploadField;
    }

    public RadioChoice getRadioChoice() {
        return radioChoice;
    }

    public void setRadioChoice(RadioChoice radioChoice) {
        this.radioChoice = radioChoice;
    }

    public DropDownChoice getDropDownChoice() {
        return dropDownChoice;
    }

    public void setDropDownChoice(DropDownChoice dropDownChoice) {
        this.dropDownChoice = dropDownChoice;
    }

    public TextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(TextArea textArea) {
        this.textArea = textArea;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    @Override
    public int compareTo(FieldController o) {
        return Double.valueOf(this.order).compareTo(o.order);
    }

    public Unique getUnique() {
        return unique;
    }

    public void setUnique(Unique unique) {
        this.unique = unique;
    }

    public Select2MultiChoice getSelect2MultiChoice() {
        return select2MultiChoice;
    }

    public void setSelect2MultiChoice(Select2MultiChoice select2MultiChoice) {
        this.select2MultiChoice = select2MultiChoice;
    }

    public Select2Choice getSelect2Choice() {
        return select2Choice;
    }

    public void setSelect2Choice(Select2Choice select2Choice) {
        this.select2Choice = select2Choice;
    }

    public LabelField getLabelField() {
        return labelField;
    }

    public void setLabelField(LabelField labelField) {
        this.labelField = labelField;
    }

    public ImageField getImageField() {
        return imageField;
    }

    public void setImageField(ImageField imageField) {
        this.imageField = imageField;
    }

}
