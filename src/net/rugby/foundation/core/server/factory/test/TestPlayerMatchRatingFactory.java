package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerMatchRating;

public class TestPlayerMatchRatingFactory implements IPlayerMatchRatingFactory {

	private IPlayerMatchStatsFactory pmsf;
	private IMatchGroupFactory mf;
	private IPlayerFactory pf;
	IRatingQueryFactory rqf;
	private Random random = new Random();
	
	@Inject
	TestPlayerMatchRatingFactory(IPlayerMatchStatsFactory pmsf, IMatchGroupFactory mgf, IPlayerFactory pf, IRatingQueryFactory rqf ) {
		this.pmsf = pmsf;
		this.mf = mgf;
		this.pf = pf;
		this.rqf = rqf;
	}
	
	@Override
	public IPlayerMatchRating get(Long id) {
		if (id.equals(2000L)) {

			return new PlayerMatchRating(723, pf.get(9001014L), (IGroup)mf.get(300L),
					new ScrumMatchRatingEngineSchema20130713(), pmsf.getById(1000L), "helpful tooltip details also", rqf.get(700L)) ;
		}
		return null;
	}

	@Override
	public List<? extends IPlayerMatchRating> getForMatch(Long matchId) {
		
		List<IPlayerMatchRating> list = new ArrayList<IPlayerMatchRating>();
		
		assert(matchId.equals(100L));
		
		List<IPlayerMatchStats> pmsl = pmsf.getByMatchId(matchId);
		
		Iterator<IPlayerMatchStats> it = pmsl.iterator();
		IRatingEngineSchema schema = new ScrumMatchRatingEngineSchema20130713();
		
		while (it.hasNext()) {
			list.add(get(it.next(),schema));
		}
		return list;

	}

	@Override
	public IPlayerMatchRating getNew(IPlayer player, IMatchGroup match,
			Integer rating, IRatingEngineSchema schema,
			IPlayerMatchStats playerMatchStats, String details, IRatingQuery query) {
		return new PlayerMatchRating(rating, player, match, schema, playerMatchStats, details, null);
	}

	@Override
	public IPlayerMatchRating put(IPlayerMatchRating pmr) {
		return pmr;
	}

	@Override
	public IPlayerMatchRating get(IPlayerMatchStats pms, IRatingEngineSchema schema) {
		int rating = getRandomRating();
		return new PlayerMatchRating(rating, pf.get(pms.getPlayerId()), (IGroup)mf.get(pms.getMatchId()), schema, pms, "helpful tooltip details", rqf.get(700L));
	}

	private int getRandomRating() {
		return random.nextInt(1000);
	}

	@Override
	public Boolean deleteForSchema(IRatingEngineSchema schema) {
		return true;
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		return true;
	}

	@Override
	public boolean deleteForQuery(IRatingQuery rq) {
		// TODO Auto-generated method stub
		return true;
	}

}
