package de.swa.fuh.qrefinement.logic;

import java.util.Vector;

import de.swa.fuh.qrefinement.command.SelectQueryCommandHistory;
import de.swa.fuh.qrefinement.qrm.RefinementCollection;
import de.swa.fuh.qrefinement.qrm.RefinementUIModel;
import de.swa.gc.GraphCode;

/**
 * 
 * 
 * Class implementing contruction of new query based on an adapted rocchio weighting schema. This class implements Strategy Pattern.
 * 
 * @author Nicolas Boch
 *
 */
public class RelationshipWeightRocchioRelevanceFeedback implements RelevanceFeedback{

	/**
	 * Name of the algorithm to be shown in UI configuration dialog
	 */
	private static final String name = "Adapted Rocchio Algorithm";
	@Override
	public GraphCode generateQuery(GraphCode rel, GraphCode nrel, Vector<GraphCode> vrel, Vector<GraphCode> vnrel) {
		
		GraphCode rel_new = new GraphCode();
		GraphCode nrel_new = new GraphCode();
		GraphCode lastquery = RefinementCollection.getInstance().getCurrentQuery();
		GraphCode lastquery_new = new GraphCode();
		GraphCode query = new GraphCode();
		
		Vector<String> dictionary = new Vector <String>();
		
		//Construct dictionary of resulting query GC containing features from old query, relevant and non-relevant parts.
		for (String s : lastquery.getDictionary())
		{
			if (!dictionary.contains(s))
			{
				dictionary.add(s);
			}
		}
		
		for (String s : rel.getDictionary())
		{
			if (!dictionary.contains(s))
			{
				dictionary.add(s);
			}
		}
		
		for (String s : nrel.getDictionary())
		{
			if (!dictionary.contains(s))
			{
				dictionary.add(s);
			}
		}
		
		lastquery_new.setDictionary(dictionary);
		rel_new.setDictionary(dictionary);
		nrel_new.setDictionary(dictionary);
		query.setDictionary(dictionary);
		
		// Construct normalized GC of last recently executed query
		for (String s : dictionary)
		{
			for (String t : dictionary)
			{
				int i = 0;
				
				if (lastquery.getDictionary().contains(s) && lastquery.getDictionary().contains(t))
				{
				try { i = lastquery.getEdgeValueForTerms(s, t); } catch (Exception x) {x.printStackTrace();}
				}
				if (s.equals(t))
				{
					lastquery_new.setValueForTerms(s, t, i);
				}
				else
				{
					if (!(SelectQueryCommandHistory.getInstance().getLastQueryMetric() instanceof AdaptedRocchioGraphCodeMetricQuerying))
					{
					lastquery_new.setValueForTerms(s, t, RefinementUIModel.getInstance().getRelationshipWeights()[i]);
					}
					else
					{
						lastquery_new.setValueForTerms(s, t, i);
					}
				}

				
				i = 0;
				
				//Construct normalized GC of relevant assets and parts of assets
				if (rel.getDictionary().contains(s) && rel.getDictionary().contains(t))
				{
				try { i = rel.getEdgeValueForTerms(s, t); } catch (Exception x) {x.printStackTrace();}
				}
				if (s.equals(t))
				{
				rel_new.setValueForTerms(s, t, i);
				}
				else
				{
					rel_new.setValueForTerms(s, t, RefinementUIModel.getInstance().getRelationshipWeights()[i]);
				}
			
				
				i = 0;
				
				// Construct normalized GC of non relevant parts of assets and parts of assets
				if (nrel.getDictionary().contains(s) && nrel.getDictionary().contains(t))
				{
				try { i = nrel.getEdgeValueForTerms(s, t); } catch (Exception x) {x.printStackTrace();}
				}
				if (s.equals(t))
				{
					nrel_new.setValueForTerms(s, t, i);
				}
				else
				{
					nrel_new.setValueForTerms(s, t, RefinementUIModel.getInstance().getRelationshipWeights()[i]);
				}
			}
		}
		
		
		// calculate new query's GC by weighting old query, relevant and non relevant parts with weights specified by user
		for (String s : dictionary)
		{
			for (String t : dictionary)
			{
				
				double i;
				double j;
				double k;
				double erg;
				
				i = lastquery_new.getEdgeValueForTerms(s, t);
				j = rel_new.getEdgeValueForTerms(s, t);
				k = nrel_new.getEdgeValueForTerms(s, t);
				
				erg = RefinementUIModel.getInstance().getA() * i + RefinementUIModel.getInstance().getB() * j - RefinementUIModel.getInstance().getC() * k;
				
				query.setValueForTerms(s, t, (int)(erg * 10.0));
				
			}
		}
		
		return query;
	}
	
	/**
	 * Get the name of the algorithm
	 * @return the algorithm's name
	 */
	public static String getName()
	{
		return name;
	}

}
