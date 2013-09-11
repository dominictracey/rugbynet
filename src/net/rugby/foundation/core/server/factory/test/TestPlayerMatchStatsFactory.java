package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStatTimeLog;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class TestPlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2975677607806066726L;
	private IPlayerFactory pf;
	private IMatchGroupFactory mf;
	private Random random = new Random();

	@Inject
	public TestPlayerMatchStatsFactory(IPlayerFactory pf, IMatchGroupFactory mf) {
		this.pf = pf;
		this.mf = mf;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#getById(java.lang.Long)
	 */
	@Override
	public IPlayerMatchStats getById(Long id) {
		if (id == null) {
			return new ScrumPlayerMatchStats();
		}
		Long pid = id-10000L;
		Long teamId = 9002L; //AUS
		int slot = (int) (pid-9002000);
		if (id < 9001999) {
			teamId = 9001L; //NZL
			slot += 1000;
		}
		return createPMS(pf.get(pid), mf.get(100L), slot, teamId);
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#put(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public IPlayerMatchStats put(IPlayerMatchStats val) {
		return val;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.test.IPlayerMatchFactory#delete(net.rugby.foundation.model.shared.IPlayerMatchStats)
	 */
	@Override
	public Boolean delete(IPlayerMatchStats val) {
		return true;
	}

	@Override
	public List<IPlayerMatchStats> getByMatchId(Long matchId) {
		List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();
		IMatchGroup m = mf.get(matchId);
		//NZL
		int slot = 0;
		for (Long i=9001001L; i<9001023L; ++i) {
			IPlayerMatchStats pms = createPMS(pf.get(i), m, slot++, 9001L);
			pms.setId(i+10000L);
			list.add(pms);
		}
		
		//AUS
		slot = 0;
		for (Long i=9002001L; i<9002023L; ++i) {
			IPlayerMatchStats pms = createPMS(pf.get(i), m, slot++, 9002L);
			pms.setId(i+10000L);
			list.add(pms);
		}
		return list;
	}

	private IPlayerMatchStats createPMS(IPlayer p, IMatchGroup m, Integer slot, Long teamId) {
		IPlayerMatchStats pms = new ScrumPlayerMatchStats();
		pms.setCleanBreaks(random.nextInt(5));
		pms.setDefendersBeaten(random.nextInt(5));
		pms.setKicks(random.nextInt(5));
		pms.setLineoutsStolenOnOppThrow(random.nextInt(1));
		pms.setLineoutsWonOnThrow(random.nextInt(2));
		pms.setMatchId(m.getId());
		pms.setMetersRun(random.nextInt(50));
		pms.setName(p.getDisplayName());
		pms.setOffloads(random.nextInt(5));
		pms.setPasses(random.nextInt(5));
		pms.setPenaltiesConceded(random.nextInt(2));
		pms.setPlayerId(p.getId());
		pms.setPoints(random.nextInt(5));
		pms.setPosition(p.getPosition());
		pms.setRedCards(0);
		pms.setRuns(random.nextInt(5));
		pms.setSlot(slot);
		pms.setTacklesMade(random.nextInt(5));
		pms.setTacklesMissed(random.nextInt(5));
		pms.setTeamId(teamId);
		pms.setTimePlayed(random.nextInt(80));
		pms.setTries(random.nextInt(1));
		pms.setTryAssists(random.nextInt(1));
		pms.setTurnovers(random.nextInt(3));
		pms.setYellowCards(0);
		return pms;
	}
	@Override
	public List<IPlayerMatchStats> query(List<Long> matchIds,
			position posi, Long countryId, Long teamId) {
		return getByMatchId(100L);
	}

	@Override
	public boolean deleteForMatch(IMatchGroup m) {
		return true;
	}
}
