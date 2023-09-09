package viewer.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import viewer.Node;
import viewer.Viewer;

public class TreeDisplay extends JPanel implements MouseListener {
    public static final int WIDTH = 720;
    public static final int HEIGHT = 720;

    private Viewer viewer;
    private Node root;

    public TreeDisplay(Viewer viewer) {
        this.viewer = viewer;
        this.root = viewer.getTree();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addMouseListener(this);
    }

    public void setRoot(Node selected) {
        if(root == null)
            root = selected;
        else {
            root.getTreeDisplayNode().setFakeRoot(false);
            root.getTreeDisplayNode().setRealRoot(null);
            if(selected.getFather() != null) {
                selected.getFather().getTreeDisplayNode().setFakeRoot(true);
                selected.getFather().getTreeDisplayNode().setRealRoot(selected);
                root = selected.getFather();
            }
            else 
                root = selected;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(root != null) {
            Graphics2D g2 = (Graphics2D)g;
            root.getTreeDisplayNode().drawTreeNavigation(g2, WIDTH, HEIGHT);
            setPreferredSize(new Dimension(root.getTreeDisplayNode().nodesToPixels(root.getTreeDisplayNode().getTreeWidth()), HEIGHT));
            revalidate();
            g2.dispose();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(root == null)
            return;
        Node selected = root.getTreeDisplayNode().getNodeByPosition(e.getX(), e.getY());
        if(selected != null) {  
            if(e.getClickCount() == 2)
                setRoot(selected);
            viewer.setSelected(selected);
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

    public Node getRealRoot() {
        if(root.getTreeDisplayNode().isFakeRoot())
            return root.getTreeDisplayNode().getRealRoot();
        return root;
    }

    public Node getRoot() {
        return root;
    }
}
