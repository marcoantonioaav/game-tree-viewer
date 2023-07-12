package viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.collections.ChunkSet;
import other.context.Context;
import other.state.container.ContainerState;

public class Node {
    private Node father = null;
    private List<Node> children = new ArrayList<>();

    private final int SIZE = 128;
    private final int MARGIN = SIZE/4;
    private final int PADDING = SIZE/16;
    private final int STATE_SIZE = SIZE/3;
    private final int LABEL_FONT_SIZE = SIZE/10;
    private final int EVALUATION_FONT_SIZE = SIZE/8;

    public static final int EMPTY = 0;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;

    private String label = "";
    private int[][] state = null;
    private int wins = 0;
    private int playouts = 0;
    private float evaluation = 0;

    private boolean showEvaluation = false;

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
        showEvaluation = true;
    }

    public BufferedImage getImage() {
        BufferedImage image = Utils.newWhiteImage(nodesToPixels(getTreeWidth()), nodesToPixels(getTreeHeight()));
        Graphics2D g2 = image.createGraphics();
        draw(g2);
        g2.dispose();
        return image;
    }

    public void draw(Graphics2D g2) {
        drawCircle(g2);
        drawEdge(g2);
        drawLabel(g2);
        drawState(g2);
        drawPlayouts(g2);
        drawEvaluation(g2);
        for(Node child : children)
            child.draw(g2);
    }

    private void drawLabel(Graphics2D g2) {
        if(label != "") {
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, LABEL_FONT_SIZE));
            int textWidth = (int)g2.getFontMetrics().getStringBounds(label, g2).getWidth();
            int textHeight = (int)g2.getFontMetrics().getStringBounds(label, g2).getHeight();
            g2.drawString(label, getX() + SIZE/2 - textWidth/2, getY() + SIZE/2 + textHeight/3 - STATE_SIZE/2 - PADDING);
        }      
    }

    private void drawPlayouts(Graphics2D g2) {
        if(playouts > 0) {
            String playoutsString = wins+"/"+playouts;
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, EVALUATION_FONT_SIZE));
            int textWidth = (int)g2.getFontMetrics().getStringBounds(playoutsString, g2).getWidth();
            int textHeight = (int)g2.getFontMetrics().getStringBounds(playoutsString, g2).getHeight();
            g2.drawString(playoutsString, getX() + SIZE/2 - textWidth/2, getY() + SIZE/2 + textHeight/3 + STATE_SIZE/2 + PADDING);
        }      
    }

    private void drawEvaluation(Graphics2D g2) {
        if(showEvaluation) {
            String evaluationString = evaluation+"";
            g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, EVALUATION_FONT_SIZE));
            int textWidth = (int)g2.getFontMetrics().getStringBounds(evaluationString, g2).getWidth();
            int textHeight = (int)g2.getFontMetrics().getStringBounds(evaluationString, g2).getHeight();
            g2.drawString(evaluationString, getX() + SIZE/2 - textWidth/2, getY() + SIZE/2 + textHeight/3 + STATE_SIZE/2 + PADDING);
        }      
    }

    private void drawState(Graphics2D g2) {
        if(state != null)
            for(int i = 0; i<state.length; i++)
                for(int j = 0; j<state[0].length; j++)
                    drawStateCell(g2, i, j);
    }

    private void drawStateCell(Graphics2D g2, int i, int j) {
        int stateX = getX() + SIZE/2 - STATE_SIZE/2;
        int stateY = getY() + SIZE/2 - STATE_SIZE/2;
        int cellSize = STATE_SIZE/state.length;
        g2.drawRect(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
        if(state[i][j] == PLAYER_1)
            g2.drawOval(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
        else if(state[i][j] == PLAYER_2)
            g2.fillOval(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
    }

    private void drawEdge(Graphics2D g2) {
        if(father != null)
            g2.drawLine(getX() + SIZE/2, getY(), father.getX() + SIZE/2, father.getY()+SIZE);
    }

    private void drawCircle(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.drawOval(getX(), getY(), SIZE, SIZE);
    }

    public int getX() {
        try {
            return (getTreeMinX()*2 + nodesToPixels(getTreeWidth()))/2 - SIZE/2;
        } catch (Exception e) {
            return 0;
        }
    }

    public int getY() {
        return nodesToPixels(getRootDistance()) + MARGIN/2;
    }

    public int getRootDistance() {
        if(isRoot())
            return 0;
        return 1 + father.getRootDistance();
    }

    public int getTreeMinX() throws Exception {
        if(isRoot())
            return 0;
        int minX = father.getTreeMinX();
        for(Node brother : father.getChildren()) {
            if(brother.equals(this))
                return minX;
            minX += brother.nodesToPixels(brother.getTreeWidth());
        }
        throw new Exception("Node not in fathers children list");
    }

    public int getRootHeight() {
        if(isRoot())
            return getTreeHeight();
        else
            return father.getRootHeight();
    }
 
    public int getTreeHeight() {
        if(children.isEmpty())
            return 1;
        int treeHeight = 1;
        for(Node child : children)
            treeHeight = (int) Math.max(treeHeight, 1+child.getTreeHeight());
        return treeHeight;
    }

    public int getTreeWidth() {
        if(children.isEmpty())
            return 1;
        int treeWidth = 0;
        for(Node child : children)
            treeWidth += child.getTreeWidth();
        return treeWidth;
    }

    public int nodesToPixels(int count) {
        return SIZE*count + MARGIN*count;
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
        showEvaluation = true;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setLabel(Context ludiiContext) {
        if(!ludiiContext.winners().isEmpty()) {
            int winner = ludiiContext.winners().get(0);
            this.label = "PLAYER " + winner + " WINS";
        }
        else if(ludiiContext.trial().over())
            this.label = "DRAW";
        else
            this.label = "";
    }
}
