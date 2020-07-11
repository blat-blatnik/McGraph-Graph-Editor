package model;

import javax.swing.undo.UndoManager;

/**
 * @author Boris
 * @version 1.0
 *
 * This UndoManager is used to keep track of changes in the GraphModel. It has a method that actually allows us to get
 * a index into which UndoableEdit will be undone next - the normal UndoManager doesn't have this functionality for
 * whatever reason.
 *
 * @see UndoManager
 */
public class GraphUndoManager extends UndoManager {

    /**
     * @return An index representing which UndoableEdit will be undone next, or -1 if no UndoableEdits can be undone.
     */
    public int getUndoPointer() {
        return edits.indexOf(editToBeUndone());
    }

}