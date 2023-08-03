package de.swa.gmaf;

import javax.xml.ws.Endpoint;

import de.swa.gmaf.api.GMAF_Facade_SOAPImpl;
import de.swa.gmaf.api.GMAF_UI_FacadeImpl;
import de.swa.ui.AutoProcessThread;
import de.swa.ui.Configuration;

public class Service {
	public static void main(String[] args) {
		Service.start();
	}

	public static void start() {
		// if auto processing is activated, check for MMFGs
		if (Configuration.getInstance().isAutoProcess()) {
			AutoProcessThread at = new AutoProcessThread();
			at.setDaemon(true);
			at.start();
		}

		String api = "http://" + Configuration.getInstance().getServerName() + ":"
				+ Configuration.getInstance().getServerPort() + "/" + Configuration.getInstance().getContext()
				+ "/gmafApi";
		String ui = "http://" + Configuration.getInstance().getServerName() + ":"
				+ Configuration.getInstance().getServerPort() + "/" + Configuration.getInstance().getContext()
				+ "/gmafUI";

		String restApi = "http://" + Configuration.getInstance().getServerName() + ":"
				+ Configuration.getInstance().getRestServicePort() + "/" + Configuration.getInstance().getContext()
				+ "/gmafApi";

//		JdkHttpServerFactory.createHttpServer(URI.create(restApi), new GMAF_Facade_RESTImpl());

		System.out.println("GMAF SOAP API@: " + api);
		System.out.println("GMAF SOAP UI @: " + ui);
//		System.out.println("GMAF REST API@: " + restApi);
		Endpoint.publish(api, new GMAF_Facade_SOAPImpl());
		Endpoint.publish(ui, new GMAF_UI_FacadeImpl());
		
		System.out.println("GMAF Service running for collection: " + Configuration.getInstance().getCollectionName());
	}
}
