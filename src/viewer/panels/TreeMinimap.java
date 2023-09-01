package viewer.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import viewer.Node;
import viewer.Viewer;

public class TreeMinimap extends JPanel {
    public static final int WIDTH = 240;
    public static final int HEIGHT = 240;

    private Viewer viewer;

    private final int NODE_MAX_SIZE = WIDTH - 2;
    private final int NODE_MIN_SIZE = 4;
    private int nodeSize = NODE_MIN_SIZE;

    private final int MAX_NODE_CAPACITY = 30000;

    public TreeMinimap(Viewer viewer) {
        this.viewer = viewer;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    public void update() {
        nodeSize = NODE_MIN_SIZE;
        while(nodesToPixels(viewer.getTree().getHeight()) <= (HEIGHT-20 - getMargin()) && nodeSize <= NODE_MAX_SIZE)
            nodeSize++;

        setPreferredSize(new Dimension(nodesToPixels(viewer.getTree().getWidth()), HEIGHT));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(viewer.getSelected() != null) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(Color.LIGHT_GRAY);
            try {
                g2.fillRect(0, 0, Math.max(nodesToPixels(viewer.getTree().getWidth()), WIDTH), Math.max(nodesToPixels(viewer.getTree().getHeight()), HEIGHT));
            } catch (Exception e) {
            }
            if(viewer.getTree().getTreeNodeCount() <= MAX_NODE_CAPACITY)
                drawTreeMinimap(g2, viewer.getTree());
            g2.dispose();
        }
    }

    private void drawTreeMinimap(Graphics2D g2, Node node) {
        int x = getX(node), y = getY(node);
        
        if(viewer.getTreeDisplay().getRealRoot().contains(node) && node.getTreeDisplayNode().isDrawn()) {
            g2.setColor(Color.WHITE);
            try {
                g2.fillRect(getTreeMinX(node), y, nodesToPixels(node.getWidth()), nodesToPixels(node.getTreeDisplayNode().getTreeHeight()));
            } catch (Exception e) {
            }
        }
        g2.setColor(Color.BLACK);
        
        if(node.getFather() != null)
            g2.drawLine(x + nodeSize/2, y, getX(node.getFather()) + nodeSize/2, getY(node.getFather())+nodeSize);
        Color nodeColor = node.getColorByScore();
        if(nodeColor == Color.WHITE)
            g2.drawOval(x, y, nodeSize, nodeSize);
        else {
            g2.setColor(nodeColor);
            g2.fillOval(x, y, nodeSize, nodeSize);
            g2.setColor(Color.BLACK);
        }
        
        for(Node child : node.getChildren())
             drawTreeMinimap(g2, child);
    }

    private int getX(Node node) {
        try {
            return (getTreeMinX(node)*2 + nodesToPixels(node.getWidth()))/2 - nodeSize/2;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getY(Node node) {
        return nodesToPixels(getRootDistance(node)) + getMargin()/2;
    }

    public int getTreeMinX(Node node) throws Exception {
        if(node.isRoot())
            return 0;
        int minX = getTreeMinX(node.getFather());
        for(Node brother : node.getFather().getChildren()) {
            if(brother.equals(node))
                return minX;
            minX += nodesToPixels(brother.getWidth());
        }
        throw new Exception("Node not in fathers node.getChildren() list");
    }

    public int getRootDistance(Node node) {
        if(node.isRoot())
            return 0;
        return 1 + getRootDistance(node.getFather());
    }

    public int nodesToPixels(int count) {
        return nodeSize*count + getMargin()*count;
    }

    public int getMargin() {
        return nodeSize/4;
    }
}
