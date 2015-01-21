package net.rugby.foundation.topten.server.utilities;

import java.net.URLEncoder;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public class GooglePromoter implements IPromotionHandler {

	String retval = "<p>******** GOOGLE **********</p>\n";
	@Override
	public String process(ITopTenList ttl, String channel) {

		String guid = "";
		if (ttl.getFeatureGuid() != null) {
			guid = ttl.getFeatureGuid();
		} else {
			guid = ttl.getGuid();
		}
		
		String URL = URLEncoder.encode("http://www.rugby.net/s/" + guid);
		retval += "<p><a href=\"https://www.google.com/webmasters/tools/submit-url?urlnt=" + URL +"\">Add to Google</a></p>\n";;


		return retval;
	}

	@Override
	public String process(ITopTenList ttl) {
		return process(ttl, "");
	}
}
