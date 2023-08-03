package de.swa.ui.command;

import java.util.ArrayList;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;

import de.swa.ui.MMFGCollection;
import de.swa.ui.QueryResultFrame;
import de.swa.ui.panels.LogPanel;
import de.swa.ui.panels.QueryResultPanel;

/** class to encapsulate SPARQL commands **/

public class QueryBySPARQLCommand extends AbstractCommand {
	
	private String query;
	private String session_id;
	
	public QueryBySPARQLCommand(String q) {
		query = q;
	}
	
	public void setSessionId(String s) {
		session_id = s;
	}
	
	@Override
	public void execute() {
		MMFGCollection.isQuery = true;
		LogPanel.getCurrentInstance().addToLog("executing SPARQL query: " + query);
		
		Query q = QueryFactory.create(query);
		
		QueryExecution qexec = QueryExecutionFactory.create(q, MMFGCollection.getInstance().getRDFModel());
		
		switch(q.queryType()) {
			case ASK:askQuery(qexec);
				break;
			case CONSTRUCT:constructQuery(qexec);
				break;
			case DESCRIBE:describeQuery(qexec);
				break;
			case SELECT:selectQuery(qexec);
				break;
			default: System.out.println("Query Typ wird nicht unterst�tzt!");
				break;
		}
		
		LogPanel.getCurrentInstance().addToLog("returning ranked result list.");
	}
	
	public void askQuery(QueryExecution qexec) {
		if (QueryResultFrame.getInstance() == null) new QueryResultFrame();
		Boolean result = qexec.execAsk();
		QueryResultPanel.getCurrentInstance().displayResult(result.toString());
	    QueryResultFrame.getInstance().toFront();
	    QueryResultFrame.getInstance().setVisible(true);
	    qexec.close();
	}
	
	public void constructQuery(QueryExecution qexec) {
		if (QueryResultFrame.getInstance() == null) new QueryResultFrame();
		Model resultModel = qexec.execConstruct();
		QueryResultPanel.getCurrentInstance().displayResult(resultModel.toString());
	    QueryResultFrame.getInstance().toFront();
	    QueryResultFrame.getInstance().setVisible(true);
	    qexec.close();
	}
	
	public void describeQuery(QueryExecution qexec) {
		if (QueryResultFrame.getInstance() == null) new QueryResultFrame();
		Model resultModel = qexec.execDescribe();
		QueryResultPanel.getCurrentInstance().displayResult(resultModel.toString());
		QueryResultFrame.getInstance().setVisible(true);
		QueryResultFrame.getInstance().toFront();
	    qexec.close();
	}
	
	public void selectQuery(QueryExecution qexec) {
		if (QueryResultFrame.getInstance() == null) new QueryResultFrame();
			
		ResultSet results = qexec.execSelect() ;
		int colNum = results.getResultVars().size();
		int rowNum = 0;
		String[] header = new String[colNum];
		results.getResultVars().toArray(header);
		ArrayList<ArrayList> tempData = new ArrayList<ArrayList>();
		    
		for ( ; results.hasNext() ; )
		{
		  QuerySolution soln = results.nextSolution() ;
		  ArrayList row = new ArrayList();
		  for(int i=0;i<colNum;i++) {		    	  
			  row.add((Object)soln.get(header[i]));
		  }
		  tempData.add(row); 
		  rowNum++;
		  
		}
		Object[][] data = new Object[rowNum][colNum];
		
		for(int i=0;i<rowNum;i++) {
			for(int j=0;j<colNum;j++) {
				data[i][j] = tempData.get(i).get(j);
			}
		}
		    
		QueryResultPanel.getCurrentInstance().setTable(data,header);
		QueryResultFrame.getInstance().toFront();
		QueryResultFrame.getInstance().setVisible(true);
		qexec.close();
	}
}
