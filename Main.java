import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

class Main {
    private static JFrame frame;
    private static JFXPanel jfx;
    private static Scene scene;
    private static WebView web;
    public static void main(String[] args) {
        // Set theme
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
        
        jfx = new JFXPanel();
        frame.add(jfx, BorderLayout.CENTER);
        
        Platform.runLater(new Runnable() {
            public void run() {
                web = new WebView();
                web.getEngine().load("https://example.com");
                scene = new Scene(new VBox(web));
                jfx.setScene(scene);
            }
        });

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}