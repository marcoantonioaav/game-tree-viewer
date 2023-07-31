package viewer;

public class Viewer extends Thread {
    private String title = "Game tree viewer";
    private Display display = new Display(this);
    private StateViewer stateViewer = new StateViewer(this);

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

    public void export() {
        Utils.saveImage(tree.getImage(), "C:\\Users\\MarcoAntonio\\Documents\\GitHub\\game-tree-viewer\\", "test", "png");
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
        setSelected(tree);
        display.setRoot(tree);
    }

    public String getTitle() {
        return title;
    }

    public Display getDisplay() {
        return display;
    }

    public StateViewer getStateViewer() {
        return stateViewer;
    }

    public void setSelected(Node selected) {
        tree.unselectTree();
        selected.setSelected(true);
        this.selected = selected;
        stateViewer.repaint();
    }

    public Node getSelected() {
        return selected;
    }
}
