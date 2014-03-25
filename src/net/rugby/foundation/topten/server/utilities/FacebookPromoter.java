package net.rugby.foundation.topten.server.utilities;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public class FacebookPromoter implements IPromotionHandler {

	@Override
	public String process(ITopTenList ttl) {
		String retval = "******** FACEBOOK ********\n";
		{
			String URL = "http://www.rugby.net/fb/topten.html?listId="+ttl.getId() + "\"";
			retval += "<a href=\"http://www.facebook.com/sharer.php?src=sp&u="+ URL +">Share on FB</a>\n";

		}
		return retval;
	}
}
