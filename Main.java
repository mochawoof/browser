import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.web.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.util.Callback;

class Main {
    private static JFrame frame;
    private static JPanel panel;
    public static void main(String[] args) {
        // Set theme
        // for (javax.swing.UIManager.LookAndFeelInfo l : javax.swing.UIManager.getInstalledLookAndFeels()) {System.out.println(l.getName());}
        try {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if (Settings.get("Theme").equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        frame = new JFrame("Browser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Top bar
        JMenuBar menu = new JMenuBar();
        frame.setJMenuBar(menu);
        
        JMenu tabm = new JMenu("Tab");
        menu.add(tabm);
        
        JMenu editm = new JMenu("Edit");
        menu.add(editm);
        JMenuItem settings = new JMenuItem("Settings...");
        editm.add(settings);
        settings.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int r = Settings.show(frame);
                if (r == Settings.OK || r == Settings.RESET) {
                    if (JOptionPane.showConfirmDialog(frame, "Browser needs to restart to apply your settings. Restart now?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        frame.dispose();
                        main(args);
                    };
                }
            }
        });
        
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        
        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        panel.add(tabs, BorderLayout.CENTER);
        
        // Search bar
        JTextField searchBar = new JTextField();
        panel.add(searchBar, BorderLayout.PAGE_START);
        searchBar.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                
            }
        });
        
        JFXPanel jfx = new JFXPanel();
        tabs.add("Hello", jfx);
        
        Platform.runLater(new Runnable() {
            public void run() {
                WebView web = new WebView();
                WebEngine engine = web.getEngine();
                
                // JS events
                engine.setOnAlert(new EventHandler<WebEvent<String>>() {
                    public void handle(WebEvent<String> e) {
                        JOptionPane.showMessageDialog(frame, e.getData(), "Message", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                engine.setConfirmHandler(new Callback<String, Boolean>() {
                    public Boolean call(String e) {
                        return JOptionPane.showConfirmDialog(frame, e, "Confirm", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
                    }
                });
                engine.setPromptHandler(new Callback<PromptData, String>() {
                    public String call(PromptData e) {
                        String s = (String) JOptionPane.showInputDialog(frame, e.getMessage(), "Input", JOptionPane.QUESTION_MESSAGE, null, null, e.getDefaultValue());
                        return s;
                    }
                });
                
                // Change listeners
                engine.titleProperty().addListener(new ChangeListener<String> () {
                    public void changed(ObservableValue<?extends String> obs, String oldValue, String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                frame.setTitle("Browser" + ((newValue == null || newValue.trim().equals("")) ? "" : " - " + newValue));
                            }
                        });
                    }
                });
                engine.locationProperty().addListener(new ChangeListener<String> () {
                    public void changed(ObservableValue<?extends String> obs, String oldValue, String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                searchBar.setText(newValue);
                            }
                        });
                    }
                });
                
                web.getEngine().load("https://testpages.herokuapp.com/styled/alerts/alert-test.html");
                Scene scene = new Scene(new VBox(web));
                jfx.setScene(scene);
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}