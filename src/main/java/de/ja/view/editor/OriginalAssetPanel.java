package de.ja.view.editor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.swa.ui.Tools;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * Diese Klasse stellt die mit einem Graph
 * Code assoziierte originale Datei dar.
 */
public class OriginalAssetPanel extends JPanel {

    // Collection-Ordner.
    private final File directory = new File("./collection/");

    // Layout.
    private final BorderLayout borderLayout = new BorderLayout();

    // Platzhalter für Nachrichten.
    private final JLabel placeHolder;

    public OriginalAssetPanel() {
        setLayout(borderLayout);
        placeHolder = new JLabel();
    }

    /**
     * Diese Methode sucht im Ordner collection
     * nach identischen Dateien mit demselben Namen.
     * @param fileName Namen der Datei.
     */
    @SuppressWarnings("deprecation")
    public void searchForSimilarFile(String fileName) {
        Component centerComp = borderLayout.getLayoutComponent(this, BorderLayout.CENTER);
        if(centerComp != null) {
            remove(centerComp);
        }
        if(fileName != null && !fileName.isEmpty()) {
            if(fileName.endsWith(".gc")) {
                File[] files = directory.listFiles((dir, name) -> fileName.substring(0, fileName.length() - 3).equalsIgnoreCase(name));
                if(files != null) {
                    // Entnommen aus der Klasse AssetDetailPanel.java
                    if(files.length > 0) {
                        File first = files[0];
                        if (first != null) {
                            String extension = first.getName();
                            extension = extension.substring(extension.lastIndexOf(".") + 1);
                            extension = extension.toLowerCase();

                            if (extension.equals("png") || extension.equals("gif") || extension.equals("jpg")
                                    || extension.equals("jpeg") || extension.equals("bmp") || extension.equals("tiff")) {
                                ImageIcon ii = new ImageIcon(first.getAbsolutePath());

                                Image i = Tools.getScaledInstance(first.getName(), ii.getImage(), 500,
                                        false);
                                JLabel l = new JLabel(new ImageIcon(i));

                                add(l, BorderLayout.CENTER);
                            } else {
                                try {
                                    RandomAccessFile rf = new RandomAccessFile(first, "r");
                                    String line;
                                    StringBuilder content = new StringBuilder();
                                    while ((line = rf.readLine()) != null) {
                                        content.append(line).append("\n");
                                    }

                                    String prettyFormat = content.toString();
                                    try {
                                        JsonParser parser = new JsonParser();
                                        JsonObject json = parser.parse(content.toString()).getAsJsonObject();

                                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                        prettyFormat = gson.toJson(json);
                                    } catch (Exception ignored) {}

                                    JEditorPane ep = new JEditorPane();
                                    ep.setText(prettyFormat);
                                    add(new JScrollPane(ep), BorderLayout.CENTER);
                                    rf.close();
                                } catch (Exception x) {
                                    x.printStackTrace();
                                }
                            }
                        }
                    } else {
                        if(centerComp != null) {
                            remove(centerComp);
                        }
                        // Keine passende Datei in Collection gefunden.
                        placeHolder.setHorizontalAlignment(SwingConstants.CENTER);
                        placeHolder.setVerticalAlignment(SwingConstants.CENTER);
                        placeHolder.setText("No matching file found in collection!");
                        add(placeHolder, BorderLayout.CENTER);
                    }
                }
            } else {
                if(centerComp != null) {
                    remove(centerComp);
                }
                // Keine passende Datei in Collection gefunden.
                placeHolder.setHorizontalAlignment(SwingConstants.CENTER);
                placeHolder.setVerticalAlignment(SwingConstants.CENTER);
                placeHolder.setText("No matching file found in collection!");
                add(placeHolder, BorderLayout.CENTER);
            }
        } else {
            if(centerComp != null) {
                remove(centerComp);
            }
            // Keine Graph Code ausgewählt.
            placeHolder.setHorizontalAlignment(SwingConstants.CENTER);
            placeHolder.setVerticalAlignment(SwingConstants.CENTER);
            placeHolder.setText("No GraphCode selected!");
            add(placeHolder, BorderLayout.CENTER);
        }
    }
}
