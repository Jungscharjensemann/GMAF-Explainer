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

public class EditorGraphCode extends JPanel {
    private JPanel operationsPanel;

    private JPanel calculationPanel;

    private JPanel leftPart;

    private JPanel rightPart;

    private final JLabel graphCodeName;

    private final GraphCodeTable graphCodeTable;

    private final JComboBox<String> calculationComboBox;
    private final JList<GraphCodeListElement> graphCodeList;

    private final DefaultListModel<GraphCodeListElement> graphCodeListModel;

    public EditorGraphCode(ExplainerFrame reference) {

        setLayout(new GridLayout(1, 2));
        setBorder(new TitledBorder("GraphCode - Editor"));

        operationsPanel = new JPanel();
        operationsPanel.setLayout(new GridLayout(0, 2));
        operationsPanel.setMinimumSize(new Dimension(0, 100));

        JButton openGraphCodeChooserButton = new JButton("Select Graph Code(s)");
        openGraphCodeChooserButton.addActionListener(new SelectGraphCodesController(this));
        operationsPanel.add(openGraphCodeChooserButton);

        JButton removeSelectedButton = new JButton("Remove selected Graph Code(s)");
        removeSelectedButton.addActionListener(new RemoveSelectedGraphCodesController(this));
        operationsPanel.add(removeSelectedButton);

        calculationPanel = new JPanel();
        calculationPanel.setLayout(new GridLayout(1, 2));

        calculationComboBox = new JComboBox<>();
        calculationComboBox.addItem("Union");
        calculationComboBox.addItem("Subtraction");
        calculationComboBox.addItem("Similarities");
        calculationComboBox.addItem("Differences");
        operationsPanel.add(calculationComboBox);

        JButton calculationOperationButton = new JButton("Execute");
        calculationOperationButton.addActionListener(new GraphCodeCalculationController(this));
        operationsPanel.add(calculationOperationButton);

        leftPart = new JPanel();
        leftPart.setLayout(new BorderLayout());
        leftPart.setMinimumSize(new Dimension(200, 300));

        leftPart.add(operationsPanel, BorderLayout.NORTH);
        leftPart.add(calculationPanel, BorderLayout.SOUTH);

        graphCodeListModel = new DefaultListModel<>();

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

        rightPart = new JPanel();
        rightPart.setLayout(new BorderLayout());

        JPanel topRight = new JPanel();
        topRight.setLayout(new BorderLayout());

        graphCodeName = new JLabel();
        graphCodeName.setBorder(new EmptyBorder(5, 5, 5, 5));
        graphCodeName.setHorizontalAlignment(JLabel.CENTER);
        graphCodeName.setHorizontalTextPosition(SwingConstants.CENTER);
        topRight.add(graphCodeName, BorderLayout.CENTER);

        graphCodeTable = new GraphCodeTable();
        rightPart.add(graphCodeTable, BorderLayout.CENTER);
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
