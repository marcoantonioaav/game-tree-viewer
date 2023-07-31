package viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

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
            viewer.getSelected().drawState(g2, SIZE);
            g2.dispose();
        }
    }
}
