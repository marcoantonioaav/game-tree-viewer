package viewer;

import viewer.panels.InfoPanel;
import viewer.panels.StateDisplay;
import viewer.panels.TreeDisplay;

public class Viewer extends Thread {
    private String title = "Game tree viewer";
    private TreeDisplay treeDisplay = new TreeDisplay(this);
    private StateDisplay stateDisplay = new StateDisplay(this);
    private InfoPanel infoPanel = new InfoPanel(this);

    private Node tree = null;
    private Node selected = null;

    public Viewer() {

    }

    public Viewer(String vizName) {
        this.title = this.title + " - " + vizName;
    }

    public Viewer(Node tree) {
        setTree(tree);
    }

    @Override
    public void run() {
        new Window(this);
    }

    @Deprecated
    public void export() {
        //Utils.saveImage(tree.getImage(), "C:\\Users\\MarcoAntonio\\Documents\\GitHub\\game-tree-viewer\\", "test", "png");
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
        setSelected(tree);
        treeDisplay.setRoot(tree);
    }

    public String getTitle() {
        return title;
    }

    public TreeDisplay getTreeDisplay() {
        return treeDisplay;
    }

    public StateDisplay getStateDisplay() {
        return stateDisplay;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public void setSelected(Node selected) {
        tree.getTreeDisplayNode().unselectTree();
        selected.getTreeDisplayNode().setSelected(true);
        this.selected = selected;
        stateDisplay.repaint();
        infoPanel.update();
    }

    public Node getSelected() {
        return selected;
    }
}
