package net.rugby.foundation.topten.server.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.TeamGroup;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.inject.Inject;
import com.google.inject.Injector;



@Api(
		name = "sidenav",
		version = "v1",
		clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.IOS_CLIENT_ID},
		audiences = {Ids.ANDROID_AUDIENCE}
		)
public class SideNavV1 {
	private IConfigurationFactory cf;
	private IRatingSeriesFactory rsf;

	//	@Inject
	//	public SideNavV1(ICompetitionFactory cf) {
	//		this.cf = cf;
	//	}
	private static Injector injector = null;

	public SideNavV1() {
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}
		this.cf = injector.getInstance(IConfigurationFactory.class);
		this.rsf = injector.getInstance(IRatingSeriesFactory.class);
	}

	@ApiMethod(name = "item.getAll", path="/items/", httpMethod = "GET")
	public List<SideNavItem> getallitem() {
		List<Long> comps = cf.get().getCompsForClient();
		Map<Long, String> nameMap = cf.get().getCompetitionMap();
		List<SideNavItem> sideNavItems = new ArrayList<SideNavItem>();
		for(Long compId : comps) {
			SideNavItem newItem = new SideNavItem();
			newItem.id = compId;
			newItem.name = nameMap.get(compId);
			//Iterate rating modes
			List<RatingModeItem> ratingModeItems = new ArrayList<RatingModeItem>();
			Map<RatingMode, Long> rmodes = rsf.getModesForComp(compId);
			for (Map.Entry<RatingMode, Long> entry : rmodes.entrySet())
			{
			    RatingMode rmode = entry.getKey();
			    String menuName = rmode.getMenuName();
			    Long ratingId = entry.getValue();
			    RatingModeItem thisRatingMode = new RatingModeItem();
			    thisRatingMode.id = ratingId;
			    thisRatingMode.name = menuName;
			    ratingModeItems.add(thisRatingMode);
			}
			newItem.ratingModes = ratingModeItems;
			sideNavItems.add(newItem);
		}
		return sideNavItems;
	}

}
