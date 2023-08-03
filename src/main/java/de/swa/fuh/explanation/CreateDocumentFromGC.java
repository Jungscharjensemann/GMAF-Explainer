package de.swa.fuh.explanation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;

import org.apache.commons.lang3.StringUtils;

import de.swa.gc.GraphCode;

/**
 * 
 * @author Annelie Lausch 6953476
 * 
 *         Creating a document from the information in the Graph Codes
 *
 */
public class CreateDocumentFromGC {

	private static DefaultStyledDocument document;
	private static List<String> testHeadlineFeatRel;
	private static Vector<String> features;
	private static boolean detailed = false;

	/**
	 * Document for query representation
	 * 
	 * @param gc        (query Graph Code)
	 * @param isRefined (boolean variable, true if query was refined)
	 */
	public CreateDocumentFromGC(GraphCode gc, boolean isRefined) {

		document = new DefaultStyledDocument();
		features = new Vector<String>();
		testHeadlineFeatRel = new ArrayList<>();
		List<String> objects = new ArrayList<>();

		features = gc.getDictionary();// For reading features
		Vector<String> sortedFeatures = gc.getDictionary();// For changing feature vector

		boolean refined = isRefined;
		// Variables for different relationship types between features
		boolean nextToTest = false;
		boolean belowTest = false;
		boolean consistOfTest = false;
		boolean beforeTest = false;
		boolean relatedToTest = false;
		boolean attachedToTest = false;
		boolean descriptionTest = false;
		boolean doingTest = false;
		boolean propertyTest = false;
		boolean synonymTest = false;
		boolean aggregationTest = false;
		boolean generalisationTest = false;
		List<List<String>> consistOf = new ArrayList<>();
		List<List<String>> nextTo = new ArrayList<>();
		List<List<String>> below = new ArrayList<>();
		List<List<String>> before = new ArrayList<>();
		List<List<String>> relatedTo = new ArrayList<>();
		List<List<String>> attachedTo = new ArrayList<>();
		List<List<String>> description = new ArrayList<>();
		List<List<String>> doing = new ArrayList<>();
		List<List<String>> property = new ArrayList<>();
		List<List<String>> synonym = new ArrayList<>();
		List<List<String>> aggregation = new ArrayList<>();
		List<List<String>> generalisation = new ArrayList<>();

		// Going through complete Graph Code of asset to get displayable information
		for (Enumeration<String> el = features.elements(); el.hasMoreElements();) {
			String temp = el.nextElement();
			// Identifying objects
			if (temp.contains("_")) {
				objects.add(temp);
				sortedFeatures.remove(temp);
			}
			int x = features.indexOf(temp);
			// Adding features to according relationship list
			for (int j = 0; j < features.size(); j++) {
				int value1 = gc.getValue(x, j);
				int value2 = gc.getValue(x, x);
				// Only use one half of matrix and only features with matrix value (x,x) not 2
				// (2 = child note)
				if (x != j && value2 != 2) {
					switch (value1) {
					case 1:
						consistOfTest = addElemToRelationshipList(consistOf, consistOfTest, x, j);
						break;
					case 11:
						nextToTest = addElemToRelationshipList(nextTo, nextToTest, x, j);
						break;
					case 12:
						beforeTest = addElemToRelationshipList(before, beforeTest, x, j);
						break;
					case 15:
						belowTest = addElemToRelationshipList(below, belowTest, x, j);
						break;
					case 17:
						attachedToTest = addElemToRelationshipList(attachedTo, attachedToTest, x, j);
						break;
					case 18:
						relatedToTest = addElemToRelationshipList(relatedTo, relatedToTest, x, j);
						break;
					case 19:
						doingTest = addElemToRelationshipList(doing, doingTest, x, j);
						break;
					case 20:
						propertyTest = addElemToRelationshipList(property, propertyTest, x, j);
						break;
					case 21:
						descriptionTest = addElemToRelationshipList(description, descriptionTest, x, j);
						break;
					case 50:
						synonymTest = addElemToRelationshipList(synonym, synonymTest, x, j);
						break;
					case 51:
						aggregationTest = addElemToRelationshipList(aggregation, aggregationTest, x, j);
						break;
					case 52:
						generalisationTest = addElemToRelationshipList(generalisation, generalisationTest, x, j);
						break;
					default:
						break;
					}
				}
			}
		}

		sortedFeatures.sort(null);// Sort features for display alphabetical
		// Different attributes for text display
		SimpleAttributeSet headline = new AttributeSets(Colors.getBlue(), true, 18).getSet();
		SimpleAttributeSet headline2 = new AttributeSets(Colors.getLight(), true, 14).getSet();
		SimpleAttributeSet headline3 = new AttributeSets(Colors.getLight(), true, 16).getSet();
		SimpleAttributeSet nameStyle = new AttributeSets(Colors.getBlue(), false, 14).getSet();
		SimpleAttributeSet featureStyle = new AttributeSets(Colors.getBlue(), false, 13).getSet();
		SimpleAttributeSet xStyle = new AttributeSets(Colors.getGreen(), true, 14).getSet();

		insertStringIntoDoc(document, "Presentation of query\n", headline);
		if (NamesHelper.getQueryName() != null) {
			SimpleAttributeSet picQ = new AttributeSets(NamesHelper.getQueryName()).getSet();
			insertStringIntoDoc(document, " \n", featureStyle);
			insertStringIntoDoc(document, " ", picQ);
			// Check if query was refined
			if (!refined)
				insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "  \n\n", nameStyle);
			else
				insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "_refined  \n\n", nameStyle);
		} else if (!refined)
			insertStringIntoDoc(document, "by keywords  \n\n", nameStyle);
		else
			insertStringIntoDoc(document, "by keywords_refined  \n\n", nameStyle);

		// List features
		if (!sortedFeatures.isEmpty()) {
			insertStringIntoDoc(document, "* Identified features / terms:  \n", headline3);
			for (String strFeat : sortedFeatures) {
				if (BooleanHelper.getRefineButton())
					insertStringIntoDoc(document, "X ", xStyle);
				else
					strFeat = StringUtils.normalizeSpace(strFeat);
				insertStringIntoDoc(document, strFeat + ",   ", featureStyle);
			}
		}
		// List identified objects
		if (!objects.isEmpty()) {
			insertStringIntoDoc(document, "\n\n* Identified objects: \n", headline2);
			for (String object : objects) {
				if (BooleanHelper.getRefineButton())
					insertStringIntoDoc(document, "X ", xStyle);
				insertStringIntoDoc(document, object + ",   ", featureStyle);
			}
		}
		// List relationships between features
		if (detailed) {
			insertStringIntoDoc(document, "\n\n* Relationships between features / terms: ", headline2);
			for (int i = 0; i < features.size(); i++) {
				String featHead = features.get(i);
				generateOutput(nextTo, featHead, nextToTest, i, "\n  Next to: ");
				generateOutput(below, featHead, belowTest, i, "\n  Below: ");
				generateOutput(consistOf, featHead, consistOfTest, i, "\n  Consists of: ");
				generateOutput(before, featHead, beforeTest, i, "\n  In front of: ");
				generateOutput(relatedTo, featHead, relatedToTest, i, "\n  Related to: ");
				generateOutput(attachedTo, featHead, attachedToTest, i, "\n  Attached to: ");
				generateOutput(description, featHead, descriptionTest, i, "\n  Described by: ");
				generateOutput(doing, featHead, doingTest, i, "\n  Activity: ");
				generateOutput(property, featHead, propertyTest, i, "\n  Property of: ");
				generateOutput(synonym, featHead, synonymTest, i, "\n  Synonyms: ");
				generateOutput(aggregation, featHead, aggregationTest, i, "\n  Aggregation terms: ");
				generateOutput(generalisation, featHead, generalisationTest, i, "\n  Generalisation terms: ");
			}
			detailed = false;
			insertStringIntoDoc(document, "\n\n", featureStyle);
		}
	}

	/**
	 * Document for ranking information of one or two assets compared to query
	 * 
	 * @param query        (query Graph Code)
	 * @param result       (result asset Graph Code)
	 * @param isComparison (boolean variable, true if two result assets are compared
	 *                     to query)
	 * @param isRefined    (boolean variable, true if query was refined)
	 */
	public CreateDocumentFromGC(GraphCode query, GraphCode result, boolean isComparison, boolean isRefined) {

		document = new DefaultStyledDocument();
		features = new Vector<String>();
		List<List<Object>> relations = new ArrayList<>();

		features = query.getDictionary();
		Vector<String> sortedFeatures = query.getDictionary();

		boolean refined = isRefined;
		int numberFeatures = 0;
		int numberPairs = 0;

		// Get relationship pairs with matrix value not 0 in both Graph Codes (query,
		// asset)
		for (int i = 0; i < features.size(); i++) {
			for (int j = 0; j < features.size(); j++) {
				if (i != j) {
					int value1 = result.getValue(i, j);
					int value2 = query.getValue(i, j);
					if (value1 != 0 && value2 != 0) {
						List<Object> info = new ArrayList<>();
						info.add(0, features.elementAt(i));
						info.add(1, features.elementAt(j));
						info.add(2, value1);
						info.add(3, value2);
						relations.add(info);
					}
				}
			}
		}
		// Numbers for comparison
		numberFeatures = features.size();
		numberPairs = relations.size();

		// Different attributes for text display
		SimpleAttributeSet headline = new AttributeSets(Colors.getBlue(), true, 18).getSet();
		SimpleAttributeSet headline2 = new AttributeSets(Colors.getLight(), true, 14).getSet();
		SimpleAttributeSet textStyle = new AttributeSets(Colors.getLight(), false, 14).getSet();
		SimpleAttributeSet textItalicStyle = new AttributeSets(Colors.getLight(), false, true, false, 14).getSet();
		SimpleAttributeSet numberStyle = new AttributeSets(Colors.getBlue(), true, 14).getSet();
		SimpleAttributeSet nameStyle = new AttributeSets(Colors.getBlue(), false, 14).getSet();
		SimpleAttributeSet featureStyle = new AttributeSets(Colors.getBlue(), false, 13).getSet();
		SimpleAttributeSet pairStyle = new AttributeSets(Colors.getTeal(), false, 12).getSet();
		SimpleAttributeSet typeStyle = new AttributeSets(Colors.getGreen(), false, 12).getSet();
		SimpleAttributeSet typeNStyle = new AttributeSets(Colors.getRed(), false, 12).getSet();

		insertStringIntoDoc(document, "Ranking information ", headline);

		// Check if two result assets are compared or only one asset with query
		if (!isComparison) {
			NumberHelper.setNumberFeaturesForC(numberFeatures);// For comparison of two result assets
			NumberHelper.setNumberPairsForC(numberPairs);// For comparison of two result assets
			SimpleAttributeSet picS = new AttributeSets(NamesHelper.getSelectedName()).getSet();
			insertStringIntoDoc(document, "\nExplanation of selected result asset \n", headline2);
			insertStringIntoDoc(document, " ", picS);
			insertStringIntoDoc(document, "  " + NamesHelper.getSelectedNameCut(), nameStyle);
			insertStringIntoDoc(document, "  Position in result list: " + "\n", nameStyle);
			insertStringIntoDoc(document, "\ncompared to query \n", headline2);
			if (NamesHelper.getQueryName() != null) {
				SimpleAttributeSet picQ = new AttributeSets(NamesHelper.getQueryName()).getSet();
				insertStringIntoDoc(document, " ", picQ);
				// Check for query refinement
				if (!refined)
					insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "  \n\n", nameStyle);
				else
					insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "_refined  \n\n", nameStyle);
			} else if (!refined)
				insertStringIntoDoc(document, "by keywords  \n\n", nameStyle);
			else
				insertStringIntoDoc(document, "by keywords_refined  \n\n", nameStyle);
			insertStringIntoDoc(document,
					"*****\nTo COMPARE the ranking information of this selected asset with another asset, choose the SECOND asset with RIGHT MOUSE CLICK.\n*****\n\n",
					textItalicStyle);
		}
		// Comparison of two result assets
		else {
			SimpleAttributeSet picC = new AttributeSets(NamesHelper.getChosenName()).getSet();
			SimpleAttributeSet picS = new AttributeSets(NamesHelper.getSelectedName()).getSet();
			int diffFeatures = NumberHelper.getNumberFeaturesForC() - numberFeatures;
			int diffPairs = NumberHelper.getNumberPairsForC() - numberPairs;
			insertStringIntoDoc(document, " Comparison of two result assets \n\n", headline2);
			insertStringIntoDoc(document, "FIRST asset - selected by MOUSE click: \n", headline2);
			insertStringIntoDoc(document, " ", picC);
			insertStringIntoDoc(document, "  " + NamesHelper.getChosenNameCut(), nameStyle);
			insertStringIntoDoc(document, "  Position in result list: " + "\n\n", nameStyle);
			insertStringIntoDoc(document, "SECOND asset - selected by BUTTON: \n", headline2);
			insertStringIntoDoc(document, " ", picS);
			insertStringIntoDoc(document, "  " + NamesHelper.getSelectedNameCut(), nameStyle);
			insertStringIntoDoc(document, "  Position in result list: " + "\n\n", nameStyle);
			insertStringIntoDoc(document, "The FIRST asset has ", textStyle);
			if (diffFeatures > 0)
				insertStringIntoDoc(document, "  " + diffFeatures + " less  ", numberStyle);
			if (diffFeatures < 0)
				insertStringIntoDoc(document, "  " + Math.abs(diffFeatures) + " more  ", numberStyle);
			if (diffFeatures == 0)
				insertStringIntoDoc(document, " the same number of  ", numberStyle);
			insertStringIntoDoc(document, "features in common with the query in comparison to the SECOND asset.\n\n",
					textStyle);
			insertStringIntoDoc(document, "The FIRST asset has ", textStyle);
			if (diffPairs > 0)
				insertStringIntoDoc(document, "  " + diffPairs + " less  ", numberStyle);
			if (diffPairs < 0)
				insertStringIntoDoc(document, "  " + Math.abs(diffPairs) + " more  ", numberStyle);
			if (diffPairs == 0)
				insertStringIntoDoc(document, "  the same number of  ", numberStyle);
			insertStringIntoDoc(document,
					"common pairs of relationship types with the query in comparison to the SECOND asset.\n\n\n",
					textStyle);
		}
		// Display ranking information of one result asset
		insertStringIntoDoc(document, "Ranking information of the (first) asset compared to the query: \n", headline2);
		if (isComparison) {
			if (NamesHelper.getQueryName() != null) {
				SimpleAttributeSet picQ = new AttributeSets(NamesHelper.getQueryName()).getSet();
				insertStringIntoDoc(document, " ", picQ);
				if (!refined)
					insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "  \n\n", nameStyle);
				else
					insertStringIntoDoc(document, "  " + NamesHelper.getQueryNameCut() + "_refined  \n\n", nameStyle);
			} else if (!refined)
				insertStringIntoDoc(document, "by keywords  \n\n", nameStyle);
			else
				insertStringIntoDoc(document, "by keywords_refined  \n\n", nameStyle);
		}
		insertStringIntoDoc(document, "Number of common features: ", textStyle);
		insertStringIntoDoc(document, numberFeatures + "\n", numberStyle);
		insertStringIntoDoc(document, "Number of common relationship pairs: ", textStyle);
		insertStringIntoDoc(document, numberPairs + "\n", numberStyle);
		// Common features of query and result asset
		sortedFeatures.sort(null);
		if (!sortedFeatures.isEmpty()) {
			insertStringIntoDoc(document, "\n* Common features / terms: \n", headline2);
			for (String strFeat : sortedFeatures) {
				insertStringIntoDoc(document, strFeat + ",   ", featureStyle);
			}
		}
		// Relationship pairs, if type is equal text in green, different type text in
		// red
		if (!relations.isEmpty()) {
			insertStringIntoDoc(document, "\n\n* Relationship pairs: \n", headline2);
			for (List<Object> object : relations) {
				String type = "";
				insertStringIntoDoc(document, "Pair: ", pairStyle);
				insertStringIntoDoc(document, object.get(0) + ", " + object.get(1), featureStyle);
				if (object.get(2) == object.get(3)) {
					type = "same relationship type";
					insertStringIntoDoc(document, " - " + type + " (" + getType(object.get(2)) + ")\n", typeStyle);
				} else {
					type = "different relationship type";
					insertStringIntoDoc(document,
							" - " + type + " (" + getType(object.get(2)) + ", " + getType(object.get(3)) + ")\n",
							typeNStyle);
				}
			}
			insertStringIntoDoc(document, "\n\n", featureStyle);
		}
	}

	/**
	 * To insert string at the end of document with style (text properties)
	 * 
	 * @param doc   (DefaultStyledDocument in which strings are inserted)
	 * @param text  (String to insert)
	 * @param style (SimpleAttributeSet for text formatting properties)
	 */
	private static void insertStringIntoDoc(DefaultStyledDocument doc, String text, SimpleAttributeSet style) {
		try {
			doc.insertString(doc.getLength(), text, style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Displaying the relationship between features
	 * 
	 * @param listRelationship (relationship list)
	 * @param featHead         (name of feature x for which relationships are
	 *                         displayed)
	 * @param test             (boolean variable to check of corresponding
	 *                         relationship list is not empty)
	 * @param increment        (equals position of feature x in dictionary for
	 *                         getting correct list of related features)
	 * @param head             (name of relationship type)
	 */
	private static void generateOutput(List<List<String>> listRelationship, String featHead, boolean test,
			int increment, String head) {
		SimpleAttributeSet headline4 = new AttributeSets(Colors.getLight(), true, false, true, 13).getSet();
		SimpleAttributeSet relHeadStyle = new AttributeSets(Colors.getTeal(), true, 12).getSet();
		SimpleAttributeSet relationshipStyle = new AttributeSets(Colors.getBlue(), false, 12).getSet();
		if (test && !listRelationship.get(increment).isEmpty()) {
			if (!testHeadlineFeatRel.contains(featHead)) {
				insertStringIntoDoc(document, "\n" + StringUtils.normalizeSpace(featHead) + ":", headline4);
				testHeadlineFeatRel.add(featHead);
			}
			insertStringIntoDoc(document, head, relHeadStyle);
			String output = "";
			for (String str : listRelationship.get(increment)) {
				if (str != null) {
					output = output + " * " + str;
				}
			}
			insertStringIntoDoc(document, output, relationshipStyle);
		}
	}

	/**
	 * Adding new elements to the according relationship list
	 * 
	 * @param listRelationship (relationship list)
	 * @param test             (boolean variable to check of corresponding
	 *                         relationship list is not empty)
	 * @param incrementX       (position of feature x in dictionary)
	 * @param incrementJ       (for adding related features j of feature x)
	 * @return
	 */
	private static boolean addElemToRelationshipList(List<List<String>> listRelationship, boolean test, int incrementX,
			int incrementJ) {
		if (!test) {
			for (int i = 0; i < features.size(); i++) {
				listRelationship.add(new ArrayList<>());
			}
			test = true;
		}
		if (test) {
			String feature = StringUtils.normalizeSpace(features.elementAt(incrementJ));
			listRelationship.get(incrementX).add(feature);
		}
		detailed = true;
		return test;
	}

	/**
	 * Description of relationship type according to value in Graph Code matrix
	 * 
	 * @param object (equals relationship number in matrix, cast to integer for
	 *               switch-case function)
	 * @return
	 */
	private static String getType(Object object) {
		String type = "";
		switch ((int) object) {
		case 1:
			type = "consistOf";
			break;
		case 11:
			type = "nextTo";
			break;
		case 12:
			type = "before";
			break;
		case 13:
			type = "behind";
			break;
		case 14:
			type = "above";
			break;
		case 15:
			type = "below";
			break;
		case 16:
			type = "partOf";
			break;
		case 17:
			type = "attachedTo";
			break;
		case 18:
			type = "relatedTo";
			break;
		case 19:
			type = "doing";
			break;
		case 20:
			type = "property";
			break;
		case 21:
			type = "description";
			break;
		case 50:
			type = "synonym";
			break;
		case 51:
			type = "aggregation";
			break;
		case 52:
			type = "generalisation";
			break;
		default:
			break;
		}
		return type;
	}

	public DefaultStyledDocument getDocument() {
		return document;
	}

}
