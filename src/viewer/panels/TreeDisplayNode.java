package viewer.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import viewer.Node;

public class TreeDisplayNode {
    public final int NODE_MAX_SIZE = 128;
    public final int NODE_MIN_SIZE = NODE_MAX_SIZE/4;

    public int size = NODE_MAX_SIZE;
    public int maxWidthPx = 0;
    public boolean selected = false;
    public boolean childrenActive = true;
    public boolean fakeRoot = false;
    public Node realRoot = null;
    public boolean drawn = false;

    private Node node;

    public TreeDisplayNode(Node node) {
        this.node = node;
    }

    public void drawTreeNavigation(Graphics2D g2, int maxWidthPx, int maxHeightPx) {
        this.maxWidthPx = maxWidthPx;
        setTreeDrawn(false);
        setTreeChildrenActive(true);
        setSize(NODE_MIN_SIZE);
        int maxDepth = getTreeHeight();
        while((nodesToPixels(getTreeHeight()) > maxHeightPx || nodesToPixels(getTreeWidth()) > maxWidthPx) && maxDepth >= 1) {
            maxDepth--;
            pruneByDepth(maxDepth);
        }
        int smallSize = NODE_MIN_SIZE;
        while(nodesToPixels(getTreeHeight()) <= (maxHeightPx - getMargin()) && nodesToPixels(getTreeWidth()) <= (maxWidthPx - getMargin()) && smallSize <= NODE_MAX_SIZE) {
            smallSize++;
            setSize(smallSize);
        }
        drawTreeNavigation(g2);
    }

    private void drawTreeNavigation(Graphics2D g2) {
        drawn = true;
        if(fakeRoot) {
            drawUpArrow(g2);
            realRoot.getTreeDisplayNode().drawTreeNavigation(g2);
            return;
        }

        drawCircle(g2);
        drawEdge(g2);
        if(node.isUsingEvaluation())   
            drawTextOnCenter(g2, String.format("%.1f", node.getEvaluation()));
        else if(node.getPlayouts() > 0)
            drawTextOnCenter(g2, node.getWins()+"/"+node.getPlayouts());
        
        for(Node child : node.getChildren())
            if(childrenActive)
                child.getTreeDisplayNode().drawTreeNavigation(g2);
            else
                drawDots(g2);
    }

    private void drawDots(Graphics2D g2) {
        g2.drawLine(getX() + size/2, getY() + size, getX() + size/2, getY() + size + getMargin());
        g2.fillOval(getX() + size/2 - size/32, getY() + size + getMargin() + size/2 - size/8, size/16, size/16);
        g2.fillOval(getX() + size/2 - size/32, getY() + size + getMargin() + size/2, size/16, size/16);
        g2.fillOval(getX() + size/2 - size/32, getY() + size + getMargin() + size/2 + size/8, size/16, size/16);
    }

    private void drawUpArrow(Graphics2D g2) {
        if(selected)
            g2.setColor(Color.BLUE);
        g2.fillPolygon(
            new int[]{
                getX() + size/2 - size/8,
                getX() + size/2,
                getX() + size/2 + size/8,
            }, 
            new int[]{
                getY() + size/2 + size/8,
                getY() + size/2 - size/8,
                getY() + size/2 + size/8,
            }, 
            3);
        g2.fillOval(getX() + size/2 - size/32, getY() + size/2 + size/8, size/16, size/16);
        g2.fillOval(getX() + size/2 - size/32, getY() + size/2 + size/5, size/16, size/16);
        g2.setColor(Color.BLACK);
    }

    private void drawTextOnCenter(Graphics2D g2, String text) {
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, getFontSize()));
        int textWidth = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int textHeight = (int)g2.getFontMetrics().getStringBounds(text, g2).getHeight();
        g2.drawString(text, getX() + size/2 - textWidth/2, getY() + size/2 + textHeight/3);
    } 

    private void drawEdge(Graphics2D g2) {
        if(node.getFather() != null)
            g2.drawLine(getX() + size/2, getY(), node.getFather().getTreeDisplayNode().getX() + size/2, node.getFather().getTreeDisplayNode().getY()+size);
    }

    private void drawCircle(Graphics2D g2) {
        if(selected)
            g2.setColor(Color.BLUE);
        else
            g2.setColor(Color.BLACK);
        g2.fillOval(getX(), getY(), size, size);
        
        setColorByScore(g2);
        int circleThickness = Math.min(size/16, 5);
        if(selected)
            g2.fillOval(getX()+circleThickness*2, getY()+circleThickness*2, size-(circleThickness*4), size-(circleThickness*4));
        else
            g2.fillOval(getX()+circleThickness, getY()+circleThickness, size-(circleThickness*2), size-(circleThickness*2));
        g2.setColor(Color.BLACK);
    }

    private void setColorByScore(Graphics2D g2) {
        float score, min;
        if(node.isUsingEvaluation()) {
            score = node.getEvaluation();
            min = -1f;
        }
        else if(node.getPlayouts() > 0) {
            score = (float)node.getWins()/(float)node.getPlayouts();
            min = 0f;
        }
        else return;

        if(score == 1f)
            g2.setColor(Color.GREEN);
        else if(score == min)
            g2.setColor(Color.RED);
        else
            g2.setColor(Color.ORANGE);
    }  

    private void setTreeDrawn(boolean drawn) {
        this.drawn = drawn;
        for(Node child : node.getChildren())
            child.getTreeDisplayNode().setTreeDrawn(drawn);
    }

    public void setTreeChildrenActive(boolean active) {
        childrenActive = active;
        for(Node child : node.getChildren())
            child.getTreeDisplayNode().setTreeChildrenActive(active);
    }

    private void pruneByDepth(int maxDepth) {
        if(maxDepth > 0)
            childrenActive = true;
        else
            childrenActive = false;
        for(Node child : node.getChildren())
            child.getTreeDisplayNode().pruneByDepth(maxDepth-1);
    }

    public int getX() {
        try {
            return (getTreeMinX()*2 + nodesToPixels(getTreeWidth()))/2 - size/2;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getY() {
        return nodesToPixels(getRootDistance()) + getMargin()/2;
    }

    public int getRootDistance() {
        if(node.isRoot() || fakeRoot)
            return 0;
        return 1 + node.getFather().getTreeDisplayNode().getRootDistance();
    }

    public int getTreeMinX() throws Exception {
        if(node.isRoot() || fakeRoot)
            return (maxWidthPx - nodesToPixels(getTreeWidth()))/2;
        if(node.getFather().getTreeDisplayNode().isFakeRoot())
            return node.getFather().getTreeDisplayNode().getTreeMinX();
        int minX = node.getFather().getTreeDisplayNode().getTreeMinX();
        for(Node brother : node.getFather().getChildren()) {
            if(brother.equals(this.node))
                return minX;
            minX += brother.getTreeDisplayNode().nodesToPixels(brother.getTreeDisplayNode().getTreeWidth());
        }
        throw new Exception("Node not in fathers node.getChildren() list");
    }

    public int getRootHeight() {
        if(node.isRoot())
            return getTreeHeight();
        else
            return node.getFather().getTreeDisplayNode().getRootHeight();
    }

    public int getTreeHeight() {
        if(!childrenActive)
            return 1;
        if(node.getChildren().isEmpty())
            return 1;
        if(fakeRoot)
            return 1+realRoot.getTreeDisplayNode().getTreeHeight();
        int treeHeight = 1;
        for(Node child : node.getChildren())
            treeHeight = (int) Math.max(treeHeight, 1+child.getTreeDisplayNode().getTreeHeight());
        return treeHeight;
    }

    public int getTreeWidth() {
        if(!childrenActive)
            return 1;
        if(node.getChildren().isEmpty())
            return 1;
        if(fakeRoot)
            return realRoot.getTreeDisplayNode().getTreeWidth();
        int treeWidth = 0;
        for(Node child : node.getChildren())
            treeWidth += child.getTreeDisplayNode().getTreeWidth();
        return treeWidth;
    }

    public int nodesToPixels(int count) {
        return size*count + getMargin()*count;
    }

    public int getMargin() {
        return size/4;
    }

    public int getFontSize() {
        return size/4;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChildrenActive() {
        return childrenActive;
    }

    public void setSize(int size) {
        this.size = size;
        for(Node child : node.getChildren())
            child.getTreeDisplayNode().setSize(size);
    }

    public boolean isFakeRoot() {
        return fakeRoot;
    }

    public void unselectTree() {
        selected = false;
        for(Node child : node.getChildren())
            child.getTreeDisplayNode().unselectTree();
    }

    public Node getNodeByPosition(int x, int y) {
        if(isOnLimits(x, y))
            return node;
        for(Node child : node.getChildren()) {
            Node n = child.getTreeDisplayNode().getNodeByPosition(x, y);
            if(n != null)
                return n;
        }
        return null;
    }

    public boolean isOnLimits(int x, int y) {
        return drawn && x >= getX() && x < getX() + size && y >= getY() && y < getY() + size;
    }

    public void setFakeRoot(boolean fakeRoot) {
        this.fakeRoot = fakeRoot;
    }

    public void setRealRoot(Node realRoot) {
        this.realRoot = realRoot;
    }

}
