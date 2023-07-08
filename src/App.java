import java.awt.image.BufferedImage;

import agent.Minimax;
import agent.SequentialHalving;
import app.StartDesktopApp;
import manager.ai.AIRegistry;
import viewer.Node;
import viewer.Viewer; 
import viewer.Utils;

public class App {
    public static void main(String[] args) {
        startLudii();
    }

    public static void startLudii() {
        Viewer viewer = new Viewer();
        AIRegistry.registerAI("SH", () -> {return new SequentialHalving(viewer);}, (game) -> {return true;});
        AIRegistry.registerAI("MM", () -> {return new Minimax(viewer);}, (game) -> {return true;});
        StartDesktopApp.main(new String[0]);
        viewer.start();
    }

    public static Node getExampleTree() {
        Node root = new Node("r");
        Node node1 = new Node("1");
        Node node2 = new Node("2");
        Node node3 = new Node("3");
        Node node4 = new Node("4");
        Node node5 = new Node("5");
        root.addChild(node1);
        root.addChild(node2);
        node1.addChild(node3);
        node1.addChild(node4);
        node2.addChild(node5);
        return root;
    }

    public static void exportNode(Node node) {
        BufferedImage image = Utils.newWhiteImage(node.nodesToPixels(node.getTreeWidth()), node.nodesToPixels(node.getTreeHeight()));
        Utils.drawNodeOnImage(node, image);
        Utils.saveImage(image, "C:\\Users\\MarcoAntonio\\Documents\\GitHub\\game-tree-viewer\\", "test", "png");
    }

    public static void startViewer(Node node) {
        new Viewer(node).start();
    }
}