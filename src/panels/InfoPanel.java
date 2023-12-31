package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import viewer.Viewer;

public class InfoPanel extends JPanel {
    public final int WIDTH = 240;
    public final int HEIGHT = 240;

    private Viewer viewer;

    private JLabel state = new JLabel();
    private JLabel evaluation = new JLabel();
    private JLabel playouts = new JLabel();
    private JLabel branching = new JLabel();
    private JLabel nodeCount = new JLabel();
    private JLabel branchingMean = new JLabel();
    private JLabel dimensions = new JLabel();

    private final Font TITLE_FONT = new Font("Verdana", Font.BOLD, 18);
    private JLabel node = new JLabel();
    private JLabel tree = new JLabel();

    public InfoPanel(Viewer viewer) {
        this.viewer = viewer;
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.WHITE);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        setLayout(new FlowLayout(FlowLayout.CENTER, 60, 5));
        
        node.setFont(TITLE_FONT);
        add(node);
        
        add(state);
        add(evaluation);
        add(playouts);
        add(branching);

        tree.setFont(TITLE_FONT);
        add(tree);

        add(nodeCount);
        add(branchingMean);
        add(dimensions);

        addToolTips();
    }

    public void update() {
        if(viewer.getSelected() != null) {
            state.setText(viewer.getSelected().getLabel());
            if(viewer.getSelected().isUsingEvaluation())
                evaluation.setText("Evaluation: "+String.format("%.3f", viewer.getSelected().getEvaluation()));
            else
                evaluation.setText("Evaluation: None");
            playouts.setText("Playouts (Q/N): "+viewer.getSelected().getPlayoutsQ()+"/"+viewer.getSelected().getPlayoutsN());
            branching.setText("Branching factor: "+viewer.getSelected().getChildren().size());
            nodeCount.setText("Expanded nodes: "+viewer.getSelected().getTreeNodeCount());
            branchingMean.setText("Branching factor (mean): "+String.format("%.2f", viewer.getSelected().getBranchingFactor()));
            dimensions.setText("Height: "+viewer.getSelected().getHeight()+"   Width: "+viewer.getSelected().getWidth());

            node.setText("NODE INFO");
            tree.setText("SUBTREE ANALYSIS");
        }
    }

    private void addToolTips() {
        state.setToolTipText("Node's board state");
        evaluation.setToolTipText("Numeric evaluation of the node's reward on the range [-1, 1]");
        playouts.setToolTipText("Playouts (also called rollouts) are random simulated games. Q is the sum of playout rewards and N is the number of playouts made");
        branching.setToolTipText("Node's children (branches) count");
        nodeCount.setToolTipText("Subtree node count");
        branchingMean.setToolTipText("Average branching factor of all non-leaf nodes in the subtree");
        dimensions.setToolTipText("Subtree height and width");
    }
}
