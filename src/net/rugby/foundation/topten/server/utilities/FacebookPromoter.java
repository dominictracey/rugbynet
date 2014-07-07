package net.rugby.foundation.topten.server.utilities;

import java.net.URLEncoder;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public class FacebookPromoter implements IPromotionHandler {

	@Override
	public String process(ITopTenList ttl) {
		String retval = "<p>******** FACEBOOK ********</p>\n";
		{
			String URL = URLEncoder.encode("http://www.rugby.net/fb/topten.html?listId="+ttl.getId() + "#listId="+ttl.getId());
			retval += "<p><a href=\"http://www.facebook.com/sharer.php?src=sp&u="+ URL +"\">Share on FB</a></p>\n";

		}
		return retval;
	}
}
