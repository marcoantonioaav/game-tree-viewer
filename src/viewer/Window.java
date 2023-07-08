package viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Window extends JFrame implements ActionListener {
    private Viewer viewer;

    public Window(Viewer viewer) {
        this.viewer = viewer;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setTitle(viewer.getTitle());
        add(viewer.getDisplay());
        addMenuBar();
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
