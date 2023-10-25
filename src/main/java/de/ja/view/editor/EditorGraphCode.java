package de.ja.view.editor;

import de.ja.controller.*;
import de.ja.handler.jlist.ListItemTransferHandler;
import de.ja.model.editor.GraphCodeListElement;
import de.ja.view.ExplainerFrame;
import de.ja.view.editor.renderer.GraphCodeListRenderer;
import de.ja.view.table.GraphCodeTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Diese Klasse stellt den Grundbereich
 * EditorGraphCode dar.
 */
public class EditorGraphCode extends JPanel {

    // Name eines ausgewählten Graph Codes.
    private final JLabel graphCodeName;

    // Tabellarische Darstellung eines ausgewählten Graph Codes.
    private final GraphCodeTable graphCodeTable;

    // Darstellung der mit ausgewähltem Graph Code
    // assoziierten originalen Datei.
    private final OriginalAssetPanel originalAssetPanel;

    // Auswahlliste für Operationen auf Graph Codes.
    private final JComboBox<String> calculationComboBox;

    // Liste an Graph Codes.
    private final JList<GraphCodeListElement> graphCodeList;

    // Datenmodell für die Liste an Graph Codes.
    private final DefaultListModel<GraphCodeListElement> graphCodeListModel;

    public EditorGraphCode(ExplainerFrame reference) {
        setLayout(new GridLayout(1, 2));
        setBorder(new TitledBorder("GraphCode - Editor"));
        // Panel für Interaktionsmöglichkeiten mit Graph Code Dateien.
        JPanel operationsPanel = new JPanel();
        operationsPanel.setLayout(new GridLayout(0, 2));
        operationsPanel.setMinimumSize(new Dimension(0, 100));
        // Knopf zum Importieren von Graph Code Dateien.
        JButton openGraphCodeChooserButton = new JButton("Import Graph Code(s)");
        openGraphCodeChooserButton.addActionListener(new ImportGraphCodesController(this));
        operationsPanel.add(openGraphCodeChooserButton);
        // Knopf zum Entfernen von Graph Code Dateien.
        JButton removeSelectedButton = new JButton("Remove selected Graph Code(s)");
        removeSelectedButton.addActionListener(new RemoveSelectedGraphCodesController(this));
        operationsPanel.add(removeSelectedButton);
        // Auswahlliste für Operationen.
        calculationComboBox = new JComboBox<>();
        calculationComboBox.addItem("GraphCode Operation");
        calculationComboBox.addItem("Union");
        calculationComboBox.addItem("Subtraction");
        calculationComboBox.addItem("Similarities");
        calculationComboBox.addItem("Differences");
        operationsPanel.add(calculationComboBox);
        // Knopf zum Ausführen einer Operation auf Graph Codes.
        JButton calculationOperationButton = new JButton("Execute");
        calculationOperationButton.addActionListener(new GraphCodeCalculationController(reference));
        operationsPanel.add(calculationOperationButton);
        // Linker Teil der Arbeitsfläche.
        JPanel leftPart = new JPanel();
        leftPart.setLayout(new BorderLayout());
        leftPart.setMinimumSize(new Dimension(200, 300));
        leftPart.add(operationsPanel, BorderLayout.NORTH);

        graphCodeListModel = new DefaultListModel<>();
        // Liste an Graph Codes initialisieren und konfigurieren.
        graphCodeList = new JList<>(graphCodeListModel);
        graphCodeList.setLayout(new FlowLayout(FlowLayout.LEADING));
        graphCodeList.setMinimumSize(new Dimension(100, 50));
        graphCodeList.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        graphCodeList.addListSelectionListener(new GraphCodeSelectionListener(reference));
        graphCodeList.addMouseListener(new GraphCodeListMouseAdapter(this));
        graphCodeList.setTransferHandler(new ListItemTransferHandler<GraphCodeListElement>());
        graphCodeList.setDragEnabled(true);
        graphCodeList.setDropMode(DropMode.INSERT);
        graphCodeList.setCellRenderer(new GraphCodeListRenderer());
        graphCodeList.setVisibleRowCount(-1);
        graphCodeList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        graphCodeList.putClientProperty("List.isFileList", Boolean.TRUE);

        leftPart.add(new JScrollPane(graphCodeList), BorderLayout.CENTER);
        // Rechter Teil der Arbeitsfläche.
        JPanel rightPart = new JPanel();
        rightPart.setLayout(new BorderLayout());

        JPanel topRight = new JPanel();
        topRight.setLayout(new BorderLayout());
        // Panel für tabellarische und originale Darstellung
        // eines ausgewählten Graph Codes.
        JTabbedPane tabbedPane = new JTabbedPane();

        graphCodeName = new JLabel();
        graphCodeName.setBorder(new EmptyBorder(5, 5, 5, 5));
        graphCodeName.setHorizontalAlignment(JLabel.CENTER);
        graphCodeName.setHorizontalTextPosition(SwingConstants.CENTER);
        topRight.add(graphCodeName, BorderLayout.CENTER);
        // Tabellarische Darstellung eines ausgewählten Graph Codes.
        graphCodeTable = new GraphCodeTable();
        // Darstellung der Original-Datei eines ausgewählten Graph Codes.
        originalAssetPanel = new OriginalAssetPanel();
        // Tabs hinzufügen.
        tabbedPane.addTab("GraphCode - Table", graphCodeTable);
        tabbedPane.addTab("Original Asset", originalAssetPanel);

        rightPart.add(tabbedPane, BorderLayout.CENTER);
        rightPart.add(topRight, BorderLayout.NORTH);

        add(leftPart);
        add(rightPart);
    }

    public JComboBox<String> getCalculationComboBox() {
        return calculationComboBox;
    }

    public JList<GraphCodeListElement> getGraphCodeList() {
        return graphCodeList;
    }

    public OriginalAssetPanel getOriginalAssetPanel() {
        return originalAssetPanel;
    }

    public JLabel getGraphCodeName() {
        return graphCodeName;
    }

    public GraphCodeTable getGraphCodeTable() {
        return graphCodeTable;
    }

    public DefaultListModel<GraphCodeListElement> getGraphCodeListModel() {
        return graphCodeListModel;
    }
}
