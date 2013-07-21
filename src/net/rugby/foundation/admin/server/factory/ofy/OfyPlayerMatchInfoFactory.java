package net.rugby.foundation.admin.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.PlayerMatchInfo;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;

public class OfyPlayerMatchInfoFactory implements IPlayerMatchInfoFactory {

	private IPlayerMatchRatingFactory pmrf;
	private IPlayerMatchStatsFactory pmsf;
	private IMatchGroupFactory mf;
	private ICompetitionFactory cf;
	private IMatchRatingEngineSchemaFactory sf;
	
	@Inject
	public OfyPlayerMatchInfoFactory(IPlayerMatchStatsFactory pmsf, IPlayerMatchRatingFactory pmrf, IMatchGroupFactory mf,
			ICompetitionFactory cf, IMatchRatingEngineSchemaFactory sf) {
		this.pmrf = pmrf;
		this.pmsf = pmsf;
		this.mf = mf;
		this.cf = cf;
		this.sf = sf;
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
	public List<IPlayerMatchInfo> getForMatch(Long matchId, IMatchRatingEngineSchema schema) {
		try {
			List<IPlayerMatchInfo> list = new ArrayList<IPlayerMatchInfo>();
			mf.setId(matchId);
			IMatchGroup m = mf.getGame();

			if (m != null) {
				List<? extends IPlayerMatchStats> pmsl = pmsf.getByMatchId(matchId);
				for (IPlayerMatchStats pms : pmsl) {
					if (pms instanceof ScrumPlayerMatchStats) {
						IPlayerMatchRating pmr = pmrf.get(pms,schema);
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
	public IPlayerMatchInfo getForPlayerMatchStats(Long pmsId, IMatchRatingEngineSchema schema) {
		
		if (schema == null) {
			schema = sf.getDefault();
		}
		
		IPlayerMatchStats pms = pmsf.getById(pmsId);
		IPlayerMatchRating pmr = null;
		if (pms instanceof ScrumPlayerMatchStats) {
			pmr = pmrf.get(pms,schema);
		}
		
		return new PlayerMatchInfo(pms,pmr);
	}


	@Override
	public List<IPlayerMatchInfo> query(Long compId, Long roundId,
			position posi, Long countryId, Long teamId, Long schemaId) {
		try {
			List<IPlayerMatchInfo> list = new ArrayList<IPlayerMatchInfo>();

			List<Long> matches = new ArrayList<Long	>();

			if (schemaId == null) {
				schemaId = sf.getDefault().getId();
			}
			
			// so first we need a list of matches
			ICompetition comp = null;
			if (compId != null) {
				cf.setId(compId);
				comp = cf.getCompetition();
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "compID can't be null in call to query");
				return null; 
			}

			if (roundId != null) {
				for (IRound r : comp.getRounds()) {
					if (r.getId().equals(roundId)) {
						matches = r.getMatchIDs();
						break;
					}
				}
			} else {
				// all the matches in the comp!
				for (IRound r : comp.getRounds()) {
					matches.addAll(r.getMatchIDs());
				}
			}
			
			// now matches has all the matches
			List<IPlayerMatchStats> statsList = pmsf.query(matches, posi, countryId, teamId);
			
			for (IPlayerMatchStats stats : statsList) {
				IPlayerMatchRating pmr = null;
				if (stats instanceof ScrumPlayerMatchStats) {
					pmr = pmrf.get(stats,sf.getById(schemaId));
				}
				
				list.add(new PlayerMatchInfo(stats,pmr));
			}
			
			return list;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "query: " + e.getMessage());
			return null;
		}
	}
}
