package de.swa.test;

import java.io.File;

import de.swa.gc.ExplainableGraphCode;
import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeIO;
import de.swa.gmaf.extensions.defaults.GeneralDictionary;
import de.swa.mmfg.extension.Explainer;
import de.swa.mmfg.extension.LanguageModel;

public class ExplainerTest {
	public static void main(String[] args) throws Exception {
		GeneralDictionary dict = GeneralDictionary.getInstance();
		
		GraphCode gc = GraphCodeIO.read(new File("graphcodes/1967-Mustang-Wallpaper-Free-Download.jpg.gc"));

		ExplainableGraphCode egc = new ExplainableGraphCode(gc);
		String text1 = Explainer.explain(egc, 3, LanguageModel.SIMPLE);
		String text = Explainer.explain(egc, 3, LanguageModel.COMPLEX);
		System.out.println("ESGC: " + text);
	}
}
