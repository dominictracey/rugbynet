package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.CompRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.inject.Inject;

/**
 * 
 * @author Dominic Tracey
 * 
 * Setting any Id will work, but other test factories like to use compId 1L;
 *
 */

public class TestCompetitionFactory implements ICompetitionFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 788805092578913470L;
	private Long id;
	private final IRoundFactory rf;
	private ICoreRuleFactory crf;
	private ITeamGroupFactory tf;
	private IClubhouseFactory chf;
	private IMatchGroupFactory mf;
	
	@Inject
	TestCompetitionFactory(IRoundFactory rf, ICoreRuleFactory crf, ITeamGroupFactory tf, IClubhouseFactory chf, ICompetitionFactory cf, IMatchGroupFactory mf) {
		this.rf = rf;
		//this.rf.setFactories(cf, mf);
		this.mf = mf;
		//this.mf.setFactories(rf, tf);
		this.crf = crf;
		this.tf = tf;
		this.chf = chf;
	}
	
	@Override
	public void setId(Long id) {
		this.id = id;

	}

	@Override
	public ICompetition getCompetition() {
		if (id == 1L) {
			return getTestComp1();
		} else if (id == 2L) {
			return getTestComp2();
		} else if (id == 3L) {
			return getTestComp3();
		} else if (id == 4L) {
			return getTestComp4();
		}
		
		return null;
	}
	
	/*
	 * Comp1 should trigger rules:
	 * 	RuleMatchStaleNeedsAttention
	 * 	RuleMatchToLock
	 * 	RuleMatchToFetch
	 */
	private ICompetition getTestComp1() {
		ICompetition c = getEmptyComp();
		c.getRoundIds().add(2L);
		c.getRoundIds().add(3L);
		c.getRoundIds().add(4L);
		c.getRoundIds().add(5L);
		
		addRounds(c);

		setNextAndPrevRound(c);
		setBeginAndEnd(c);
		
		c.setCompClubhouseId(70L);
		
		for (Long l = 9001L; l < 9007L; ++l)
			c.getTeamIds().add(l);
		
		addTeams(c);
		
		return c;

	}


	/**
	 * @return
	 */
	private ICompetition getTestComp2() {
		ICompetition c = getEmptyComp();
		c.getRoundIds().add(12L);
		c.getRoundIds().add(13L);
		c.getRoundIds().add(14L);
		
		addRounds(c);

		setNextAndPrevRound(c);
		setBeginAndEnd(c);
		
		c.setCompClubhouseId(75L);
		
		for (Long l = 9201L; l < 9209L; ++l)
			c.getTeamIds().add(l);
		
		addTeams(c);
		
		c.setForeignID(999L);
		c.setForeignURL("testData\\191757-round2-results.htm");
		c.setLongName("European Heineken Cup");
		c.setShortName("Heineken Cup");

		return c;
	}

	/**
	 * @return
	 */
	private ICompetition getTestComp3() {
		ICompetition c = getEmptyComp();
		c.getRoundIds().add(31L);
		addRounds(c);
		//setNextAndPrevRound(c);
		c.setPrevRoundIndex(-1);
		c.setNextRoundIndex(0);
		setBeginAndEnd(c);
		return c;
	}
	
	/**
	 * @return
	 */
	private ICompetition getTestComp4() {
		ICompetition c = getEmptyComp();
		c.getRoundIds().add(15L);
		addRounds(c);
		for (Long i=9300L; i<9324L; ++i) {
			c.getTeamIds().add(i);
			c.getTeams().add(tf.get(i));
		}
		//setNextAndPrevRound(c);
//		c.setPrevRoundIndex(-1);
//		c.setNextRoundIndex(0);
//		setBeginAndEnd(c);
		return c;
	}
	
	@Override
	public ICompetition put(ICompetition c) {	
		
		if (c.getCompClubhouseId() == null) {
			chf.setId(null);
			IClubhouse clubhouse = null;
			clubhouse = chf.get();
			clubhouse.setActive(true);
			clubhouse.setDescription("CompetitionClubhouse for " + c.getLongName());
			clubhouse.setName("CC" + c.getShortName());
			clubhouse.setOwnerID(-99L);  //-99L is system owned
			clubhouse.setPublicClubhouse(false);
			// no join link
			
			clubhouse = chf.put(clubhouse);

			c.setCompClubhouseId(clubhouse.getId());
		}	
		
		return c;
	}
	


	/*
	 * Prev round is defined as the round before the next Wednesday and Next round is the round after the next Wednesday
	 *   To keep some sense of sanity in our testing, we assume an absolute view of time.. ah fuck it.. put it in a rule.
	 */
	private void setNextAndPrevRound(ICompetition c) {
		c.setPrevRoundIndex(-1);
		c.setNextRoundIndex(0);
		boolean done = false;
		while (!done) {
			IRule<ICompetition> checker = crf.get(c, CompRule.COMP_INCREMENT_NEXT_ROUND);
			if (checker.test() == true) {
				c.setPrevRoundIndex(c.getNextRoundIndex());
				c.setNextRoundIndex(c.getNextRoundIndex()+1);
			} else {
				done = true;
			}
		}
	}
	

	
	private void setBeginAndEnd(ICompetition c) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1000);
		c.setEnd(cal.getTime());
		cal.add(Calendar.DATE, 2000);
		c.setBegin(cal.getTime());

		for (IRound r : c.getRounds()) {
			if (r.getBegin().before(r.getBegin())) {
				c.setBegin(r.getBegin());
			}
			
			if (r.getEnd().after(c.getEnd())) {
				c.setEnd(r.getEnd());
			}
		}
	}

	private ICompetition getEmptyComp() {
		Competition c = new Competition();
		c.setAbbr("Rugby.net");
		
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -12);
		c.setBegin(cal.getTime());
		cal.add(Calendar.DATE, 23);
		c.setEnd(cal.getTime());

		
		c.setForeignID(999L);
		c.setForeignURL("http://testonlyplease.com/999");
		
		c.setId(id);
		c.setLongName("Rugby.net Championship Cup");
		c.setShortName("Rugby.net Championship");
		c.setUnderway(true);
		
		c.setRounds(new ArrayList<IRound>());
		
		return c;
	}
	
	/**
	 * @param c
	 */
	private void addRounds(ICompetition c) {
		for (Long rid : c.getRoundIds()) {
			rf.setId(rid);
			IRound g = rf.getRound();
			c.getRounds().add(g);
		}		
	}

	/**
	 * @param c
	 */
	private void addTeams(ICompetition c) {
		for (Long tid : c.getTeamIds()) {
			ITeamGroup g = tf.get(tid);
			c.getTeams().add(g);
		}		
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getUnderwayComps()
	 */
	@Override
	public List<ICompetition> getUnderwayComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();

		setId(1L);
		list.add(getCompetition());
		
		setId(2L);
		list.add(getCompetition());
		
		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getAllComps()
	 */
	@Override
	public List<ICompetition> getAllComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();

		setId(1L);
		list.add(getCompetition());
		
		setId(2L);
		list.add(getCompetition());
		
		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#build()
	 */
	@Override
	public void build(Long compId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ICompetition repair(ICompetition comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(Long compId) {
		// TODO Auto-generated method stub
		return false;
	}

//	/* (non-Javadoc)
//	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getLastUpdate(java.lang.Long)
//	 */
//	@Override
//	public Date getLastUpdate(Long compId) {
//		return new Date();  // I guess don't cache in test?
//	}
}
