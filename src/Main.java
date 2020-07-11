import model.Graph;
import utils.GraphIO;
import view.GraphFrame;
import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
  * @version 10.0
 *
 * This class is the main class of the application that takes care of initialisation and starting up the program.
 *
 * @see GraphPanel
 * @see GraphIO
 * @see UIManager
 */
public class Main {

    /**
     * The main method that loads a graph and creates a new GraphFrame.
     * @param args optional arguments, i.e. the path the graph can be loaded from.
     */
    public static void main(String[] args) {
        setLookAndFeel();

        String loadPath = getGraphLoadPath(args);
        if (loadPath != null && loadPath.length() > 0)
            System.out.println("Loading graph " + loadPath);

        Graph graph = new Graph(loadPath);
        EventQueue.invokeLater(() -> new GraphFrame(graph));
    }

    /**
     * Sets the look and feel on different operation systems, so the application feels more native.
     */
    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) { //NOTE(Boris): We don't really care what exception happened - getting the exact L&F isn't important.
            System.err.println("Failed to set system look and feel");
        }
    }

    /**
     * Gets the load path for a graph.
     * @param args optional arguments for load path
     * @return the load path of the graph
     */
    private static String getGraphLoadPath(String[] args) {
        if (args.length > 1)
            System.out.println("Ignoring extra command line arguments...");

        if (args.length >= 1)
            return args[0];
        else {
            Properties lastSession = GraphIO.loadLastSession();
            if (lastSession != null)
                return lastSession.getProperty("lastFile", null);
            else
                return null;
        }
    }
}