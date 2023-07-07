package viewer;


public class Viewer extends Thread {
    public String title = "Game tree viewer";
    private Display display = new Display(this);
    private Node tree;

    public Viewer(Node tree) {
        this.tree = tree;
    }

    @Override
    public void run() {
        new Window(display, title);
    }

    public Node getTree() {
        return tree;
    }

    public void setTree(Node tree) {
        this.tree = tree;
    }
}
