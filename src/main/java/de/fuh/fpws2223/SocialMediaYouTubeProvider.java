package de.fuh.fpws2223;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.http.client.methods.HttpGet;

public class SocialMediaYouTubeProvider extends SocialMediaProvider {

	private final static String ENDPOINT = "https://youtube.googleapis.com/youtube/v3/videos?part=snippet%2CcontentDetails%2Cstatistics";
	private final static String ENVIRONMENT_PARMATER_NAME = "YOUTUBE_API_KEY";
	

	@Override
	protected String getPostId(File f) {
		String postId = null;

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(f));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();

			String link = sb.toString();

			postId = getIdFromLink(link);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return "&id=" + postId;
	}

	private String getIdFromLink(String link) {
		String id = null;

		/*
		 * mögliche Link-Formen https://youtu.be/0lmj1Bv0KYc
		 * https://www.youtube.com/watch?v=0lmj1Bv0KYc
		 */
		if (link.contains("v=")) {
			int index = link.lastIndexOf("v=") + 2;
			id = link.substring(index);
		} else if (link.lastIndexOf("/") >= 0) {
			int index = link.lastIndexOf("/") + 1;
			id = link.substring(index);
		}

		return id;
	}
	
	@Override
	public HttpGet buildHttpGet(String id, String authKey) {

		HttpGet httpGet = new HttpGet(ENDPOINT + id + authKey);
		return httpGet;
	}

	@Override
	protected String getAuthParamsForRequest() {
		String youtubeApiKey = System.getenv(ENVIRONMENT_PARMATER_NAME);
		return "&key=" + youtubeApiKey;
	}



}
