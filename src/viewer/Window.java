package viewer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import panels.TreeDisplay;
import panels.TreeDisplayNode;
import panels.TreeMinimap;

public class Window extends JFrame implements ActionListener, ItemListener{
    private Viewer viewer;

    private JPanel sidePanel = new JPanel(new GridLayout(0, 1, 0, 0));

    public Window(Viewer viewer) {
        this.viewer = viewer;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle(viewer.getTitle());
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addMenuBar();

        JScrollPane displayScroller = new JScrollPane(viewer.getTreeDisplay(), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        displayScroller.setPreferredSize(new Dimension(TreeDisplay.WIDTH + 5, TreeDisplay.HEIGHT));
        add(displayScroller);

        sidePanel.add(viewer.getStateDisplay());
        sidePanel.add(viewer.getInfoPanel());

        JScrollPane minimapScroller = new JScrollPane(viewer.getTreeMinimap(), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        minimapScroller.setPreferredSize(new Dimension(TreeMinimap.WIDTH, TreeMinimap.HEIGHT));
        sidePanel.add(minimapScroller);
        add(sidePanel);
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JRadioButtonMenuItem nothing, evaluation, playouts; 
    private JCheckBoxMenuItem showCursor;

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file, edit, help, colorBy;
        JMenuItem export, goToRoot;
        
        ButtonGroup colorByGroup = new ButtonGroup();

        file = new JMenu("File");

        export = new JMenuItem("Export...");
        export.addActionListener(this);
        
        file.add(export);
        edit = new JMenu("Edit");
        colorBy = new JMenu("Color by...");
        nothing = new JRadioButtonMenuItem("Nothing");
        nothing.addItemListener(this);
        evaluation = new JRadioButtonMenuItem("Evaluation");
        evaluation.addItemListener(this);
        playouts = new JRadioButtonMenuItem("Playouts");
        playouts.addItemListener(this);
        colorByGroup.add(nothing);
        colorByGroup.add(evaluation);
        colorByGroup.add(playouts);
        colorBy.add(nothing);
        colorBy.add(evaluation);
        colorBy.add(playouts);
        goToRoot = new JMenuItem("Go to root");
        goToRoot.addActionListener(this);
        showCursor = new JCheckBoxMenuItem("Show cursor");
        showCursor.addItemListener(this);
        edit.add(goToRoot);
        edit.add(colorBy);
        edit.add(showCursor);
        help = new JMenu("Help");
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);
        setJMenuBar(menuBar);
    }

    public void update() {
        if(viewer.getTree() == null) 
            return;
        if(viewer.getTree().getTreeDisplayNode().getColorBy() == TreeDisplayNode.AUTO)
            viewer.getTree().getTreeDisplayNode().getColor();
        nothing.setSelected(viewer.getTree().getTreeDisplayNode().getColorBy() == TreeDisplayNode.NOTHING);
        evaluation.setSelected(viewer.getTree().getTreeDisplayNode().getColorBy() == TreeDisplayNode.EVALUATION);
        playouts.setSelected(viewer.getTree().getTreeDisplayNode().getColorBy() == TreeDisplayNode.PLAYOUTS);

        showCursor.setSelected(viewer.getTree().getTreeDisplayNode().getShowCursor());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Export...":
                if(viewer.getTree() != null) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.addChoosableFileFilter(new FileTypeFilter(".png", "PNG"));
                    fileChooser.addChoosableFileFilter(new FileTypeFilter(".gif", "GIF"));
                    fileChooser.addChoosableFileFilter(new FileTypeFilter(".jpg", "JPG"));
                    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        viewer.export(fileChooser.getSelectedFile().getAbsolutePath(), ((FileTypeFilter)fileChooser.getFileFilter()).getExtension());
                    }
                }
                break;
            case "Go to root":
                viewer.getTreeDisplay().setRoot(viewer.getTree());
                viewer.setSelected(viewer.getTree());
                break;
            default:
                break;
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getItem().getClass() == JRadioButtonMenuItem.class) {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                String selectedName = ((JRadioButtonMenuItem)e.getItem()).getText();
                if(selectedName == "Nothing") {
                    viewer.getTree().getTreeDisplayNode().setColorBy(TreeDisplayNode.NOTHING);
                }
                else if(selectedName == "Evaluation") {
                    viewer.getTree().getTreeDisplayNode().setColorBy(TreeDisplayNode.EVALUATION);
                }
                else if(selectedName == "Playouts") {
                    viewer.getTree().getTreeDisplayNode().setColorBy(TreeDisplayNode.PLAYOUTS);
                }
                viewer.getTreeDisplay().repaint();
                viewer.getTreeMinimap().update();
            }
        }
        else {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                viewer.getTree().getTreeDisplayNode().setShowCursor(true);
            }
            else {
                viewer.getTree().getTreeDisplayNode().setShowCursor(false);
            }
            viewer.getTreeDisplay().repaint();
        }
    }

    private class FileTypeFilter extends FileFilter {
        private String extension;
        private String description;
     
        public FileTypeFilter(String extension, String description) {
            this.extension = extension;
            this.description = description;
        }
     
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            return file.getName().endsWith(extension);
        }
     
        public String getDescription() {
            return description + String.format(" (*%s)", extension);
        }

        public String getExtension() {
            return extension;
        }
    }
}
