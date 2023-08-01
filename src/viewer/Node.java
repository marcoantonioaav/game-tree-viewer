package viewer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import main.collections.ChunkSet;
import other.context.Context;
import other.state.container.ContainerState;
import viewer.panels.TreeDisplayNode;

public class Node {
    private Node father = null;
    private List<Node> children = new ArrayList<>();
    
    public static final int EMPTY = 0;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;

    private String label = "";
    private int[][] state = null;
    private int wins = 0;
    private int playouts = 0;
    private float evaluation = Float.NaN;

    private TreeDisplayNode treeDisplayNode = new TreeDisplayNode(this);

    public Node() { }

    public Node(String label) {
        this.label = label;
    }

    public Node(int[][] state) {
        setState(state);
    }

    public Node(Context ludiiContext) {
        setState(ludiiContext);
        setLabel(ludiiContext);
    }

    public Node(Context ludiiContext, int wins, int playouts) {
        setState(ludiiContext);
        setLabel(ludiiContext);
        this.wins = wins;
        this.playouts = playouts;
    }

    public Node(Context ludiiContext, int evaluation) {
        setState(ludiiContext);
        setLabel(ludiiContext);
        this.evaluation = evaluation;
    }

    /*public BufferedImage getImage() {
        BufferedImage image = Utils.newWhiteImage(nodesToPixels(getTreeWidth()), nodesToPixels(getTreeHeight()));
        Graphics2D g2 = image.createGraphics();
        //drawTreeNavigation(g2);
        g2.dispose();
        return image;
    } */   

    public boolean contains(Node node) {
        if(equals(node))
            return true;
        for(Node child : children)
            if(child.contains(node))
                return true;
        return false;
    }

    public Color getColorByScore() {
        float score;
        if(isUsingEvaluation()) {
            score = getEvaluation();
            score = normalizeTo0To1(score, -1f, 1f);
        }
        else if(getPlayouts() > 0)
            score = (float)getWins()/(float)getPlayouts();
        else return Color.WHITE;

        int r, g;
        if(score >= 0.5f) {
            r = 255 - (int)(255*normalizeTo0To1(score, 0.5f, 1f));
            g = 255;
        }
        else {
            r = 255;
            g = (int)(255*normalizeTo0To1(score, 0f, 0.5f));
        }
        return new Color(r, g, 0);
    }  

    private float normalizeTo0To1(float score, float min, float max) {
        return (score - min) / (max - min);
    }

    public int getHeight() {
        if(getChildren().isEmpty())
            return 1;
        int treeHeight = 1;
        for(Node child : getChildren())
            treeHeight = (int) Math.max(treeHeight, 1+child.getHeight());
        return treeHeight;
    }

    public int getWidth() {
        if(getChildren().isEmpty())
            return 1;
        int treeWidth = 0;
        for(Node child :getChildren())
            treeWidth += child.getWidth();
        return treeWidth;
    }

    public float getBranchingFactor() {
        return (float)getTreeChildrenCountSum()/(float)Math.max(getTreeNonTerminalCount(), 1);
    }

    public int getTreeChildrenCountSum() {
        if(children.isEmpty())
            return 0;
        int childrenCountSum = children.size();
        for(Node child : children)
            if(!child.getChildren().isEmpty())
                childrenCountSum += child.getTreeChildrenCountSum();
        return childrenCountSum;
    }

    public int getTreeNonTerminalCount() {
        if(children.isEmpty())
            return 0;
        int count = 1;
        for(Node child : children)
            count += child.getTreeNonTerminalCount();
        return count;
    }

    public int getTreeNodeCount() {
        if(children.isEmpty())
            return 1;
        int count = 1;
        for(Node child : children)
            count += child.getTreeNodeCount();
        return count;
    }

    public Node getRoot() {
        if(isRoot())
            return this;
        return father.getRoot();
    }

    public boolean isRoot() {
        return father == null;
    }

    public void addChild(Node child) {
        children.add(child);
        if(child.getFather() == null)
            child.setFather(this);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
        if(father != null)
            if(!this.father.getChildren().contains(this))
                this.father.addChild(this);
    }

    public void setState(int[][] state) {
        this.state = state;
    }

    public void setState(Context ludiiContext) { 
        ContainerState containerState = ludiiContext.state().containerStates()[0];
        ChunkSet pieces, emptyPieces;
        if(ludiiContext.game().isVertexGame()) {
            pieces = containerState.cloneWhoVertex();
            emptyPieces = containerState.emptyChunkSetVertex();
        }
        else {
            pieces = containerState.cloneWhoCell();
            emptyPieces = containerState.emptyChunkSetCell();
        }
        List<Integer> cells = new ArrayList<>();
        for(int i=0; i < pieces.numChunks(); i++) {
            int piece = pieces.getChunk(i);
            if (emptyPieces.getChunk(i) != 0 || piece != 0)
                cells.add(piece);
        }
        int boardSize = (int)Math.sqrt(cells.size());
        int[][] state = new int[boardSize][boardSize];
        for(int i = 0; i<boardSize; i++) {
            for(int j = 0; j<boardSize; j++) {
                state[i][j] = cells.get(boardSize*((boardSize-1) - j) +  i);
            }
        }
        this.state = state;
    }

    public void setPlayouts(int wins, int playouts) {
        this.wins = wins;
        this.playouts = playouts;
    }

    public void setEvaluation(float evaluation) {
        this.evaluation = evaluation;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabel(Context ludiiContext) {
        if(!ludiiContext.winners().isEmpty()) {
            int winner = ludiiContext.winners().get(0);
            this.label = "Player " + winner + " victory state";
        }
        else if(ludiiContext.trial().over())
            this.label = "Draw state";
        else
            this.label = "Non-terminal state";
    }

    public String getLabel() {
        return label;
    }

    public float getEvaluation() {
        return evaluation;
    }

    public boolean isUsingEvaluation() {
        return evaluation != Float.NaN;
    }

    public int getPlayouts() {
        return playouts;
    }

    public int getWins() {
        return wins;
    }

    public int[][] getState() {
        return state;
    }

    public TreeDisplayNode getTreeDisplayNode() {
        return treeDisplayNode;
    }
}
