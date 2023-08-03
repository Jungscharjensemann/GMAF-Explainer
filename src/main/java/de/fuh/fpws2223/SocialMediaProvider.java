package de.fuh.fpws2223;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

public abstract class SocialMediaProvider {

	protected abstract String getPostId(File f);

	protected abstract String getAuthParamsForRequest();

	protected abstract HttpGet buildHttpGet(String id, String authKey);

	private String getJsonFromResponse(HttpResponse httpResponse) throws Exception {

		String jsonString = "";

		System.out.println("----------------------------------------");
		System.out.println(httpResponse.getStatusLine());
		System.out.println("----------------------------------------");

		HttpEntity entity = httpResponse.getEntity();

		byte[] buffer = new byte[1024];
		if (entity != null) {
			InputStream inputStream = entity.getContent();
			try {
				int bytesRead = 0;
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				while ((bytesRead = bis.read(buffer)) != -1) {
					jsonString += new String(buffer, 0, bytesRead);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					inputStream.close();
				} catch (Exception ignore) {
				}
			}
		}
		return jsonString;
	}

	public String execute(File f) {

		String jsonString = "";

		String id = getPostId(f);
		String authKey = getAuthParamsForRequest();

		CloseableHttpClient client = HttpClientBuilder.create().build();
		try {
			HttpGet httpGetRequest = buildHttpGet(id, authKey);
			HttpResponse httpResponse = client.execute(httpGetRequest);

			jsonString = getJsonFromResponse(httpResponse);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return jsonString;
	}
}
