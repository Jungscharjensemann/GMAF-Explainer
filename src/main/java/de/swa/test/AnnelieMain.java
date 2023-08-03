package de.swa.test;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeIO;

public class AnnelieMain {
	public static void main(String[] args) {
		JFrame fenster = new JFrame();
		
		GraphCode gc = GraphCodeIO.read(new File("graphcodes/1235.gc"));
		
		// hier kommt Deine Darstellung / Logik / usw. rein
		JPanel expl = new JPanel();
		
		fenster.add(expl);
		
		fenster.setSize(300, 300);
		fenster.setVisible(true);
	}
}
