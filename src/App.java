import java.awt.image.BufferedImage;

import agent.ExampleAI;
import app.StartDesktopApp;
import manager.ai.AIRegistry;
import viewer.Node;
import viewer.Viewer; 
import viewer.Utils;

public class App {
    public static void main(String[] args) {
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
        startViewer(root);
    }

    public static void exportNode(Node node) {
        BufferedImage image = Utils.newWhiteImage(node.getTreePixelWidth(), node.getTreePixelHeight());
        Utils.drawNodeOnImage(node, image);
        Utils.saveImage(image, "C:\\Users\\MarcoAntonio\\Documents\\GitHub\\game-tree-viewer\\", "test", "png");
    }

    public static void startViewer(Node node) {
        new Viewer(node).start();
    }

    public static void startLudii() {
        AIRegistry.registerAI("Example", () -> {return new ExampleAI();}, (game) -> {return true;});
        StartDesktopApp.main(new String[0]);
    }
}