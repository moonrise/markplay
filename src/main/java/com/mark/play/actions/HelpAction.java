package com.mark.play.actions;

import com.mark.main.IMain;
import com.mark.Log;
import com.mark.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class HelpAction extends AbstractAction {
    private IMain main;

    public HelpAction(IMain main) {
        super("Help...");
        putValue(AbstractAction.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));

        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Log.log("Help action");
        createWindow();
    }

    private void createWindow() {
        JFrame frame = new JFrame(String.format("%s Help", Utils.AppName));
        createUI(frame);

        frame.setLocation(this.main.getAppFrame().getOffsetLocation(150, 50));
        frame.pack();
        frame.setVisible(true);
    }

    private void createUI(final JFrame frame){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        try {
            String docPath = Utils.getResourcePath("/docs/home.html");
            Log.log("Doc path: %s", docPath);
            File helpFile = new File(docPath);
            if (helpFile.exists()) {
                editorPane.setPage(helpFile.toURI().toURL());
            }
            else {
                editorPane.setText("<html>Could not locate the help file.</html>");
            }
        } catch (IOException e) {
            editorPane.setText("<html>Page not found.</html>");
        }

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(700,600));

        panel.add(scrollPane);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }
}
