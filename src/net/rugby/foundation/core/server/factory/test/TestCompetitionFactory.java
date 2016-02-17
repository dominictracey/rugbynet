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
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
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
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;

import com.google.inject.Inject;

/**
 * 
 * @author Dominic Tracey
 * 
 * Setting any Id will work, but other test factories like to use compId 1L;
 *
 */

public class TestCompetitionFactory extends BaseCachingFactory<ICompetition> implements ICompetitionFactory, Serializable {
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
	private ICompetition globalComp;

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
	protected ICompetition getFromPersistentDatastore(Long id) {

		if (id == 1L) {
			return getTestComp1();
		} else if (id == 2L) {
			return getTestComp2();
		} else if (id == 3L){
			return getTestComp3();
		} else if (id == 4L) {
			return getTestComp4();
		} else if (id == 5L) {
			return getTestComp5();
		} else if (id == 6L){
			return getTestComp6();
		} else if (id == 100000L) {
			return getGlobalComp();
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
		ICompetition c = getEmptyComp(1L);
		c.getRoundIds().add(6L);
		c.getRoundIds().add(7L);
		c.getRoundIds().add(8L);
		c.getRoundIds().add(9L);

		addRounds(c);

		setNextAndPrevRound(c);
		setBeginAndEnd(c);

		c.setCompClubhouseId(70L);
		c.setForeignID(999L);
		c.setForeignURL("testData\\191757-round2-results.htm");
		c.setLongName("The Rugby Net Championships");
		c.setShortName("TRN Championships");
		c.setAbbr("TRNC");
		
		for (Long l = 9001L; l < 9007L; ++l)
			c.getTeamIds().add(l);

		addTeams(c);

		return c;

	}


	/**
	 * @return
	 */
	private ICompetition getTestComp2() {
		ICompetition c = getEmptyComp(2L);
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
		ICompetition c = getEmptyComp(3L);
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
		ICompetition c = getEmptyComp(4L);
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
		c.setCompType(CompetitionType.HEINEKEN_CUP);
		return c;
	}
	
	/**
	 * @return
	 */
	//SUPER RUGBY
	private ICompetition getTestComp5() {
		ICompetition c = getEmptyComp(5L);
		c.getRoundIds().add(16L);
		addRounds(c);
		for (Long i=9400L; i<9415L; ++i) {
			c.getTeamIds().add(i);
			c.getTeams().add(tf.get(i));
		}
		//setNextAndPrevRound(c);
		//		c.setPrevRoundIndex(-1);
		//		c.setNextRoundIndex(0);
		//		setBeginAndEnd(c);
		c.setCompType(CompetitionType.SUPER_RUGBY);
		return c;
	}
	private ICompetition getTestComp6() {
		ICompetition c = getEmptyComp(6L);
		c.getRoundIds().add(17L);
		addRounds(c);
		 
//			c.getTeamIds().add(9210L);
//			c.getTeams().add(tf.get(9210L));
			c.getTeamIds().add(9302L);
			c.getTeams().add(tf.get(9302L));
			c.getTeamIds().add(9305L);
			c.getTeams().add(tf.get(9305L));
			c.getTeamIds().add(9309L);
			c.getTeams().add(tf.get(9309L));
			c.getTeamIds().add(9314L);
			c.getTeams().add(tf.get(9314L));
			c.getTeamIds().add(9318L);
			c.getTeams().add(tf.get(9318L));
			c.getTeamIds().add(9321L);
			c.getTeams().add(tf.get(9321L));
			
		
		//setNextAndPrevRound(c);
		//		c.setPrevRoundIndex(-1);
		//		c.setNextRoundIndex(0);
		//		setBeginAndEnd(c);
		c.setCompType(CompetitionType.AVIVA_PREMIERSHIP);
		return c;
	}
	@Override
	protected ICompetition putToPersistentDatastore(ICompetition c) {

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

	private ICompetition getEmptyComp(long id) {
		Competition c = new Competition();
		c.setId(id);
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
			IRound g = rf.get(rid);
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

		list.add(get(1L));

		list.add(get(2L));

		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#getAllComps()
	 */
	@Override
	public List<ICompetition> getAllComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();

		list.add(get(1L));

		list.add(get(2L));

		return list;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.ICompetitionFactory#build()
	 */
	@Override
	public void invalidate(Long compId) {
		ICompetition c = get(compId);
		if (c != null) {
			deleteFromMemcache(c);
		}

	}

	@Override
	public ICompetition repair(ICompetition comp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteFromPersistentDatastore(ICompetition compId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ICompetition getGlobalComp() {
		if (globalComp == null) {
			globalComp = new Competition();
			globalComp.setLongName("All Global Competitions");
			globalComp.setCompType(CompetitionType.GLOBAL);
			globalComp.setAbbr("Global");
			globalComp.setUnderway(true);
			globalComp.setId(10000L);
		}
		return globalComp;
	}

	@Override
	public ICompetition create() {
		// TODO Auto-generated method stub
		return new Competition();
	}

	@Override
	public List<ICompetition> getClientComps() {
		List<ICompetition> list = new ArrayList<ICompetition>();

		list.add(get(1L));

		list.add(get(2L));

		return list;
	}

	@Override
	public Boolean addRound(Long compId, int uri, String name) {
		// TODO Auto-generated method stub
		return null;
	}


}
