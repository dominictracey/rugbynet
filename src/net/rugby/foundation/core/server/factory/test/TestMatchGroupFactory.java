package net.rugby.foundation.core.server.factory.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.google.inject.Inject;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;
import net.rugby.foundation.model.shared.MatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.Group.GroupType;

public class TestMatchGroupFactory implements IMatchGroupFactory {
	private Long id;
	private ITeamGroupFactory tf;
	private final IMatchResultFactory mrf;
	
	@Inject
	TestMatchGroupFactory(ITeamGroupFactory tf, IMatchResultFactory mrf) {
		this.tf = tf;
		this.mrf = mrf;
	}

	@Override
	public IMatchGroup getGame() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		IMatchGroup g = new MatchGroup();
		((MatchGroup)g).setId(id);
		((Group)g).setGroupType(GroupType.MATCH);
		g.setHomeTeamId(0L);
		g.setVisitingTeamId(0L);
		if (id == 100) {
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9002L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -12);
			mrf.setId(8001L);
			g.setSimpleScoreMatchResultId(8001L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.FINAL_HOME_WIN);
		} else if (id == 101) {
			g.setHomeTeamId(9003L);
			g.setVisitingTeamId(9004L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -11);
			mrf.setId(8002L);
			g.setSimpleScoreMatchResultId(8002L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.FINAL_VISITOR_WIN);
		} else if (id == 102) {
			g.setHomeTeamId(9001L);
			g.setVisitingTeamId(9004L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -4);
			mrf.setId(8003L);
			g.setSimpleScoreMatchResultId(8003L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.FINAL_DRAW_OT);
		} else if (id == 103) {
			g.setHomeTeamId(9005L);
			g.setVisitingTeamId(9006L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -3);
			mrf.setId(8004L);
			g.setSimpleScoreMatchResultId(8004L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
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
			mrf.setId(8201L);
			g.setSimpleScoreMatchResultId(8201L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.FINAL_HOME_WIN);
		} else if (id == 201) {
			g.setHomeTeamId(9203L);
			g.setVisitingTeamId(9204L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -8);
			mrf.setId(8202L);
			g.setSimpleScoreMatchResultId(8202L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
			g.setStatus(Status.FINAL_VISITOR_WIN);
		} else if (id == 202) {
			g.setHomeTeamId(9205L);
			g.setVisitingTeamId(9206L);
			g.setLocked(true);
			cal.add(Calendar.DATE, -8);
			mrf.setId(8203L);
			g.setSimpleScoreMatchResultId(8203L);
			g.setSimpleScoreMatchResult((ISimpleScoreMatchResult) mrf.get());
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
			g.setForeignUrl("http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard");
			cal.set(2011, 10, 16);
			g.setStatus(Status.COMPLETE_AWAITING_RESULTS);
		}
		//if (g.getHomeTeamId() != 0L) {
			tf.setId(g.getHomeTeamId());
			g.setHomeTeam(tf.getTeam());
		//} 
		//
		//if (g.getVisitingTeamId() != 0L) {
			tf.setId(g.getVisitingTeamId());
			g.setVisitingTeam(tf.getTeam());
		//}
		
		//if (g.getHomeTeamId() != 0L && g.getVisitingTeamId() != 0L)
			g.setDisplayName();	
		
		g.setDate(cal.getTime());
		return g;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
		
	}

	@Override
	public IMatchGroup put(IMatchGroup g) {
//		if (g == null) {
//			g = new MatchGroup();
//		}
//		
//		if (g.getHomeTeam() == null) {
//			tf.setId(null);
//			g.setHomeTeam(tf.getTeam());
//		} else {
//			tf.put(g.getHomeTeam());
//		}
//		
//		if (g.getVisitingTeam() == null) {
//			tf.setId(null);
//			g.setVisitingTeam(tf.getTeam());
//		} else {
//			tf.put(g.getVisitingTeam());
//		}
//		
//		((MatchGroup)g).setHomeTeamId(g.getHomeTeam().getId());
//		((MatchGroup)g).setVisitingTeamId(g.getVisitingTeam().getId());
//		
//		ofy.put(g);
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

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IMatchGroupFactory#setFactories(net.rugby.foundation.core.server.factory.IRoundFactory, net.rugby.foundation.core.server.factory.ITeamGroupFactory)
	 */
//	@Override
//	public void setFactories(IRoundFactory rf, ITeamGroupFactory tf) {
//		this.tf = tf;
//		
//	}


}
