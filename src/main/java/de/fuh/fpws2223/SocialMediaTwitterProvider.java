package de.fuh.fpws2223;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

public class SocialMediaTwitterProvider extends SocialMediaProvider {

	final static private String ENDPOINT = "https://api.twitter.com/2/tweets/";
	private final static String ENVIRONMENT_PARMATER_NAME = "TWITTER_API_KEY";
	private String twitterApiKey = null;

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

		return postId;
	}

	private String getIdFromLink(String link) {

		/*
		 * https://twitter.com/axlrosedaily/status/1588580886475608064?s=20&t=
		 * o88MtLGhXdE8aOI-UyCHvQ
		 * https://twitter.com/axlrosedaily/status/1588580886475608064
		 */

		int index = link.lastIndexOf("/");
		String id = link.substring(index + 1);

		int len = 0;
		do {
			len = id.length();
			id = cutTail(id, "?");
			id = cutTail(id, "&");
		} while (len != id.length());

		return id;
	}

	private String cutTail(String text, String search) {

		int index = text.indexOf(search);

		if (index >= 0) {
			text = text.substring(0, index);
		}

		return text;
	}

	@Override
	public String getAuthParamsForRequest() {
		twitterApiKey = System.getenv(ENVIRONMENT_PARMATER_NAME);
		return twitterApiKey;
	}

	@Override
	public HttpGet buildHttpGet(String id, String authKey) {

		HttpGet httpGet = new HttpGet(ENDPOINT + id);

		try {
			List nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tweet.fields", "public_metrics"));
			nameValuePairs.add(new BasicNameValuePair("expansions", "attachments.media_keys"));
			nameValuePairs.add(new BasicNameValuePair("media.fields",
					"duration_ms,height,media_key,preview_image_url,public_metrics,type,url,width,alt_text"));
			URI uri = new URIBuilder(httpGet.getURI()).addParameters(nameValuePairs).build();
			((HttpRequestBase) httpGet).setURI(uri);
			httpGet.addHeader("Authorization", authKey);
			httpGet.addHeader("Cookie", "guest_id=v1%3A166677617579739668");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpGet;
	}

}
