package de.fuh.fpws2223;

import java.util.Iterator;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public abstract class SocialMediaPostExtractor {
	public abstract void createFeatureGraph(String jsonPost, MMFG fg);

	protected void buildNodes(JsonNode jsonNode, Node localRoot, MMFG fg) {

		if (jsonNode.isObject()) {
			ObjectNode objectNode = (ObjectNode) jsonNode;
			Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();

			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> entry = iter.next();
				Node nodeName = new Node(entry.getKey(), fg);
				localRoot.addChildNode(nodeName);

				if (entry.getValue().isValueNode()) {
					ValueNode valueNode = (ValueNode) entry.getValue();
					Node n = new Node(valueNode.asText(), fg);
					nodeName.addChildNode(n);
				} else
					buildNodes(entry.getValue(), nodeName, fg);
			}
		} else if (jsonNode.isArray()) {
			ArrayNode arrayNode = (ArrayNode) jsonNode;

			for (int i = 0; i < arrayNode.size(); i++) {
				buildNodes(arrayNode.get(i), localRoot, fg);
			}
		}
	}
}
