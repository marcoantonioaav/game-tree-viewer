package viewer;

public class Viewer extends Thread {
    private String title = "Game tree viewer";
    private Display display = new Display(this);
    private Node tree = null;

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
        tree.setSelected(true);
        this.tree = tree;
    }

    public String getTitle() {
        return title;
    }

    public Display getDisplay() {
        return display;
    }
}
