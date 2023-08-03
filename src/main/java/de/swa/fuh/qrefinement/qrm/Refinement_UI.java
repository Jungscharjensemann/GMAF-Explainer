package de.swa.fuh.qrefinement.qrm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import de.swa.fuh.qrefinement.command.BackToGMAFCommand;
import de.swa.fuh.qrefinement.command.BackToMainCardCommand;
import de.swa.fuh.qrefinement.command.ExitCommand;
import de.swa.fuh.qrefinement.command.ExpertModeCommand;
import de.swa.fuh.qrefinement.command.RequeryCommand;
import de.swa.fuh.qrefinement.command.SelectQueryCommandHistory;
import de.swa.fuh.qrefinement.command.StartEvalCommand;
import de.swa.fuh.qrefinement.command.StopEvalCommand;
import de.swa.fuh.qrefinement.ui.panels.ConfigDialog;
import de.swa.fuh.qrefinement.ui.panels.DetailRelevanceMarksPanel;
import de.swa.fuh.qrefinement.ui.panels.ImageRefinementPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementAssetListPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementDetailPanel;
import de.swa.fuh.qrefinement.ui.panels.RefinementListTable;
import de.swa.fuh.qrefinement.ui.panels.TextRefinementPanel;
import de.swa.ui.GMAF_UI;

/** 
 * Implementation of the main frame of the UI
 * 
 * @author Nicolas Boch
 *
 */
public class Refinement_UI extends JFrame implements ActionListener {
	
	
	/**
	 * static instance variable for implementation of Singleton pattern
	 */
	private static Refinement_UI instance;
	/**
	 * string to identify panel containing main ui content
	 */
	public final static String CONTENTPANEL = "Contentpanel";
	/**
	 * string to identify panel containing ui panel for specifying parts of image contents
	 */
	public final static String IMAGEPANEL = "Imagepanel";
	/**
	 * string to identify panel containing ui panel for specifying parts of text
	 */
	public final static String TEXTPANEL = "Textpanel";
	/**
	 * JPanel containing different cards for several frames in the same window showing up at different times
	 */
	private JPanel cards;
	
	private Vector<JComponent> expcomps = new Vector<JComponent> ();
	
	private JComponent expbutton;
	
	private boolean expmode = false;
	
	public static void init()
	{
		if (instance == null)
		{
			instance = new Refinement_UI();
		}
	}
	
	public static Refinement_UI getInstance()
	{
		return instance;
	}
	
	
	/**
	 * set static instance variable to null. Should be called when disposing this frame.
	 */
	public static void clear()
	{
		instance = null;
	}
	
	private Refinement_UI()
	{

	cards = new JPanel();
	cards.setLayout(new CardLayout());
	
	JPanel content = new JPanel();
	content.setLayout(new BorderLayout(5,5));
	content.setName(CONTENTPANEL);
	
	System.out.println("Contentpanel created");
	
	JToolBar tb = new JToolBar();
	tb.setFloatable(false);
	tb.add(createToolBarItem("Close without return to GMAF","CL", "resources/standby.png"));
	tb.add(createToolBarItem("Close and return to GMAF","CLG","resources/window_close.png"));
	tb.add(createToolBarItem("Requery with Refinement","REQ","resources/arrow_circle2.png"));
	tb.add(createToolBarItem("Undo", "UND", "resources/arrow_u_turn.png"));
	tb.add(createToolBarItem("Redo","RED", "resources/arrow_right.png"));
	expbutton = (createToolBarItem("Expertmode","EXP","resources/engineer.png"));
	tb.add(expbutton);
	expcomps.add(createToolBarItem("Config","CONF","resources/gearwheel.png"));
	expcomps.add(createToolBarItem("Start or resume Eval","START","resources/media_play.png"));
	expcomps.add(createToolBarItem("Stop Eval","STOP","resources/media_stop.png"));
	
	for (JComponent c : expcomps)
	{
		c.setVisible(false);
		tb.add(c);
	}
	
	setTitle("Query Refinement");
	content.add(tb, "North");
	content.add(new JScrollPane(new RefinementAssetListPanel()),"Center");
	content.add(DetailRelevanceMarksPanel.getInstance(),"East");
	setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowListener(){
		public void windowClosing(WindowEvent e)
		{
			String name = "";
			for (Component comp : cards.getComponents())
			{
				if (comp.isVisible())
				{
					name = ((JPanel) comp).getName();
				}
			}
			
			if (name.equals(IMAGEPANEL) || name.equals(TEXTPANEL))
			{
				new BackToMainCardCommand().execute();
			}
			else
			{
			RefinementCollection.clear();
			dispose();
			Refinement_UI.clear();
			}
		}

		@Override
		public void windowOpened(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowActivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			// TODO Auto-generated method stub
			
		}
	});

	cards.add(content, CONTENTPANEL);
	cards.add(ImageRefinementPanel.getInstance(), IMAGEPANEL);
	cards.add(TextRefinementPanel.getInstance(), TEXTPANEL);
	getContentPane().add(cards);
	setSize(1200,1200);
	if (GMAF_UI.getInstance() != null)
	{
		setLocationRelativeTo(GMAF_UI.getInstance());
	}
	setVisible(true);
//	setExtendedState(JFrame.MAXIMIZED_BOTH);
	}


	/**
	 * creates a toolbar item consisting of a button with an icon
	 * @param text the text shown in the created button
	 * @param action the action command associated with the created button
	 * @param icon the path to an image file where the image is shown as an icon in the created button
	 * @return
	 */
	private JComponent createToolBarItem(String text, String action, String icon) {
		if (action.equals("EXP")) {
			JToggleButton b = new JToggleButton(text, new ImageIcon(icon));
			b.setActionCommand(action);
			b.setToolTipText(text);
			b.addActionListener(this);
			b.setBorderPainted(true);
			b.setSelected(false);
			return b;
		}
		else
		{
		
			JButton b = new JButton(text, new ImageIcon(icon));
			b.setActionCommand(action);
			b.setToolTipText(text);
			b.addActionListener(this);
			return b;
		}
		}


public JPanel getCards ()
{
	return cards;
}

public boolean getExpertMode ()
{
	return expmode;
}

public void setExpertMode (boolean b)
{
	expmode = b;
}

public Vector<JComponent> getExpertComponents ()
{
	return expcomps;
}


/**
 * Implementation of interface ActionListener
 */
public void actionPerformed (ActionEvent e)
{
	
	if (e.getActionCommand() == "REQ")
	{
		SelectQueryCommandHistory.getInstance().add(new RequeryCommand());
	}
	
	if (e.getActionCommand() == "B")
	{
		CardLayout c = (CardLayout)cards.getLayout();
		RefinementListTable.getInstance().refresh();
		RefinementDetailPanel.getCurrentInstance().refresh();
		c.show(cards, CONTENTPANEL);
	}
	
	if (e.getActionCommand() == "CLG")
	{
		new BackToGMAFCommand().execute();
	}
	
	if (e.getActionCommand() == "CL")
	{
		new ExitCommand().execute();
	}
	
	if (e.getActionCommand() == "UND")
	{
		try
		{
		SelectQueryCommandHistory.getInstance().undo();
		}
		catch (IllegalStateException exe)
		{
			JOptionPane.showMessageDialog(Refinement_UI.getInstance(), exe.getMessage());
		}
	}
	
	if (e.getActionCommand() == "RED")
	{
		try
		{
		SelectQueryCommandHistory.getInstance().redo();
		}
		catch (IllegalStateException exec)
		{
			JOptionPane.showMessageDialog(Refinement_UI.getInstance(), exec.getMessage());
		}
	}
	
	if (e.getActionCommand() == "CONF")
	{
		ConfigDialog cd = new ConfigDialog(this);
		cd.setVisible(true);
	}
	
	if (e.getActionCommand() == "START")
	{
		new StartEvalCommand().execute();
	}
	
	if (e.getActionCommand() == "STOP")
	{
		new StopEvalCommand().execute();
	}
	
	if (e.getActionCommand() == "EXP")
	{
		new ExpertModeCommand().execute();
	}
		
}

	
}
