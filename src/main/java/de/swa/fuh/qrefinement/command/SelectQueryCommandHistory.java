package de.swa.fuh.qrefinement.command;

import java.util.Vector;

import de.swa.fuh.qrefinement.logic.Querying;


/**
 * 
 * 
 * This class implements a command history for all commands that changed selection of an asset or produced a new query
 * 
 * @author Nicolas Boch
 *
 */
public class SelectQueryCommandHistory {
	/**
	 * Singleton Pattern. This class variable is a reference to the single instance.
	 */
	private static SelectQueryCommandHistory instance;
	
	/**
	 * position of next insert or next command to be redone
	 */
	private int position;
	
	/**
	 * List of all command objects in command history
	 */
	private Vector<UndoableCommand> commands = new Vector<UndoableCommand> ();
	
	private SelectQueryCommandHistory () {}
	
	public static SelectQueryCommandHistory getInstance ()
	{
		if (instance == null)
		{
			instance = new SelectQueryCommandHistory ();
		}
		
		return instance;
	}
	
	public void add (UndoableCommand command)
	{
		if (position < commands.size())
		{
			for (int i = position; i < commands.size(); i++)
			{
				commands.remove(position);
			}
		}
		
		command.execute();
		commands.add(command);
		position++;
	}
	
	public void redo ()
	{
		if (position >= commands.size())
		{
			throw new IllegalStateException ("Nothing to be redone");
		}
		
			commands.get(position).redo();
			position ++;
		
	}
	
	public void undo ()
	{
		if ((position - 1) < 0)
		{
			throw new IllegalStateException ("Nothing to be undone");
		}
		
		commands.get(position - 1).undo();
		position --;
	}
	
	/**
	 * Get the metric that was used to execute the last query (for equivalent redoing of query execution)
	 * @return the metric that was used as described above
	 */
	public Querying getLastQueryMetric ()
	{
		if ((position -1) < 0)
		{
			return null;
		}
		for (int j = (position - 1); j >= 0; j--)
		{
			if (commands.get(j) instanceof RequeryCommand)
			{
				RequeryCommand req = (RequeryCommand) commands.get(j);
				return req.getMetric();
			}
		}
		return null;
	}
	
	/**
	 * Get the metric that was used to execute the penultimate query (for equivalent undoing of the last query, implemented by a re-execution of the penultimate query)
	 * @return
	 */
	public Querying getLastQueryMetricUndo ()
	{
		if ((position -2) < 0)
		{
			return null;
		}
		for (int j = (position - 2); j >= 0; j--)
		{
			if (commands.get(j) instanceof RequeryCommand)
			{
				RequeryCommand req = (RequeryCommand) commands.get(j);
				return req.getMetric();
			}
		}
		return null;
	}
}
