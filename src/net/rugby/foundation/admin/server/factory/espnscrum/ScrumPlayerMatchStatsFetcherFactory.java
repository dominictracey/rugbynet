package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.EspnPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.ScrumPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;

public class ScrumPlayerMatchStatsFetcherFactory implements IPlayerMatchStatsFetcherFactory {

	private IPlayerMatchStatsFactory pmsf;
	private IUrlCacher urlCache;
	private ICompetitionFactory cf;
	private IRoundFactory rf;
	private IConfigurationFactory ccf;

	@Inject
	public void setFactories(IPlayerMatchStatsFactory pmsf, IUrlCacher urlCache, ICompetitionFactory cf, IRoundFactory rf, IConfigurationFactory ccf) {
		this.pmsf = pmsf;
		this.urlCache = urlCache;
		this.cf = cf;
		this.rf = rf;
		this.ccf = ccf;
	}
	
	@Override
	public IPlayerMatchStatsFetcher getResultFetcher(IPlayer player, IMatchGroup match, Home_or_Visitor side, Integer slot, String url) {
			if (player != null && match != null && side != null && slot != null) {
				//Logger.getLogger("Result Fetcher").log(Level.SEVERE, "Unrecognized compId specified: " + sourceCompID);
				//IPlayerMatchStatsFetcher fetcher = new ScrumPlayerMatchStatsFetcher(pmsf, urlCache);
				IPlayerMatchStatsFetcher fetcher = new EspnPlayerMatchStatsFetcher(ccf, rf, cf);
				fetcher.setPlayer(player);
				fetcher.setMatch(match);
				fetcher.setHov(side);
				fetcher.setSlot(slot);
				fetcher.setUrl(url);
				return fetcher;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Invalid player, match, hov, slot or url passed in");
				return null;
		}
	}
}
