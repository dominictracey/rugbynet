package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import net.rugby.foundation.core.server.factory.BaseMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.Group.GroupType;

public class TestMatchGroupFactory extends BaseMatchGroupFactory implements IMatchGroupFactory {


	@Override
	public IMatchGroup getFromPersistentDatastore(Long id) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		IMatchGroup g = new MatchGroup();
		((MatchGroup)g).setId(id);
		((IGroup)g).setGroupType(GroupType.MATCH);
		g.setHomeTeamId(0L);
		g.setVisitingTeamId(0L);
		if (id == 100) {
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9002L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -12);
			g.setSimpleScoreMatchResultId(8001L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8001L));
			g.setStatus(Status.FINAL_HOME_WIN);
		} else if (id == 101) {
			g.setHomeTeamId(9003L);
			g.setVisitingTeamId(9004L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -11);
			g.setSimpleScoreMatchResultId(8002L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8002L));
			g.setStatus(Status.FINAL_VISITOR_WIN);
		} else if (id == 102) {
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9004L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -4);
			g.setSimpleScoreMatchResultId(8003L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8003L));
			g.setStatus(Status.FINAL_DRAW_OT);
		} else if (id == 103) {
			g.setHomeTeamId(9005L);
			g.setVisitingTeamId(9006L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -3);
			g.setSimpleScoreMatchResultId(8004L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8004L));
			g.setStatus(Status.FINAL_HOME_WIN);
		} else if (id == 104) {
			g.setHomeTeamId(9002L);
			g.setVisitingTeamId(9003L);
			g.setLocked(true);
			cal.add(Calendar.MINUTE, -120);
			g.setStatus(Status.UNDERWAY_FIRST_HALF);
		} else if (id == 105) {
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9005L);
			g.setLocked(false);
			cal.add(Calendar.DATE, 4);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 106) {
			g.setHomeTeamId(9004L);
			g.setVisitingTeamId(9006L);
			g.setLocked(false);
			cal.add(Calendar.DATE, 4);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 107) {
			g.setHomeTeamId(0L);
			g.setVisitingTeamId(0L);
			g.setLocked(true);
			cal.add(Calendar.DATE, 10);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 108) {
			g.setHomeTeamId(0L);
			g.setVisitingTeamId(0L);
			g.setLocked(true);
			cal.add(Calendar.DATE, 11);
			g.setStatus(Status.SCHEDULED);
			// Begin Comp 2
		} else if (id == 200) {
			g.setHomeTeamId(9201L);
			g.setVisitingTeamId(9202L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -7);
			g.setSimpleScoreMatchResultId(8201L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8201L));
			g.setStatus(Status.FINAL_HOME_WIN);
		} else if (id == 201) {
			g.setHomeTeamId(9203L);
			g.setVisitingTeamId(9204L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -8);
			g.setSimpleScoreMatchResultId(8202L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8202L));
			g.setStatus(Status.FINAL_VISITOR_WIN);
		} else if (id == 202) {
			g.setHomeTeamId(9205L);
			g.setVisitingTeamId(9206L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -8);
			g.setSimpleScoreMatchResultId(8203L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get(8203L));
			g.setStatus(Status.FINAL_VISITOR_WIN);
		} else if (id == 203) {
			g.setHomeTeamId(9207L);
			g.setVisitingTeamId(9208L);
			g.setLocked(true);
			cal.add(Calendar.MINUTE, -120);
//			mrf.setId(8204L);
//			g.setSimpleScoreMatchResultId(8204L);
//			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.UNDERWAY_FIRST_HALF);
		} else if (id == 204) {
			g.setHomeTeamId(9201L);
			g.setVisitingTeamId(9208L);
			g.setLocked(true);
			cal.add(Calendar.DATE, 0);
			g.setStatus(Status.UNDERWAY_FIRST_HALF);
		} else if (id == 205) {
			g.setHomeTeamId(9202L);
			g.setVisitingTeamId(9207L);
			g.setLocked(false);
			cal.add(Calendar.MINUTE, 50);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 206) {
			g.setHomeTeamId(9203L);
			g.setVisitingTeamId(9206L);
			g.setLocked(false);
			cal.add(Calendar.DATE, 8);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 207) {
			g.setHomeTeamId(9204L);
			g.setVisitingTeamId(9205L);
			g.setLocked(false);
			cal.add(Calendar.DATE, 9);
			g.setStatus(Status.SCHEDULED);
		} else if (id == 208) {  // for testing 
			g.setHomeTeamId(9209L);
			g.setVisitingTeamId(9202L);
			g.setLocked(false);
			cal.add(Calendar.MINUTE, -120);
			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
			/*
			 * RWC 11
			 */
		} else if (id == 300) {  
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9002L);
			g.setLocked(true);
			g.setForeignId(93503L);
			//g.setForeignUrl("http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard");
			cal.set(2011, 10, 16);
			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
		} else if (id == 400) {  
			// http://www.espnscrum.com/premiership-2013-14/rugby/match/188689.html
			// testing out fetching player match stats
			g.setHomeTeamId(9210L);
			g.setVisitingTeamId(9211L);
			g.setLocked(true);
			g.setForeignId(188689L);
			//g.setForeignUrl("http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard");
			cal.set(2013, 9, 7);
			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
		}  else if (id == 401) {  
			// http://www.espnscrum.com/premiership-2013-14/rugby/match/188683.html
			g.setHomeTeamId(9212L);
			g.setVisitingTeamId(9213L);
			g.setDisplayName("London Irish v Saracens");
			g.setLocked(true);
			g.setForeignId(188683L);
			//g.setForeignUrl("http://www.espnscrum.com/premiership-2013-14/rugby/match/188683.html?view=scorecard");
			cal.set(2013, 9, 7);
			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
		}
		//if (g.getHomeTeamId() != 0L) {
			g.setHomeTeam(tf.get(g.getHomeTeamId()));
		//} 
		//
		//if (g.getVisitingTeamId() != 0L) {
			g.setVisitingTeam(tf.get(g.getVisitingTeamId()));
		//}
		
		//if (g.getHomeTeamId() != 0L && g.getVisitingTeamId() != 0L)
			g.setDisplayName();	
		
		g.setDate(cal.getTime());
		return g;
	}


	@Override
	public IMatchGroup putToPersistentDatastore(IMatchGroup g) {
		return g;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#find(net.rugby.foundation.model.shared.IMatchGroup)
	 */
	@Override
	public IMatchGroup find(IMatchGroup match) {
		// hard to implement
		return null;
	}

//	@Override
//	public List<IMatchGroup> getMatchesForRound(Long roundId) {
//		// @TODO haven't tested this
//		rf.setId(roundId);
//		IRound r = rf.getRound();
//		if (r != null) {
//			return r.getMatches();
//		} else {
//			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Could not find requested Round " + roundId);
//			return null;
//		}
//	}

	@Override
	public List<? extends IMatchGroup> getMatchesWithPipelines() {
		return new ArrayList<IMatchGroup>();
	}


	@Override
	public IMatchGroup create() {
		return new MatchGroup();
	}


	@Override
	protected boolean deleteFromPersistentDatastore(IMatchGroup t) {
		return true;
	}



}
