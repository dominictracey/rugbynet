package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
	private Random random = new Random();
	
	@Inject
	TestPlayerMatchRatingFactory(IPlayerMatchStatsFactory pmsf, IMatchGroupFactory mgf, IPlayerFactory pf ) {
		this.pmsf = pmsf;
		this.mf = mgf;
		this.pf = pf;
	}
	
	@Override
	public IPlayerMatchRating get(Long id) {
		if (id.equals(2000L)) {

			return new PlayerMatchRating(723, pf.get(9001014L), (IGroup)mf.get(300L),
					new ScrumMatchRatingEngineSchema20130713(), pmsf.getById(1000L)) ;
		}
		return null;
	}

	@Override
	public List<? extends IPlayerMatchRating> getForMatch(Long matchId) {
		
		List<IPlayerMatchRating> list = new ArrayList<IPlayerMatchRating>();
		
		assert(matchId.equals(100L));
		
		List<IPlayerMatchStats> pmsl = pmsf.getByMatchId(matchId);
		
		Iterator<IPlayerMatchStats> it = pmsl.iterator();
		IMatchRatingEngineSchema schema = new ScrumMatchRatingEngineSchema20130713();
		
		while (it.hasNext()) {
			list.add(get(it.next(),schema));
		}
		return list;

	}

	@Override
	public IPlayerMatchRating getNew(IPlayer playerId, IMatchGroup matchId,
			Integer rating, IMatchRatingEngineSchema schemaId,
			IPlayerMatchStats playerMatchStatsId) {
		return null;
	}

	@Override
	public IPlayerMatchRating put(IPlayerMatchRating pmr) {
		return pmr;
	}

	@Override
	public IPlayerMatchRating get(IPlayerMatchStats pms, IMatchRatingEngineSchema schema) {
		int rating = getRandomRating();
		return new PlayerMatchRating(rating, pf.get(pms.getPlayerId()), (IGroup)mf.get(pms.getMatchId()), schema, pms);
	}

	private int getRandomRating() {
		return random.nextInt(1000);
	}

	@Override
	public Boolean deleteForSchema(IMatchRatingEngineSchema schema) {
		return true;
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		return true;
	}

}
