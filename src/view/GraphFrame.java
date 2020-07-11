package view;

import controller.KeyboardController;
import controller.MenuBar;
import controller.SelectionController;
import model.Graph;
import controller.animation.Animation;
import utils.GraphIO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

/**
 * @author Boris
 * @author Jana
 * @version 3.0
 *
 * The McGraph application frame.
 *
 * @see Graph
 * @see GraphPanel
 * @see GraphIO
 */
public class GraphFrame extends JFrame implements Observer {

    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    private final Graph graph;

    /**
     * Constructs a new GraphFrame for a given GraphModel. The last application session properties will also be loaded
     * from the appropriate file and used to set the width/height of the frame, etc.
     *
     * @param graph The GraphModel that this GraphFrame will be viewing.
     */
    public GraphFrame(Graph graph) {
        super(deriveTitleFrom(graph));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        URL iconURL = Thread.currentThread().getContextClassLoader().getResource("icon.png");
        Image icon = Toolkit.getDefaultToolkit().getImage(iconURL);
        setIconImage(icon);

        GraphPanel panel = new GraphPanel(graph);
        add(panel);

        setJMenuBar(new MenuBar(graph, panel));
        new SelectionController(graph, panel);
        new KeyboardController(graph, panel, this);

        Properties lastSession = GraphIO.loadLastSession();
        int width, height;
        if (lastSession == null) {
            width = DEFAULT_WIDTH;
            height = DEFAULT_HEIGHT;
        } else {
            try {
                width = Integer.parseInt(lastSession.getProperty("windowWidth",  String.valueOf(DEFAULT_WIDTH)));
                height = Integer.parseInt(lastSession.getProperty("windowHeight",  String.valueOf(DEFAULT_HEIGHT)));
                String backgroundRGB = lastSession.getProperty("backgroundColor", null);
                if (backgroundRGB != null)
                    panel.setBackground(new Color((int)Long.parseLong(backgroundRGB, 16)));
            } catch (NumberFormatException e) {
                width = DEFAULT_WIDTH;
                height = DEFAULT_HEIGHT;
            }
        }

        setPreferredSize(new Dimension(width, height));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GraphIO.saveSession(graph, GraphFrame.this, panel);
                if (graph.hasUnsavedChanges())
                    promptToSaveBeforeClosing();
                else
                    dispose();
            }
        });

        this.graph = graph;
        graph.addObserver(this);
        graph.clearChangedSinceLastSave();

        panel.centerViewOnGraph();

        Animation.Manager.startAnimating();
    }

    /**
     * Dispatched a WINDOW_CLOSING event to this JFrame, causing it to close.
     */
    public void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This method is called when the GraphModel of this frame sends a notification. The Frame will set a new title
     * based on the state of the GraphModel.
     *
     * @param obj The Observable that changed - IGNORED.
     * @param arg The Message associated with the notification - IGNORED.
     */
    @Override
    public void update(Observable obj, Object arg) {
        String newTitle = deriveTitleFrom(graph);
        setTitle(newTitle);
    }

    /**
     * Stops all Animations, and then calls JFrame.dispose().
     *
     * @see Animation
     */
    @Override
    public void dispose() {
        Animation.Manager.stopAnimating();
        super.dispose();
    }

    /**
     * Asks the user if they want to save the GraphModel they are working on before closing the Frame and the
     * Application. The user can choose to either save and quit, quit without saving, or not quit at all.
     */
    private void promptToSaveBeforeClosing() {
        int userChoice = JOptionPane.showConfirmDialog(this,
                "You have unsaved work. Would you like to save before exiting?",
                "Save before quitting?",
                JOptionPane.YES_NO_CANCEL_OPTION);

        if (userChoice == JOptionPane.YES_OPTION) {
            String saveFile = graph.getFilename();
            if (saveFile == null || saveFile.length() == 0)
                saveFile = GraphIO.chooseSaveFile(graph.getFilename());
            GraphIO.saveGraph(graph, saveFile);
        }

        if (userChoice != JOptionPane.CANCEL_OPTION)
            dispose();
    }

    /**
     * @param graph The GraphModel to derive a title from.
     * @return A string representing a title appropriate for the given GraphModel.
     */
    private static String deriveTitleFrom(Graph graph) {
        String title = graph.getName();
        if (title == null || title.length() == 0)
            title = "untitled graph";
        if (graph.hasUnsavedChanges())
            title = "*" + title;
        return title + " - McGraph";
    }
}