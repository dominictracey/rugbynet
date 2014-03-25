package net.rugby.foundation.topten.server.utilities;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public class SocialMediaDirector implements ISocialMediaDirector {

	List<IPromotionHandler> promoters = new ArrayList<IPromotionHandler>();
	
	public SocialMediaDirector(){
		promoters.add(new FacebookPromoter());
		promoters.add(new GooglePromoter());
		promoters.add(new TwitterPromoter());
		
	}
	
	@Override
	public boolean PromoteTopTenList(ITopTenList ttl) {
		String body = "foo";
		body += "bar\n" + "\n";
		for (IPromotionHandler i: promoters){
			body += i.process(ttl);
		}		
		AdminEmailer emailer = new AdminEmailer();
		emailer.setMessage(body);
		emailer.setSubject(ttl.getTitle());
		return true;
	}

}
