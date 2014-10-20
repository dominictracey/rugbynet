package net.rugby.foundation.topten.server.utilities;


import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class SocialMediaDirector implements ISocialMediaDirector {

	List<IPromotionHandler> promoters = new ArrayList<IPromotionHandler>();
	
	@Inject
	public SocialMediaDirector(ITeamGroupFactory tgf, ITopTenListFactory ttlf, IConfigurationFactory ccf){
		promoters.add(new FacebookPromoter());
		promoters.add(new GooglePromoter());
		promoters.add(new TwitterPromoter(tgf, ttlf, ccf));
		
	}
	
	@Override
	public boolean PromoteTopTenList(ITopTenList ttl) {
		String body = "<html>";
		body += "<body>\n";
		body += "<p><a href=\"http://www.rugby.net/Admin.html#PortalPlace:queryId="+ ttl.getQueryId()+ "\"" + ">Query URL</a>\n" + "</p>\n";
		body += "<p><a href=\"http://www.rugby.net/#List:listId="+ ttl.getId()+ "\"" + ">" + ttl.getTitle() + "</a>\n" + "</p>\n";
		for (IPromotionHandler i: promoters){
			body += i.process(ttl);
		}
		int j = 1;
		body += "<br><hr>";
		for (ITopTenItem i: ttl.getList()) {
			body += j++ + ". " + i.getPlayer().getDisplayName() + " (" + i.getRating() + ")<br/>";
		}
		body += "</body></html>";
		AdminEmailer emailer = new AdminEmailer();
		emailer.setMessage(body);
		emailer.setSubject(ttl.getTitle());
		emailer.send();
		return true;
	}

}
