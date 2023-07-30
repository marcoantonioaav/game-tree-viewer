package viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class Display extends JPanel implements MouseListener {
    public final int WIDTH = 720;
    public final int HEIGHT = 720;

    private Viewer viewer;
    private Node root;

    public Display(Viewer viewer) {
        this.viewer = viewer;
        this.root = viewer.getTree();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addMouseListener(this);
    }

    public void setRoot(Node root) {
        this.root = root;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(root != null) {
            Graphics2D g2 = (Graphics2D)g;
            root.draw(g2, WIDTH, HEIGHT);
            g2.dispose();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Node selected = root.getSelectedNode(e.getX(), e.getY());
        if(selected != null) {
            viewer.setSelected(selected);
            if(e.getClickCount() == 2) {
                root.setFakeRoot(false);
                root.setRealRoot(null);
                if(selected.getFather() != null) {
                    selected.getFather().setFakeRoot(true);
                    selected.getFather().setRealRoot(selected);
                    root = selected.getFather();
                }
                else 
                    root = selected;
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    } 
}
