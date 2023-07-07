package viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class Display extends JPanel implements ActionListener {
    public final int WIDTH = 1280;
    public final int HEIGHT = 720;

    private Viewer viewer;

    public Display(Viewer viewer) {
        this.viewer = viewer;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        viewer.getTree().draw(g2);
        g2.dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    } 
}
