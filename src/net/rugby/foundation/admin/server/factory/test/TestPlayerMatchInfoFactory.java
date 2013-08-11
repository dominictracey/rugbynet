package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchInfoFactory;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.PlayerMatchInfo;
import net.rugby.foundation.model.shared.Position.position;

public class TestPlayerMatchInfoFactory implements IPlayerMatchInfoFactory {

	IPlayerMatchStatsFactory pmsf;
	IPlayerMatchRatingFactory pmrf;
	
	@Inject 
	TestPlayerMatchInfoFactory(IPlayerMatchStatsFactory pmsf, IPlayerMatchRatingFactory pmrf) {
		this.pmsf = pmsf;
		this.pmrf = pmrf;
	}
	
	@Override
	public IPlayerMatchInfo get(Long id) {
		return new PlayerMatchInfo(pmsf.getById(id),pmrf.get(id+1000L));
	}

	@Override
	public List<IPlayerMatchInfo> getForComp(Long playerId, Long compId) {
		List<IPlayerMatchInfo> list = new ArrayList<IPlayerMatchInfo>();
		List<IPlayerMatchStats> pmsl = pmsf.getByMatchId(100L);
		List<? extends IPlayerMatchRating> pmrl = pmrf.getForMatch(100L);
		Iterator<IPlayerMatchStats> it = pmsl.iterator();
		int count = 0;
		while (it.hasNext()) {
			list.add(new PlayerMatchInfo(it.next(), pmrl.get(count++)));
		}
		return list;
	}

	@Override
	public List<IPlayerMatchInfo> getForMatch(Long matchId,
			IMatchRatingEngineSchema schema) {
		return getForComp(null,null);
	}

	@Override
	public List<IPlayerMatchInfo> query(Long compId, Long roundId,
			position posi, Long countryId, Long teamId, Long schemaId) {
		return getForComp(null,null);
	}

	@Override
	public IPlayerMatchInfo getForPlayerMatchStats(Long pmsId,
			IMatchRatingEngineSchema schema) {
		return new PlayerMatchInfo(pmsf.getById(pmsId),pmrf.get(pmsId+1000L));	}

}
