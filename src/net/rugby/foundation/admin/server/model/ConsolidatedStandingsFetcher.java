package net.rugby.foundation.admin.server.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;

public class ConsolidatedStandingsFetcher extends ScrumHeinekenStandingsFetcher {

	public ConsolidatedStandingsFetcher(IStandingFactory sf) {
		super(sf);

	}

	@Override
	public IStanding getStandingForTeam(ITeamGroup team) {

		assert (comp != null);
		assert (round != null);
		assert (url != null);
		assert (urlCacher != null);

		try {
			urlCacher.setUrl(url);

			if (standingTablesList == null) {
				processStandingTablesList();
			}

			IStanding standing = standingFetcher.create();
			standing.setRound(round);
			standing.setRoundId(round.getId());

			standing.setTeam(team);
			standing.setTeamId(team.getId());

			searchTeamStanding(standing);
			return standing;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected void processStandingTablesList() {

	}
	protected void searchTeamStanding(IStanding standing) {
	}

}
