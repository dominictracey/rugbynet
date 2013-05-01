package net.rugby.foundation.admin.server.factory.espnscrum;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.ScrumPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;

public class ScrumPlayerMatchStatsFetcherFactory implements IPlayerMatchStatsFetcherFactory {

	private IPlayerMatchStatsFactory pmsf;

	@Inject
	public void setFactories(IPlayerMatchStatsFactory pmsf) {
		this.pmsf = pmsf;
	}
	
	@Override
	public IPlayerMatchStatsFetcher getResultFetcher(IPlayer player, IMatchGroup match, Home_or_Visitor side, Integer slot, String url) {
			if (player != null && match != null && side != null && slot != null && url != null) {
				//Logger.getLogger("Result Fetcher").log(Level.SEVERE, "Unrecognized compId specified: " + sourceCompID);
				IPlayerMatchStatsFetcher fetcher = new ScrumPlayerMatchStatsFetcher(pmsf);
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
