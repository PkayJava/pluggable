package com.angkorteam.pluggable.panel;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;

/**
 * @author Socheat KHAUV
 */
public class InputFeedback extends ComponentFeedbackPanel {

    private static final long serialVersionUID = 2606478694551646850L;

    public InputFeedback(String id, Component filter) {
        super(id, filter);
    }

}
