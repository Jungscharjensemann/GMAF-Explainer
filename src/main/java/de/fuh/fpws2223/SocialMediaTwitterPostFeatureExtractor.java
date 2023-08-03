package de.fuh.fpws2223;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class SocialMediaTwitterPostFeatureExtractor extends SocialMediaPostExtractor {

	@Override
	public void createFeatureGraph(String jsonPost, MMFG fg) {

		try {
			JsonNode result = new ObjectMapper().readTree(jsonPost);

			Node root = new Node("twitter-post", fg);
			buildNodes(result, root, fg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
