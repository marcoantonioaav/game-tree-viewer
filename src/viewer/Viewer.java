package viewer;

import viewer.panels.InfoPanel;
import viewer.panels.StateDisplay;
import viewer.panels.TreeDisplay;
import viewer.panels.TreeMinimap;

public class Viewer extends Thread {
    private String title = "Game tree viewer";
    private Window window = null;
    private TreeDisplay treeDisplay = new TreeDisplay(this);
    private StateDisplay stateDisplay = new StateDisplay(this);
    private InfoPanel infoPanel = new InfoPanel(this);
    private TreeMinimap treeMinimap = new TreeMinimap(this);

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
        window = new Window(this);
    }

    public void export(String absolutePath, String format) {
        Utils.saveImage(treeDisplay.getRoot().getTreeDisplayNode().getImage(), absolutePath, format);
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
        setSelected(tree);
        treeDisplay.setRoot(tree);
        window.updateColorBy();
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

    public TreeMinimap getTreeMinimap() {
        return treeMinimap;
    }

    public void setSelected(Node selected) {
        tree.getTreeDisplayNode().unselectTree();
        selected.getTreeDisplayNode().setSelected(true);
        this.selected = selected;
        treeDisplay.repaint();
        stateDisplay.repaint();
        infoPanel.update();
        treeMinimap.update();
    }

    public Node getSelected() {
        return selected;
    }
}
