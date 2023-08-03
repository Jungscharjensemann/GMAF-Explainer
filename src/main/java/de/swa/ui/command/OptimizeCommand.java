package de.swa.ui.command;

import java.io.File;

import de.swa.gc.GraphCode;
import de.swa.gc.GraphCodeCollection;
import de.swa.gc.GraphCodeIO;
import de.swa.ui.MMFGCollection;
import de.swa.ui.Configuration;

/** class to encapsulate the Optimize Command **/

public class OptimizeCommand extends AbstractCommand {
	public void execute() {
		GraphCode gcStop = GraphCodeCollection.calculateGCStop(MMFGCollection.getInstance().getCollectionGraphCodes(),
				false);
		GraphCodeIO.write(gcStop,
				new File(Configuration.getInstance().getGraphCodeRepository() + File.separatorChar + "GCStop.gc"));
	}
}
