package com.itrustcambodia.pluggable.validation.controller;

import java.io.Serializable;

import com.itrustcambodia.pluggable.widget.Button;

public class ButtonController implements Comparable<ButtonController>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7967196581610148655L;

    private Button button;

    private String name;

    private double order;

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    @Override
    public int compareTo(ButtonController o) {
        return Double.valueOf(this.order).compareTo(o.order);
    }

}
