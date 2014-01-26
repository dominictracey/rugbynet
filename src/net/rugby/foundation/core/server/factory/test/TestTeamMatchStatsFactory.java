package net.rugby.foundation.core.server.factory.test;


import java.io.Serializable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.BaseTeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.model.shared.ScrumTeamMatchStats;

public class TestTeamMatchStatsFactory extends BaseTeamMatchStatsFactory implements ITeamMatchStatsFactory, Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6305034768143121512L;
	private Random random = new Random();
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#get()
	 */
	@Override
	public ITeamMatchStats getFromPersistentDatastore(Long id) {
		try {
			// mock it up with random numbers
			ITeamMatchStats tms = new ScrumTeamMatchStats();
			tms.setId(id);
			tms.setCleanBreaks(random.nextInt(5));
			tms.setConversionsAttempted(random.nextInt(5));
			tms.setConversionsMade(tms.getConversionsAttempted()-1);
			tms.setDefendersBeaten(random.nextInt(5));
			tms.setDropGoalsAttempted(0);
			tms.setDropGoalsMade(0);
			tms.setKicksFromHand(random.nextInt(50));
			tms.setLineoutsThrownIn(random.nextInt(10));
			tms.setLineoutsWonOnOwnThrow(tms.getLineoutsThrownIn() - 1);
			tms.setMauls(random.nextInt(10));
			tms.setMaulsWon(tms.getMauls() -1);
			tms.setMetersRun(random.nextInt(200));
			tms.setOffloads(random.nextInt(10));
			tms.setPasses(random.nextInt(100));
			tms.setPenaltiesAttempted(random.nextInt(5));
			tms.setPenaltiesMade(tms.getPenaltiesMade()-1);
			tms.setPenaltiesConceded(random.nextInt(2));
			tms.setRedCards(0);
			tms.setRucks(random.nextInt(100));
			tms.setRucksWon((int) (tms.getRucks() * .87));
			tms.setScrumsPutIn(random.nextInt(10));
			tms.setScrumsWonOnOwnPut((int) (tms.getScrumsPutIn() * random.nextFloat()));
			tms.setTacklesMade(random.nextInt(100));
			tms.setTacklesMissed(random.nextInt(15));
			tms.setTerritory(.5F);
			tms.setTries(random.nextInt(5));
			tms.setTurnoversConceded(random.nextInt(1));
			tms.setYellowCards(0);
			return tms;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory#put(net.rugby.foundation.model.shared.ITeamMatchStats)
	 */
	@Override
	public ITeamMatchStats putToPersistentDatastore(ITeamMatchStats tms) {
		try {
			return tms;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}


	@Override
	public boolean deleteFromPersistentDatastore(ITeamMatchStats t) {
		try {
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}



	@Override
	protected ITeamMatchStats getFromPersistentDatastoreByMatchId(Long mid,	Home_or_Visitor home) {
		IMatchGroup m = mf.get(mid);
		Long id = 0L;
		if (home == Home_or_Visitor.HOME) {
			id = m.getHomeTeamId();
		} else {
			id = m.getVisitingTeamId();
		}
		
		// the id is not uniquey enough I think
		ITeamMatchStats tms = get(id + 50000L + (mid * 10));
		
		tms.setIsHome(home == Home_or_Visitor.HOME);
		tms.setMatchId(mid);
		if (home == Home_or_Visitor.HOME) {
			tms.setTeamId(m.getHomeTeamId());
		} else {
			tms.setTeamId(m.getVisitingTeamId());
		}
		return tms;
	}

}
