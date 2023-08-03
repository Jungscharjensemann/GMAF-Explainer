package de.swa.fuh.qrefinement.command;

import java.util.Vector;

/**
 * 
 * 
 * This class implements a command history for all commands that changed relevance marks of images or text
 * 
 * @author Nicolas Boch
 *
 */
public class RelevanceMarkCommandHistory {

	/**
	 * Singleton Pattern. This class Variable is a reference to the single instance.
	 */
	private static RelevanceMarkCommandHistory instance;
	
	/**
	 * List of all command objects in command history
	 */
	private Vector<UndoableCommand> commands = new Vector<UndoableCommand> ();
	
	/**
	 * position of next insert or next command to be redone
	 */
	private int position = 0; //position of next insert or next command to be redone
	
	private RelevanceMarkCommandHistory ()
	{
		
	}
	
	public static RelevanceMarkCommandHistory getInstance()
	{
		if (instance == null)
		{
			instance = new RelevanceMarkCommandHistory();
		}
		return instance;
	}
	
	/**
	 * Add a new command to the command history.
	 * @param command Command that should be added to command history and then executed.
	 */
	public void add (UndoableCommand command)
	{
		// Remove all elements after input index if the new command is not inserted at the end of the list
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
	
	public void clearHistory()
	{
		commands.clear();
		position = 0;
	}
}
