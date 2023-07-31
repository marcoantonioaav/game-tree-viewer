package viewer.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import viewer.Node;
import viewer.Viewer;

public class StateDisplay extends JPanel {
    public final int SIZE = 256;

    private Viewer viewer;

    public StateDisplay(Viewer viewer) {
        this.viewer = viewer;
        this.setPreferredSize(new Dimension(SIZE, SIZE));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(viewer.getSelected() != null) {
            Graphics2D g2 = (Graphics2D)g;
            drawState(g2);
            g2.dispose();
        }
    }

    private void drawState(Graphics2D g2) {
        if(viewer.getSelected().getState() != null)
            for(int i = 0; i<viewer.getSelected().getState().length; i++)
                for(int j = 0; j<viewer.getSelected().getState()[0].length; j++)
                    drawStateCell(g2, i, j);
    }

    private void drawStateCell(Graphics2D g2, int i, int j) {
        int stateSize = (6*SIZE)/8;
        int stateX = (SIZE - stateSize)/2;
        int stateY = (SIZE - stateSize)/2;
        int cellSize = stateSize/viewer.getSelected().getState().length;
        int cellX = stateX+(cellSize*i);
        int cellY = stateY+(cellSize*j);
        g2.drawRect(cellX, cellY, cellSize, cellSize);
        if(viewer.getSelected().getState()[i][j] == Node.PLAYER_1)
            g2.drawOval(cellX+2, cellY+2, cellSize-4, cellSize-4);
        else if(viewer.getSelected().getState()[i][j] == Node.PLAYER_2)
            g2.fillOval(cellX+2, cellY+2, cellSize-4, cellSize-4);
    }
}
