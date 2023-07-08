package viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Display extends JPanel implements ActionListener {
    public final int WIDTH = 1280;
    public final int HEIGHT = 720;

    private Viewer viewer;

    private Timer timer = new Timer(75, this);

    public Display(Viewer viewer) {
        this.viewer = viewer;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(viewer.getTree() != null) {
            Graphics2D g2 = (Graphics2D)g;
            viewer.getTree().draw(g2);
            g2.dispose();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    } 
}
