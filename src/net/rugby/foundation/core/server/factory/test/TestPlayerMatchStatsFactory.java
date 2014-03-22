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
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;
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

		if (matchId.equals(400L)) {
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
		}

		else {
			// create a bunch of random players
			int slot = 0;
			for (int i=0; i<23; ++i) {
				IPlayer p = pf.get(m.getHomeTeamId() + 100000 + random.nextInt(5000));
				IPlayerMatchStats pms = createPMS(p,m,slot++,m.getHomeTeamId());
				pms.setPosition(position.getAt(random.nextInt(11))); // we do want NONE?
				pms.setId(p.getId()+100000);
				list.add(pms);
			}
			
			slot = 0;
			for (int i=0; i<23; ++i) {
				IPlayer p = pf.get(m.getVisitingTeamId() + 100000 + random.nextInt(5000));
				IPlayerMatchStats pms = createPMS(p,m,slot++,m.getVisitingTeamId());
				pms.setPosition(position.getAt(random.nextInt(11)));  // we do want NONE?
				pms.setId(p.getId()+100000);
				list.add(pms);
			}
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
	@Override
	public List<IPlayerMatchStats> query(IRatingQuery rq) {
		if (rq.getId().equals(704L)) {
			List<IPlayerMatchStats> list = new ArrayList<IPlayerMatchStats>();
			list.addAll(getByMatchId(100L));
			list.addAll(getByMatchId(101L));
			list.addAll(getByMatchId(102L));
			list.addAll(getByMatchId(103L));
			return list;
		} else if (rq.getPositions() != null && rq.getPositions().size() > 0) {
			// if they want a position, just set everyone to be that position
			List<IPlayerMatchStats> list = getByMatchId(100L);
			for (IPlayerMatchStats pms : list) {
				pms.setPosition(rq.getPositions().get(0));
			}
			list.addAll(list);
		}
		return getByMatchId(100L);
	}
}
