package viewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private Node father = null;
    private List<Node> children = new ArrayList<>();

    private int SIZE = 128;
    private int MARGIN = 32;

    private String label = "";
    //private int[][] state;
    //private int evaluation = 0;

    public Node() {

    }

    public Node(String label) {
        this.label = label;
    }

    public void draw(Graphics2D g2) {
        drawCircle(g2);
        g2.drawString(label, getX() + SIZE/2, getY() + SIZE/2);
        if(father != null)
            g2.drawLine(getX() + SIZE/2, getY(), father.getX() + SIZE/2, father.getY()+SIZE);
        for(Node child : children)
            child.draw(g2);
    }

    private void drawCircle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawOval(getX(), getY(), SIZE, SIZE);
    }

    public int getX() {
        try {
            return (getTreeMinX()*2 + getTreePixelWidth())/2 - SIZE/2;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getY() {
        return getRootPixelHeight() - getTreePixelHeight() + MARGIN/2;
    }

    public int getTreeMinX() throws Exception {
        if(father == null)
            return 0;
        int minX = father.getTreeMinX();
        for(Node brother : father.getChildren()) {
            if(brother.equals(this))
                return minX;
            minX += brother.getTreePixelWidth();
        }
        throw new Exception("Node not in fathers children list");
    }

    public int getRootPixelHeight() {
        if(father == null)
            return getTreePixelHeight();
        else
            return father.getRootPixelHeight();
    }
 
    public int getTreePixelHeight() {
        int treeHeight = getTreeHeight();
        return SIZE*treeHeight + MARGIN*treeHeight;
    }

    public int getTreeHeight() {
        if(children.isEmpty())
            return 1;
        int treeHeight = 1;
        for(Node child : children)
            treeHeight = (int) Math.max(treeHeight, 1+child.getTreeHeight());
        return treeHeight;
    }

    public int getTreePixelWidth() {
        int treeWidth = getTreeWidth();
        return SIZE*treeWidth + MARGIN*treeWidth;
    }

    public int getTreeWidth() {
        if(children.isEmpty())
            return 1;
        int treeWidth = 0;
        for(Node child : children)
            treeWidth += child.getTreeWidth();
        return treeWidth;
    }

    public void addChild(Node child) {
        children.add(child);
        child.setFather(this);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setFather(Node father) {
        this.father = father;
    }
}
