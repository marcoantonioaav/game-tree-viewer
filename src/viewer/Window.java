package viewer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import viewer.panels.TreeMinimap;

public class Window extends JFrame implements ActionListener {
    private Viewer viewer;

    private JPanel sidePanel = new JPanel(new GridLayout(0, 1, 0, 0));

    public Window(Viewer viewer) {
        this.viewer = viewer;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setTitle(viewer.getTitle());
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addMenuBar();
        add(viewer.getTreeDisplay());
        sidePanel.add(viewer.getStateDisplay());
        sidePanel.add(viewer.getInfoPanel());

        JScrollPane scroller = new JScrollPane(viewer.getTreeMinimap(), JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroller.setPreferredSize(new Dimension(TreeMinimap.WIDTH, TreeMinimap.HEIGHT));
        sidePanel.add(scroller);
        add(sidePanel);
        pack();
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu file, edit, help;
        JMenuItem save, saveAs, open, export;
        file = new JMenu("File");
        open = new JMenuItem("Open...");
        open.addActionListener(this);
        save = new JMenuItem("Save");
        save.addActionListener(this);
        saveAs = new JMenuItem("Save as...");
        saveAs.addActionListener(this);
        export = new JMenuItem("Export");
        export.addActionListener(this);
        file.add(open);
        file.add(save);
        file.add(saveAs);
        file.add(export);
        edit = new JMenu("Edit");
        help = new JMenu("Help");
        menuBar.add(file);
        menuBar.add(edit);
        menuBar.add(help);
        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Open...":
                System.out.println(e.getActionCommand());
                break;
            case "Save":
                System.out.println(e.getActionCommand());
                break;
            case "Save as...":
                System.out.println(e.getActionCommand());
                break;
            case "Export":
                if(viewer.getTree() != null)
                    viewer.export();
                break;
            default:
                break;
        }
    }
}
