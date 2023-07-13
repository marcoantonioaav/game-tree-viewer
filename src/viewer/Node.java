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

    private final int NORMAL_SIZE = 128;
    private final int PADDING = NORMAL_SIZE/16;
    private final int STATE_SIZE = NORMAL_SIZE/3;
    private final int LABEL_FONT_SIZE = NORMAL_SIZE/10;
    private final int EVALUATION_FONT_SIZE = NORMAL_SIZE/8;
    
    private final int MIN_SMALL_SIZE = NORMAL_SIZE/4;

    public static final int EMPTY = 0;
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;

    private int size = NORMAL_SIZE;

    private String label = "";
    private int[][] state = null;
    private int wins = 0;
    private int playouts = 0;
    private float evaluation = 0;

    private int maxWidthPx = 0;

    private boolean showEvaluation = false;
    private boolean selected = false;
    private boolean childrenActive = true;

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
        setSize(NORMAL_SIZE);
        BufferedImage image = Utils.newWhiteImage(nodesToPixels(getTreeWidth()), nodesToPixels(getTreeHeight()));
        Graphics2D g2 = image.createGraphics();
        drawDetailed(g2);
        g2.dispose();
        return image;
    }

    public void draw(Graphics2D g2, int maxWidthPx, int maxHeightPx) {
        this.maxWidthPx = maxWidthPx;
        setSize(NORMAL_SIZE);
        setTreeChildrenActive(true);
        if(nodesToPixels(getTreeHeight()) <= maxHeightPx && nodesToPixels(getTreeWidth()) <= maxWidthPx)
            drawDetailed(g2);
        else {
            setSize(MIN_SMALL_SIZE);
            int maxDepth = getTreeHeight();
            while((nodesToPixels(getTreeHeight()) > maxHeightPx || nodesToPixels(getTreeWidth()) > maxWidthPx) && maxDepth >= 1) {
                maxDepth--;
                pruneByDepth(maxDepth);
            }
            int smallSize = MIN_SMALL_SIZE;
            while(nodesToPixels(getTreeHeight()) <= (maxHeightPx - getMargin()) && nodesToPixels(getTreeWidth()) <= (maxWidthPx - getMargin()) && smallSize < NORMAL_SIZE) {
                smallSize++;
                setSize(smallSize);
            }
            drawSimple(g2);
        }
    }

    public void setTreeChildrenActive(boolean active) {
        this.childrenActive = active;
        for(Node child : children)
            child.setTreeChildrenActive(active);
    }

    private void pruneByDepth(int maxDepth) {
        if(maxDepth > 0)
            childrenActive = true;
        else
            childrenActive = false;
        for(Node child : children)
            child.pruneByDepth(maxDepth-1);
    }

    private void drawSimple(Graphics2D g2) {
        drawCircle(g2);
        drawEdge(g2);
        if(showEvaluation)   
            drawTextOnCenter(g2, evaluation+"");
        else if(playouts > 0)
            drawTextOnCenter(g2, wins+"/"+playouts);
        
        for(Node child : children)
            if(childrenActive)
                child.drawSimple(g2);
            else
                drawDots(g2);
    }

    private void drawDots(Graphics2D g2) {
        g2.drawLine(getX() + size/2, getY() + size, getX() + size/2, getY() + size + getMargin());
        g2.fillOval(getX() + size/2, getY() + size + getMargin() + size/2 - 8, 4, 4);
        g2.fillOval(getX() + size/2, getY() + size + getMargin() + size/2, 4, 4);
        g2.fillOval(getX() + size/2, getY() + size + getMargin() + size/2 + 8, 4, 4);
    }

    private void drawDetailed(Graphics2D g2) {
        drawCircle(g2);
        drawEdge(g2);
        if(label != "")
            drawLabel(g2);
        drawState(g2);
        if(showEvaluation)   
            drawScore(g2, evaluation+"");
        else if(playouts > 0)
            drawScore(g2, wins+"/"+playouts);
        for(Node child : children)
            child.drawDetailed(g2);
    }

    private void drawLabel(Graphics2D g2) {
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, LABEL_FONT_SIZE));
        int textWidth = (int)g2.getFontMetrics().getStringBounds(label, g2).getWidth();
        int textHeight = (int)g2.getFontMetrics().getStringBounds(label, g2).getHeight();
        g2.drawString(label, getX() + size/2 - textWidth/2, getY() + size/2 + textHeight/3 - STATE_SIZE/2 - PADDING);  
    }

    private void drawScore(Graphics2D g2, String score) {
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, EVALUATION_FONT_SIZE));
        int textWidth = (int)g2.getFontMetrics().getStringBounds(score, g2).getWidth();
        int textHeight = (int)g2.getFontMetrics().getStringBounds(score, g2).getHeight();
        g2.drawString(score, getX() + size/2 - textWidth/2, getY() + size/2 + textHeight/3 + STATE_SIZE/2 + PADDING);
    }

    private void drawTextOnCenter(Graphics2D g2, String text) {
        g2.setFont(new Font(Font.MONOSPACED, Font.BOLD, getSimpleFontSize()));
        int textWidth = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        int textHeight = (int)g2.getFontMetrics().getStringBounds(text, g2).getHeight();
        g2.drawString(text, getX() + size/2 - textWidth/2, getY() + size/2 + textHeight/3);
    } 

    private void drawState(Graphics2D g2) {
        if(state != null)
            for(int i = 0; i<state.length; i++)
                for(int j = 0; j<state[0].length; j++)
                    drawStateCell(g2, i, j);
    }

    private void drawStateCell(Graphics2D g2, int i, int j) {
        int stateX = getX() + size/2 - STATE_SIZE/2;
        int stateY = getY() + size/2 - STATE_SIZE/2;
        int cellSize = STATE_SIZE/state.length;
        g2.drawRect(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
        if(state[i][j] == PLAYER_1)
            g2.drawOval(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
        else if(state[i][j] == PLAYER_2)
            g2.fillOval(stateX+(cellSize*i), stateY+(cellSize*j), cellSize, cellSize);
    }

    private void drawEdge(Graphics2D g2) {
        if(father != null)
            g2.drawLine(getX() + size/2, getY(), father.getX() + size/2, father.getY()+size);
    }

    private void drawCircle(Graphics2D g2) {
        if(selected)
            g2.setColor(Color.BLUE);
        else
            g2.setColor(Color.BLACK);
        g2.fillOval(getX(), getY(), size, size);
        setColorByScore(g2);
        g2.fillOval(getX()+2, getY()+2, size-4, size-4);
        g2.setColor(Color.WHITE);
        g2.fillOval(getX()+4, getY()+4, size-8, size-8);
        g2.setColor(Color.BLACK);
    }

    private void setColorByScore(Graphics2D g2) {
        float score, min;
        if(showEvaluation) {
            score = evaluation;
            min = -1f;
        }
        else if(playouts > 0) {
            score = (float)wins/(float)playouts;
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
        if(isRoot())
            return 0;
        return 1 + father.getRootDistance();
    }

    public int getTreeMinX() throws Exception {
        if(isRoot())
            return (maxWidthPx - nodesToPixels(getTreeWidth()))/2;
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
        if(!childrenActive)
            return 1;
        if(children.isEmpty())
            return 1;
        int treeHeight = 1;
        for(Node child : children)
            treeHeight = (int) Math.max(treeHeight, 1+child.getTreeHeight());
        return treeHeight;
    }

    public int getTreeWidth() {
        if(!childrenActive)
            return 1;
        if(children.isEmpty())
            return 1;
        int treeWidth = 0;
        for(Node child : children)
            treeWidth += child.getTreeWidth();
        return treeWidth;
    }

    public int nodesToPixels(int count) {
        return size*count + getMargin()*count;
    }

    public Node getRoot() {
        if(isRoot())
            return this;
        return father.getRoot();
    }

    public boolean isRoot() {
        return father == null;
    }

    public int getMargin() {
        return size/4;
    }

    public int getSimpleFontSize() {
        return size/3;
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
        for(Node child : children)
            child.setSize(size);
    }
}
