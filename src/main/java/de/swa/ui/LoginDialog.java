package de.swa.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.swa.ui.command.LoginCommand;

/** progress frame indicating the processing status at startup of the GMAF **/
public class LoginDialog extends JDialog implements ActionListener {
	JButton login = new JButton("LOGIN");
	JButton cancel = new JButton("CANCEL");
	JTextField instance = new JTextField(15);
	JTextField port = new JTextField(5);
	JPasswordField password = new JPasswordField(10);
	
	public LoginDialog() {
		setSize(500, 325);
		setUndecorated(true);
		
		setTitle("Select GMAF instance");
		setLayout(new BorderLayout());
		add(new JLabel(new ImageIcon("resources/splash.png")), "North");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 500) / 2);
	    int y = (int) ((dimension.getHeight() - 250) / 2);
	    setLocation(x, y);

	    JPanel loginPanel = new JPanel();
	    loginPanel.setLayout(new GridLayout(3,2));
	    loginPanel.add(new JLabel("GMAF Instance"));
	    loginPanel.add(instance);
	    loginPanel.add(new JLabel("Port"));
	    loginPanel.add(port);
	    loginPanel.add(new JLabel("Password"));
	    loginPanel.add(password);
	    loginPanel.setBackground(Color.WHITE);
	    JPanel loginPanelWrapper = new JPanel();
	    loginPanelWrapper.add(loginPanel);
	    loginPanelWrapper.setBackground(Color.WHITE);
	    
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(login);
	    buttonPanel.add(cancel);
	    buttonPanel.setBackground(Color.WHITE);
	    
	    JPanel south = new JPanel();
	    south.setBackground(Color.white);
	    south.setLayout(new BorderLayout());
	    south.add(loginPanelWrapper, "North");
	    south.add(buttonPanel, "South");
	    
	    add(south, "South");
	    
	    instance.setText("localhost");
	    port.setText("8481");
	    login.addActionListener(this); 
	    cancel.addActionListener(this);
	    
	    setModal(true);
	    setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == login) {
			setVisible(false);
			dispose();
			LoginCommand lc = new LoginCommand(instance.getText(), port.getText(), "fp2223");
			lc.execute();
		}
		else {
			System.exit(0);
		}
	}
}
