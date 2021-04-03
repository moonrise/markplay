package com.mark.main;

import com.mark.play.actions.*;

import javax.swing.*;

public class ToolBar extends JToolBar {
    private IMain main;

    public ToolBar(IMain main) {
        this.main = main;

        add(new ExitAction(main));
        add(new OpenAction(main));
        add(new SaveAction(main));
        add(new HelpAction(main));
        // TODO: binary toolbar
        //add(new Separator());
        //add(new PlayOnLoadAction(main));
    }
}
