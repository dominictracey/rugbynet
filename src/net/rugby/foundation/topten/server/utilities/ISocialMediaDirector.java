package net.rugby.foundation.topten.server.utilities;

import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface ISocialMediaDirector {

	boolean PromoteTopTenList(ITopTenList ttl);
	
}
