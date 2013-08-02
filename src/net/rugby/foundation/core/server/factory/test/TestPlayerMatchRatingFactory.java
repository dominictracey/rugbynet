package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.PlayerMatchRating;

public class TestPlayerMatchRatingFactory implements IPlayerMatchRatingFactory {

	private IPlayerMatchStatsFactory pmsf;
	private IMatchGroupFactory mf;
	private IPlayerFactory pf;
	
	@Inject
	TestPlayerMatchRatingFactory(IPlayerMatchStatsFactory pmsf, IMatchGroupFactory mgf, IPlayerFactory pf ) {
		this.pmsf = pmsf;
		this.mf = mgf;
		this.pf = pf;
	}
	
	@Override
	public IPlayerMatchRating get(Long id) {
		if (id.equals(2000L)) {
			mf.setId(300L);
			return new PlayerMatchRating(723, pf.getById(9001014L), (IGroup)mf.getGame(),
					new ScrumMatchRatingEngineSchema20130713(), pmsf.getById(1000L)) ;
		}
		return null;
	}

	@Override
	public List<? extends IPlayerMatchRating> getForMatch(Long matchId) {
		// TODO Auto-generated method stub
		return new ArrayList<PlayerMatchRating>();
	}

	@Override
	public IPlayerMatchRating getNew(IPlayer playerId, IMatchGroup matchId,
			Integer rating, IMatchRatingEngineSchema schemaId,
			IPlayerMatchStats playerMatchStatsId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPlayerMatchRating put(IPlayerMatchRating pmr) {
		// TODO Auto-generated method stub
		return pmr;
	}

	@Override
	public IPlayerMatchRating get(IPlayerMatchStats pms, IMatchRatingEngineSchema schema) {
		if (pms.getPlayerId().equals(9001014L)&& pms.getMatchId().equals(300L)) {
			mf.setId(300L);
			return new PlayerMatchRating(723, pf.getById(9001014L), (IGroup)mf.getGame(),
					new ScrumMatchRatingEngineSchema20130713(), pmsf.getById(1000L)) ;
		}
		return null;
	}

	@Override
	public Boolean deleteForSchema(IMatchRatingEngineSchema schema) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		// TODO Auto-generated method stub
		return false;
	}

}
