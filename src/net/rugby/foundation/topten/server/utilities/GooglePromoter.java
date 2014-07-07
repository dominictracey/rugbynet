package net.rugby.foundation.topten.server.utilities;

import java.net.URLEncoder;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public class GooglePromoter implements IPromotionHandler {

	@Override
	public String process(ITopTenList ttl) {
		String retval = "<p>******** GOOGLE **********</p>\n";
		{
			String URL = URLEncoder.encode("http://www.rugby.net/fb/topten.html?listId="+ttl.getId());
			retval += "<p><a href=\"https://www.google.com/webmasters/tools/submit-url?urlnt=" + URL +"\">Add to Google</a></p>\n";;
		}
	
		return retval;
	}
}
