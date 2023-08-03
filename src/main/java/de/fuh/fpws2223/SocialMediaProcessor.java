package de.fuh.fpws2223;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;

import de.swa.gmaf.plugin.GMAF_Plugin;
import de.swa.mmfg.MMFG;
import de.swa.mmfg.Node;

public class SocialMediaProcessor implements GMAF_Plugin {

	final static private String EXTENSION_YOUTUBE = "YOUTUBE";
	final static private String EXTENSION_TWITTER = "TWITTER";

	private Hashtable<SocialMediaPlatform, SocialMediaProvider> socialMediaProvider = new Hashtable<SocialMediaPlatform, SocialMediaProvider>();
	private Hashtable<SocialMediaPlatform, SocialMediaPostExtractor> socialMediaPostExtructor = new Hashtable<SocialMediaPlatform, SocialMediaPostExtractor>();

	private MMFG socialMediaMMFG = new MMFG();

	public SocialMediaProcessor() {
		init();
	}

	private SocialMediaPlatform getSocialMediaPlatformFromFileName(String fileName) {

		SocialMediaPlatform platform = SocialMediaPlatform.UNDEF;

		int index = fileName.lastIndexOf(".");
		if (index >= 0) {
			fileName = fileName.substring(index + 1);
		}

		if (fileName.equalsIgnoreCase(EXTENSION_YOUTUBE)) {
			platform = SocialMediaPlatform.YOUTUBE;
		} else if (fileName.equalsIgnoreCase(EXTENSION_TWITTER)) {
			platform = SocialMediaPlatform.TWITTER;
		}

		return platform;
	}

	private void init() {

		// create YouTube provider and extractor
		SocialMediaProvider youtubeProvider = new SocialMediaYouTubeProvider();
		socialMediaProvider.put(SocialMediaPlatform.YOUTUBE, youtubeProvider);

		SocialMediaPostExtractor youtubePostExtractor = new SocialMediaYouTubePostFeatureExtractor();
		socialMediaPostExtructor.put(SocialMediaPlatform.YOUTUBE, youtubePostExtractor);

		// create YouTube provider and extractor
		SocialMediaProvider twitterProvider = new SocialMediaTwitterProvider();
		socialMediaProvider.put(SocialMediaPlatform.TWITTER, twitterProvider);

		SocialMediaPostExtractor twitterPostExtractor = new SocialMediaTwitterPostFeatureExtractor();
		socialMediaPostExtructor.put(SocialMediaPlatform.TWITTER, twitterPostExtractor);

	}

	
	public boolean canProcess(String extension) {
		if (getSocialMediaPlatformFromFileName(extension.substring(1)) == SocialMediaPlatform.UNDEF)
			return false;
		return true;
	}

	
	public Vector<Node> getDetectedNodes() {
		return socialMediaMMFG.getNodes();
	}

	
	public boolean isGeneralPlugin() {
		return false;
	}


	public void process(URL url, File file, byte[] stream, MMFG mmfg) {

		SocialMediaPlatform platform = getSocialMediaPlatformFromFileName(file.getName());

		if (platform == SocialMediaPlatform.UNDEF)
			return;

		if (!socialMediaProvider.containsKey(platform))
			return;

		if (!socialMediaPostExtructor.containsKey(platform))
			return;

		SocialMediaProvider provider = this.socialMediaProvider.get(platform);
		SocialMediaPostExtractor extractor = this.socialMediaPostExtructor.get(platform);
		String jsonPost = provider.execute(file);
		extractor.createFeatureGraph(jsonPost, mmfg);

		socialMediaMMFG = mmfg;

	}


	public boolean providesRecoursiveData() {
		// TODO Auto-generated method stub
		return false;
	}

}
