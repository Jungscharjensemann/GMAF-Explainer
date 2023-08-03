package de.ja.view.table;

import de.ja.model.table.GraphCodeTableModel;
import de.ja.view.table.renderer.BetterGraphCodeRenderer;
import de.swa.gc.GraphCode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Arrays;

public class GraphCodeTable extends JPanel {

    private final JLabel jTablePlaceHolder;

    private GraphCodeTableModel graphCodeTableModel;

    private JTable graphCodeTable;

    private JPanel tablePanel;

    private JScrollPane graphCodeTableSP;

    public GraphCodeTable() {
        setLayout(new BorderLayout());

        /*
         * Komponenten vorbereiten
         */

        // GraphCodeTable
        graphCodeTable = new JTable() {

            /*@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                int selCol = getSelectedColumn();
                int selRow = getSelectedRow();
                if(selCol != -1 && selRow != -1) {
                    if(row == selRow || column == selCol) {
                        if(hasFocus()) {
                            if(c instanceof JLayer) {
                                JLayer<?> layer = (JLayer<?>) c;
                                System.out.println(layer.getView().getParent());
                                JTextField tf = (JTextField) layer.getView().getParent().getComponent(1);
                                tf.setForeground(Color.RED);
                            }
                            c.setForeground(Color.RED);
                        }
                    }
                    graphCodeTable.revalidate();
                    graphCodeTable.repaint();
                }
                return c;
            }*/
        };
        graphCodeTableModel = new GraphCodeTableModel();
        graphCodeTable.setModel(graphCodeTableModel);
        //graphCodeTable.getTableHeader().setDefaultRenderer(new GraphCodeRenderer(true));
        //graphCodeTable.setDefaultRenderer(String.class, new GraphCodeRenderer(false));
        graphCodeTable.setDefaultRenderer(String.class, new BetterGraphCodeRenderer());
        graphCodeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        graphCodeTable.setTableHeader(null);

        JTableHeader graphCodeTableHeader = graphCodeTable.getTableHeader();
        //graphCodeTableHeader.setPreferredSize(new Dimension(500, 50));

        tablePanel = new JPanel();
        tablePanel.setLayout(new GridBagLayout());
        tablePanel.add(graphCodeTable);

        graphCodeTableSP = new JScrollPane(tablePanel);
        graphCodeTableSP.setBorder(new TitledBorder("GraphCode - Table"));
        graphCodeTableSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        graphCodeTableSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        graphCodeTableSP.getVerticalScrollBar().setUnitIncrement(16);
        graphCodeTableSP.getHorizontalScrollBar().setUnitIncrement(16);

        jTablePlaceHolder = new JLabel("No GraphCode selected!");
        jTablePlaceHolder.setBorder(new EmptyBorder(5, 5, 5, 5));
        jTablePlaceHolder.setHorizontalAlignment(SwingConstants.CENTER);
        jTablePlaceHolder.setHorizontalTextPosition(SwingConstants.CENTER);
        add(jTablePlaceHolder, BorderLayout.CENTER);
        //addPlaceHolder();
    }

    public void setGraphCode(GraphCode graphCode) {
        if(graphCode != null) {
            remove(jTablePlaceHolder);

            graphCodeTableModel.setGraphCode(graphCode);
            graphCodeTableModel.fireTableStructureChanged();

            graphCodeTable.setRowHeight(0, 60);
            graphCodeTable.getColumnModel().getColumn(0).setMaxWidth(50);

            for (int i = 1; i < graphCodeTable.getColumnModel().getColumnCount(); i++) {
                graphCodeTable.getColumnModel().getColumn(i).setMaxWidth(12);
            }

            add(graphCodeTableSP, BorderLayout.CENTER);
        } else {
            if(graphCodeTable != null) {
                remove(graphCodeTableSP);
                graphCodeTableModel.setGraphCode(null);
            }
            addPlaceHolder();
        }
        revalidate();
        repaint();
    }

    private void addPlaceHolder() {
        if(!Arrays.asList(getComponents()).contains(jTablePlaceHolder)) {
            add(jTablePlaceHolder, BorderLayout.CENTER);
        }
    }
}
