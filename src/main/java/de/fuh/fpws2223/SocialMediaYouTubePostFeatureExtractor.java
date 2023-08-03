package de.fuh.fpws2223;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class SocialMediaYouTubePostFeatureExtractor extends SocialMediaPostExtractor {

	@Override
	public void createFeatureGraph(String jsonPost, MMFG fg) {

		try {
			JsonNode youtubePostSearchResult = new ObjectMapper().readTree(jsonPost);
			JsonNode youtubePost = youtubePostSearchResult.get("items").get(0);

			Node root = new Node("youtube-post", fg);
			buildNodes(youtubePost, root, fg);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
