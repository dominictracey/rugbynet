package net.rugby.foundation.topten.server.utilities;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public class GooglePromoter implements IPromotionHandler {

	@Override
	public String process(ITopTenList ttl) {
		String retval = "******** GOOGLE **********\n";
		{
			String URL = "http://www.rugby.net/fb/topten.html?listId="+ttl.getId() + "\"";
			retval += "<a href=\"https://www.google.com/webmasters/tools/submit-url?urlnt=" + URL +">Add to Google</a>\n";;
		}
	
		return retval;
	}
}
