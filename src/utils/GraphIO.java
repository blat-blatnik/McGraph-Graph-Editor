package utils;

import model.*;
import view.GraphFrame;
import view.GraphPanel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @version 10.0
 *
 * A collection of utilities for saving and loading graph files, session properties, etc. Methods in this class can load
 * graphs from both an EXTENDED format which can be used to save ALL of the information in a GraphModel or from a NON
 * EXTENDED format, which can only save SOME of the information from a graph model (see below). Graphs are always SAVED
 * in the EXTENDED format.
 *
 * ============================================================
 *
 * A file in the EXTENDED format is structured like this:
 *
 * #extended format
 * NUM_NODES(int) NUM_EDGES(int) NUM_FONTS(int)
 * FONT[0:NUM_FONTS]..
 * NODE[0:NUM_NODES]..
 * EDGE[0:NUM_EDGES]..
 * START_NODE_INDEX(int) GOAL_NODE_INDEX(int)
 *
 * FONTs in the EXTENDED format are written like this:
 * STYLE(int) SIZE(int) NAME(String)
 *
 * NODEs in the EXTENDED format are written like this:
 * X(float) Y(float) WIDTH(float) HEIGHT(float) STYLE(NodeStyle) FILL_RGB(int) BORDER_RGB(int) TEXT_RGB(int) FONT_INDEX(int) TEXT(String)
 *
 * EDGEs in the EXTENDED format are written like this:
 * INDEX_NODE1(int) INDEX_NODE2(int) WEIGHT(float) X(float) Y(float) DIRECTION(EdgeDirection) STYLE(EdgeStyle) COLOR_RGB(int)
 *
 * ============================================================
 *
 * A file in the NON EXTENDED format is structured like this:
 *
 * NUM_NODES(int) NUM_EDGES(int)
 * NODE[0:NUM_FONTS]..
 * EDGE[0:NUM_EDGES]..
 *
 * NODEs in the NON EXTENDED format are written like this:
 * X(int) Y(int) WIDTH(int) HEIGHT(int) TEXT(String)
 *
 * EDGES in the NON EXTENDED format are written like this:
 * INDEX_NODE1(int) INDEX_NODE2(int)
 *
 * ============================================================
 *
 * @see Graph
 * @see Node
 * @see Edge
 */
public final class GraphIO {

    private static final String DATA_DIRECTORY = "McGraph";
    private static final String SESSION_FILENAME = "session.properties";

    /**
     * Attempts to save the data from the given GraphModel to the given file. The files will be saved in the EXTENDED
     * format, and files saved in this way can later be losslessly loaded via loadGraph(). If any exception occurs
     * during file saving, the file will most likely be left in a partially corrupted state - unable to be loaded again.
     * The user will be notified if this occurs.
     *
     * @param graph The GraphModel to save to the file. The graph will NOT be modified during this function.
     * @param filepath A path to the file to which the graph will be saved to.
     */
    public static void saveGraph(Graph graph, String filepath) {
        if (filepath == null || filepath.length() == 0)
            return;

        String extension = getFileExtension(filepath);
        if (!extension.equals("graph"))
            filepath += ".graph";

        try(FileWriter fileWriter = new FileWriter(filepath)) {
            writeGraph(graph, fileWriter);
            graph.setFilename(filepath);
            graph.clearChangedSinceLastSave();
            System.out.println("Save successful");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Couldn't save to " + filepath + ".\n" + e.getLocalizedMessage(),
                    "Failed to save", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Attempts to load the given GraphModel with the data saved in the given graph file. This function can be called
     * with files in either the EXTENDED or the NON-EXTENDED graph formats. If any exception occurs during loading of
     * the graph, the graph will be restored to its original state before calling this function and the user will be
     * notified.
     *
     * @param graph The GraphModel to load to - the graph might be modified during this function.
     * @param filepath The path of the graph file from which to load. The file can be in either the EXTENDED or NON-EXTENDED formats.
     */
    public static void loadGraph(Graph graph, String filepath) {
        if (filepath == null || filepath.length() == 0)
            return;

        Graph backup = new Graph(graph);

        try(BufferedReader reader = new BufferedReader(new FileReader(filepath))) {

            reader.mark(1024);
            String firstLine = reader.readLine();
            reader.reset();

            if (firstLine.equals("#extended format"))
                loadGraphFromExtendedFormat(graph, reader);
            else
                loadGraphFromNonExtendedFormat(graph, reader);

            graph.setFilename(filepath);
            graph.clearChangedSinceLastSave();
            System.out.println("Load successful");
        } catch (Exception e) {
            graph.set(backup);
            JOptionPane.showMessageDialog(null,
                    "Couldn't load from " + filepath + ".\n" + e.getLocalizedMessage(),
                    "Failed to load", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Presents a dialog to the user where they can choose which file to save their graph to.
     *
     * @param startDirectory The file or directory path from which the file chooser dialog will start from.
     * @return A String containing the absolute path to the file the user chose to save to, or null if the user does not choose any file.
     */
    public static String chooseSaveFile(String startDirectory) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(startDirectory));
        chooser.setDialogTitle("Save to");

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile().getAbsolutePath();
        else
            return null;
    }

    /**
     * Presents a dialog to the user where they can choose which file to load their a graph from.
     *
     * @param startDirectory The file or directory path from which the file chooser dialog will start from.
     * @return A String containing the absolute path to the file the user chose to load from, or null if the user does not choose any file.
     */
    public static String chooseLoadFile(String startDirectory) {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(startDirectory));
        chooser.setDialogTitle("Choose a graph-file to load");
        chooser.setFileFilter(new FileNameExtensionFilter("Graph files", "graph"));
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            return chooser.getSelectedFile().getAbsolutePath();
        else
            return null;
    }

    /**
     * Saves some cross-session information such as the last file opened and the window height/width so that they can
     * be loaded the next time the application is started and save the user some time if all they want to do is pick
     * up exactly where they left off. All of this saved to the SESSION_FILENAME file.
     *
     * @param graph The GraphModel that is currently open.
     * @param frame The application frame.
     * @param panel The GraphPanel.
     */
    public static void saveSession(Graph graph, GraphFrame frame, GraphPanel panel) {
        File dataDirectory = new File(DATA_DIRECTORY);
        dataDirectory.mkdir();

        Properties properties = new Properties();
        properties.setProperty("lastFile", graph.getFilename());
        properties.setProperty("windowWidth", String.valueOf(frame.getWidth()));
        properties.setProperty("windowHeight", String.valueOf(frame.getHeight()));
        properties.setProperty("backgroundColor", String.format("%08X", panel.getBackground().getRGB()));

        try(FileWriter fileWriter = new FileWriter(DATA_DIRECTORY + "/" + SESSION_FILENAME)) {
            properties.store(fileWriter, "McGraph last session properties");
        } catch (IOException e) {
            System.err.println("Failed to save session properties.");
        }
    }

    /**
     * @return The last application session properties loaded from the SESSION_FILENAME file, or null if the properties could not be loaded.
     */
    public static Properties loadLastSession() {
        try(FileReader reader = new FileReader(DATA_DIRECTORY + "/" + SESSION_FILENAME)) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * @param filepath A path to a file.
     * @return The file extension of the file pointed to by the file path, or an empty string if the file has no extension.
     */
    public static String getFileExtension(String filepath) {
        String filename = new File(filepath).getName();
        int extensionStart = filename.lastIndexOf('.');
        if (extensionStart <= 0)
            return "";
        return filename.substring(extensionStart + 1);
    }

    /**
     * @param filepath A path to a file.
     * @return The filename pointed to by the given file path, without the file extension. E.g. "dir/file.graph" -> "file".
     */
    public static String getFilenameWithoutExtension(String filepath) {
        String filename = new File(filepath).getName();
        int extensionStart = filename.lastIndexOf('.');
        if (extensionStart <= 0)
            return filename;
        return filename.substring(0, extensionStart);
    }

    /**
     * Writes all the Fonts, Nodes, and Edges from a given GraphModel to a given file stream in the EXTENDED format.
     * The index of the "start" and "goal" Nodes of the graph are also written. The file resulting from this operation
     * is only be valid if the file being written to is completely empty before calling this function.
     *
     * @param graph The GraphModel whose Fonts, Nodes, and Edges to write.
     * @param writer The FileWriter to which data will be written to.
     * @throws IOException If the writer fails to write for whatever reason.
     */
    private static void writeGraph(Graph graph, FileWriter writer) throws IOException {
        List<Node> nodes = graph.getNodes();
        List<Edge> edges = graph.getEdges();

        Set<Font> usedFonts = new HashSet<>();
        for (Node node : nodes)
            usedFonts.add(node.getActualFont());
        Font[] fonts = new Font[usedFonts.size()];
        fonts = usedFonts.toArray(fonts);

        writer.write("#extended format\n");
        writer.write(String.format("%d %d %d\n", nodes.size(), edges.size(), fonts.length));
        writeFonts(fonts, writer);
        writeNodes(nodes, fonts, writer);
        writeEdges(nodes, edges, writer);

        Node start = graph.getStartNode();
        Node goal = graph.getGoalNode();
        int startIndex = start != null ? nodes.indexOf(start) : -1;
        int goalIndex = goal != null ? nodes.indexOf(goal) : -1;
        writer.write(String.format("%d %d", startIndex, goalIndex));
    }

    /**
     * Writes a given array of Fonts to a given file stream in the EXTENDED format.
     *
     * @param fonts The array of Fonts to write to the file.
     * @param writer The FileWriter to which the Font data will be written to.
     * @throws IOException If the writer fails to write for whatever reason.
     */
    private static void writeFonts(Font[] fonts, FileWriter writer) throws IOException {
        for (Font font : fonts) {
            writer.write(String.format("%d %d %s\n",
                    font.getStyle(), font.getSize(), font.getName()));
        }
    }

    /**
     * Writes a given List of Nodes from a GraphModel to a given file stream in the EXTENDED format.
     *
     * @param nodes The List of Nodes to write to the file.
     * @param fonts An array of Fonts used by all Nodes.
     * @param writer The FileWriter to which the Node data will be written to.
     * @throws IOException If the writer fails to write for whatever reason.
     */
    private static void writeNodes(List<Node> nodes, Font[] fonts, FileWriter writer) throws IOException {
        for (Node node : nodes) {
            Rectangle2D bounds = node.getActualBounds();
            int fillRGB = node.getActualFillColor().getRGB();
            int borderRGB = node.getActualBorderColor().getRGB();
            int textRGB = node.getActualTextColor().getRGB();
            int fontIndex = ListUtil.indexOf(fonts, node.getActualFont());
            writer.write(String.format("%g %g %g %g %s %08X %08X %08X %d %s\n",
                    bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), node.getActualStyle(),
                    fillRGB, borderRGB, textRGB, fontIndex, node.getActualName()));
        }
    }

    /**
     * Writes a given List of Edges from a GraphModel to a given file stream in the EXTENDED format.
     *
     * @param nodes The List of Nodes that the Edges to write might connect to.
     * @param edges The List of Edges to write to the file.
     * @param writer A FileWriter to which the Edge data will be written to.
     * @throws IOException If the writer fails to write for whatever reason.
     */
    private static void writeEdges(List<Node> nodes, List<Edge> edges, FileWriter writer) throws IOException {
        for (Edge edge : edges) {
            Node node1 = edge.getNode1();
            Node node2 = edge.getNode2();
            int indexNode1 = nodes.indexOf(node1);
            int indexNode2 = nodes.indexOf(node2);
            double weight = edge.getActualWeight();
            double weightX = edge.getActualWeightPoint().getX();
            double weightY = edge.getActualWeightPoint().getY();
            EdgeDirection direction = edge.getActualDirection();
            EdgeStyle style = edge.getActualStyle();
            int colorRGB = edge.getActualColor().getRGB();
            writer.write(String.format("%d %d %g %g %g %s %s %08X\n",
                    indexNode1, indexNode2, weight, weightX, weightY, direction, style, colorRGB));
        }
    }

    /**
     * Initializes the Nodes and Edges of a given GraphModel with the data from a given BufferedReader. The buffered
     * reader is assumed to read EXTENDED format graph data.
     *
     * @param graph The GraphModel whose data to set using the data read from the reader.
     * @param reader The BufferedReader of an EXTENDED format data-stream.
     * @throws IOException If the reader fails to read a line.
     */
    private static void loadGraphFromExtendedFormat(Graph graph, BufferedReader reader) throws IOException {
        reader.readLine(); // Skip the initial header '#extended format' - just assume it's there.

        String[] sizeStrings = reader.readLine().split(" ");
        int numNodes = Integer.parseInt(sizeStrings[0]);
        int numEdges = Integer.parseInt(sizeStrings[1]);
        int numFonts = Integer.parseInt(sizeStrings[2]);

        Font[] fonts = new Font[numFonts];
        for (int i = 0; i < numFonts; ++i)
            fonts[i] = parseFontExtended(reader.readLine());

        graph.clear();
        for (int i = 0; i < numNodes; ++i)
            graph.add(parseNodeExtended(fonts, reader.readLine()));
        for (int i = 0; i < numEdges; ++i)
            graph.add(parseEdgeExtended(graph.getNodes(), reader.readLine()));

        List<Node> nodes = graph.getNodes();

        String[] startAndGoalStrings = reader.readLine().split(" ");
        int startIndex = Integer.parseInt(startAndGoalStrings[0]);
        int goalIndex = Integer.parseInt(startAndGoalStrings[1]);
        if (startIndex >= 0)
            graph.setStartNode(nodes.get(startIndex));
        if (goalIndex >= 0)
            graph.setGoalNode(nodes.get(goalIndex));
    }

    /**
     * Initializes the Nodes and Edges of a given GraphModel with the data from a given BufferedReader. The buffered
     * reader is assumed to read NON-EXTENDED graph data, and will initialize the graph with default values for Nodes
     * and Edges where the non-extended format does not provide any values.
     *
     * @param graph The GraphModel whose data to set using the data read from the reader.
     * @param reader The BufferedReader of a NON-EXTENDED format data stream.
     * @throws IOException If the reader fails to read a line.
     */
    private static void loadGraphFromNonExtendedFormat(Graph graph, BufferedReader reader) throws IOException {
        String[] sizeStrings = reader.readLine().split(" ");
        int numNodes = Integer.parseInt(sizeStrings[0]);
        int numEdges = Integer.parseInt(sizeStrings[1]);

        graph.clear();
        for (int i = 0; i < numNodes; ++i)
            graph.add(parseNodeNonExtended(reader.readLine()));
        for (int i = 0; i < numEdges; ++i)
            graph.add(parseEdgeNonExtended(graph.getNodes(), reader.readLine()));
    }

    /**
     * @param fonts A List of Fonts that this Node might use.
     * @param nodeData The String containing Node data in the extended format that should be parsed.
     * @return A Node resulting from parsing the given extended format String.
     */
    private static Node parseNodeExtended(Font[] fonts, String nodeData) {
        // This is the format of each line:
        // x y width height style fillRGB borderRGB textRGB fontIndex text

        String[] splitData = nodeData.split(" ", 10);
        double x = Double.parseDouble(splitData[0]);
        double y = Double.parseDouble(splitData[1]);
        double width = Double.parseDouble(splitData[2]);
        double height = Double.parseDouble(splitData[3]);
        NodeStyle style = NodeStyle.valueOf(splitData[4]);
        int fillRGB = (int)Long.parseLong(splitData[5], 16);
        int borderRGB = (int)Long.parseLong(splitData[6], 16);
        int textRGB = (int)Long.parseLong(splitData[7], 16);
        int fontIndex = Integer.parseInt(splitData[8]);
        String name = splitData[9];

        Color fillColor = new Color(fillRGB);
        Color borderColor = new Color(borderRGB);
        Color textColor = new Color(textRGB);
        Font font = fonts[fontIndex];

        return new Node(name, x, y, width, height, style, fillColor, borderColor, textColor, font);
    }

    /**
     * @param nodes The List of Nodes that the parsed Edge might connect to.
     * @param edgeData The String containing Edge data in the extended format that should be parsed.
     * @return An Edge resulting from parsing the given extended format String.
     */
    private static Edge parseEdgeExtended(List<Node> nodes, String edgeData) {
        // This is the format of each line:
        // node1 node2 weight x y direction style color

        String[] splitData = edgeData.split(" ");
        int node1Index = Integer.parseInt(splitData[0]);
        int node2Index = Integer.parseInt(splitData[1]);
        double weight = Double.parseDouble(splitData[2]);
        double weightX = Double.parseDouble(splitData[3]);
        double weightY = Double.parseDouble(splitData[4]);
        EdgeDirection direction = EdgeDirection.valueOf(splitData[5]);
        EdgeStyle style = EdgeStyle.valueOf(splitData[6]);
        int colorRGB = (int)Long.parseLong(splitData[7], 16);
        Color color = new Color(colorRGB);
        Edge edge = new Edge(nodes.get(node1Index), nodes.get(node2Index), weight, direction, style, color);
        edge.moveActualWeightPointTo(weightX, weightY);
        return edge;
    }

    /**
     * @param fontData The String containing Font data in the extended-format that should be parsed.
     * @return The Font resulting from parsing the given extended format String. If the system does not have the font a default Font is returned.
     */
    private static Font parseFontExtended(String fontData) {
        String[] splitData = fontData.split(" ", 3);
        int style = Integer.parseInt(splitData[0]);
        int size = Integer.parseInt(splitData[1]);
        String name = splitData[2];
        if (TextUtil.fontExists(name))
            return new Font(name, style, size);
        else
            return new Font(Font.DIALOG, style, size);
    }

    /**
     * @param nodeData The String containing Node data in the non-extended format that should be parsed.
     * @return The Node resulting from parsing the given non-extended format String.
     */
    private static Node parseNodeNonExtended(String nodeData) {
        // split() limit is set to 5 so we don't accidentally cut off the name of a node if it has a space.
        String[] splitData = nodeData.split(" ", 5);
        int x = Integer.parseInt(splitData[0]);
        int y = Integer.parseInt(splitData[1]);
        int width = Integer.parseInt(splitData[2]);
        int height = Integer.parseInt(splitData[3]);
        String name = splitData[4];
        return new Node(name, x, y, width, height);
    }

    /**
     * @param nodes The List of Nodes that the parsed Edge might connect to.
     * @param edgeData The String containing edge data in the non-extended format that should be parsed.
     * @return An Edge resulting from parsing the given non-extended format String.
     */
    private static Edge parseEdgeNonExtended(List<Node> nodes, String edgeData) {
        String[] splitData = edgeData.split(" ");
        int node1Index = Integer.parseInt(splitData[0]);
        int node2Index = Integer.parseInt(splitData[1]);
        return new Edge(nodes.get(node1Index), nodes.get(node2Index));
    }

    /**
     * This class contains only static methods and fields and should never be instantiated.
     */
    private GraphIO() {}

}