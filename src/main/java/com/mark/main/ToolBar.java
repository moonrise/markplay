package com.mark.main;

import com.mark.play.actions.ExitAction;
import com.mark.play.actions.HelpAction;
import com.mark.play.actions.OpenAction;
import com.mark.play.actions.SaveAction;

import javax.swing.*;

public class ToolBar extends JToolBar {
    private IMain main;

    public ToolBar(IMain main) {
        this.main = main;

        add(new ExitAction(main));
        add(new OpenAction(main));
        add(new SaveAction(main));
        add(new HelpAction(main));
    }
}
