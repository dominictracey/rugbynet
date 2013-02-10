package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.PlayerMatchInfo;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

public class OfyPlayerMatchInfoFactory implements IPlayerMatchInfoFactory {

	private Objectify ofy;
	private IPlayerMatchRatingFactory pmrf;
	private IPlayerMatchStatsFactory pmsf;
	private IMatchGroupFactory mf;
	
	@Inject
	public OfyPlayerMatchInfoFactory(IPlayerMatchStatsFactory pmsf, IPlayerMatchRatingFactory pmrf, IMatchGroupFactory mf) {
		this.pmrf = pmrf;
		this.pmsf = pmsf;
		this.mf = mf;
		this.ofy = DataStoreFactory.getOfy();
	}


	@Override
	public IPlayerMatchInfo get(Long id) {
		//currently not stored in db
		return null;
	}

	@Override
	public List<IPlayerMatchInfo> getForComp(Long playerId, Long compId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IPlayerMatchInfo> getForMatch(Long matchId) {
		try {
			List<IPlayerMatchInfo> list = new ArrayList<IPlayerMatchInfo>();
			mf.setId(matchId);
			IMatchGroup m = mf.getGame();

			if (m != null) {
				List<? extends IPlayerMatchStats> pmsl = pmsf.getByMatchId(matchId);
				for (IPlayerMatchStats pms : pmsl) {
					if (pms instanceof ScrumPlayerMatchStats) {
						IPlayerMatchRating pmr = pmrf.get(pms.getPlayerId(),pms.getId());
						list.add(new PlayerMatchInfo(pms,pmr));
					}
				}
			}

			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForMatch: " + e.getMessage());
			return null;
		}
	}

	@Override
	public IPlayerMatchInfo getForPlayerMatchStats(Long pmsId) {
		
		IPlayerMatchStats pms = pmsf.getById(pmsId);
		IPlayerMatchRating pmr = null;
		if (pms instanceof ScrumPlayerMatchStats) {
			pmr = pmrf.get(pms.getPlayerId(),pms.getId());
		}
		
		return new PlayerMatchInfo(pms,pmr);
	}
}
