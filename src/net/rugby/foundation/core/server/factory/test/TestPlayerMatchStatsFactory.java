package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BasePlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;

public class TestPlayerMatchStatsFactory extends BasePlayerMatchStatsFactory implements IPlayerMatchStatsFactory, Serializable {
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
	public IPlayerMatchStats getFromPersistentDatastore(Long id) {
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

	@Override
	public List<IPlayerMatchStats> getFromPersistentDatastoreByMatchId(Long matchId) {
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
	public boolean deleteForMatch(IMatchGroup m) {
		return true;
	}
	@Override
	public IPlayerMatchStats create() {
		return new ScrumPlayerMatchStats();
	}
	@Override
	protected IPlayerMatchStats putToPersistentDatastore(IPlayerMatchStats t) {
		return t;
	}
	@Override
	protected boolean deleteFromPersistentDatastore(IPlayerMatchStats t) {
		return true;
	}
	@Override
	protected List<IPlayerMatchStats> queryFromPersistentDatastore(IRatingQuery rq) {
		return null;
	}

}
