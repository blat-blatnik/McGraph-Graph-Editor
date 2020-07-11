package controller;

import controller.menuitems.*;
import model.Graph;
import view.GraphPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
* @version 2.0
 *
 * This MenuBar houses all of the MenuItem controls responsible for manipulating the GraphModel.
 *
 * @see NewGraphMenuItem
 * @see OpenMenuItem
 * @see SaveMenuItem
 * @see SaveAsMenuItem
 * @see UndoMenuItem
 * @see RedoMenuItem
 * @see CutMenuItem
 * @see CopyMenuItem
 * @see PasteMenuItem
 * @see SelectAllMenuItem
 * @see DeselectMenuItem
 * @see AddMenu
 * @see DeleteMenuItem
 * @see CenterViewMenuItem
 * @see ZoomInMenuItem
 * @see ZoomOutMenuItem
 * @see BackgroundColorChooser
 * @see FindShortestPathMenuItem
 * @see ExploreGraphMenuItem
 * @see ColorGraphMenuItem
 * @see MarkStartMenuItem
 * @see MarkGoalMenuItem
 * @see ClearStartAndGoalMenuItem
 * @see InvertSelectionMenuItem
 */
public class MenuBar extends JMenuBar {

    /**
     * Constructs a new MenuBar for the given GraphModel and the given GraphPanel.
     *
     * @param graph The GraphModel that this menu bar will control.
     * @param panel The GraphPanel that this menu bar will control.
     */
    public MenuBar(Graph graph, GraphPanel panel) {
        super();

        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenu viewMenu = new JMenu("View");
        JMenu solveMenu = new JMenu("Solve");

        fileMenu.setMnemonic(KeyEvent.VK_F);
        editMenu.setMnemonic(KeyEvent.VK_E);
        viewMenu.setMnemonic(KeyEvent.VK_V);
        solveMenu.setMnemonic(KeyEvent.VK_S);

        fileMenu.add(new NewGraphMenuItem(graph));
        fileMenu.add(new OpenMenuItem(graph, panel));
        fileMenu.addSeparator();
        fileMenu.add(new SaveMenuItem(graph));
        fileMenu.add(new SaveAsMenuItem(graph));

        editMenu.add(new UndoMenuItem(graph));
        editMenu.add(new RedoMenuItem(graph));
        editMenu.addSeparator();
        editMenu.add(new CutMenuItem(graph));
        editMenu.add(new CopyMenuItem(graph));
        editMenu.add(new PasteMenuItem(graph));
        editMenu.addSeparator();
        editMenu.add(new SelectAllMenuItem(graph));
        editMenu.add(new DeselectMenuItem(graph));
        editMenu.add(new InvertSelectionMenuItem(graph));
        editMenu.addSeparator();
        editMenu.add(new AddMenu(graph));
        editMenu.add(new DeleteMenuItem(graph));

        viewMenu.add(new CenterViewMenuItem(panel));
        viewMenu.add(new ZoomInMenuItem(panel));
        viewMenu.add(new ZoomOutMenuItem(panel));
        viewMenu.addSeparator();
        viewMenu.add(new BackgroundColorChooser(panel));

        solveMenu.add(new FindShortestPathMenuItem(graph, panel));
        solveMenu.add(new ExploreGraphMenuItem(graph, panel));
        solveMenu.add(new ColorGraphMenuItem(graph, panel));
        solveMenu.addSeparator();
        solveMenu.add(new MarkStartMenuItem(graph));
        solveMenu.add(new MarkGoalMenuItem(graph));
        solveMenu.add(new ClearStartAndGoalMenuItem(graph));

        add(fileMenu);
        add(editMenu);
        add(viewMenu);
        add(solveMenu);
    }

}