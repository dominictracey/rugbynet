package net.rugby.foundation.server;

import java.util.ArrayList;
import java.util.Date;

import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.TeamMembership;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class Utility {
	private static Utility instance = null; 
	
	private Utility() {
	
	}
	
	public static Utility getInstance() {
		if (instance == null) {
			instance = new Utility();
		}
		return instance;
	}
	
	public boolean updateTeamMembership(Objectify ofy) {
		Query<Player> qp = ofy.query(Player.class);
		
		for (Player p : qp) {
			TeamMembership tm = new TeamMembership();
			tm.setStart(new Date());
			tm.setPlayerID(p.getId());
			tm.setTeamID(p.getTeamID());
			tm.setActive(true);
			
			p.setTeamAbbr(null);
			p.setTeamID(null);
			p.setTeamName(null);
			
			p.setActive(true);  // populate this new field as well
			
			ofy.put(tm,p);		
		}
		
		return true;
	}
	
	public boolean createCompetitions(Objectify ofy) {
		
//		Competition poolComp = new Competition();
//		poolComp.setLongName("2011 Rugby World Cup Pool Stages");
//		poolComp.setShortName("2011 RWC Pool");
//		poolComp.setAbbr("Pool");
//		poolComp.setBegin(new Date());
//		poolComp.setEnd(new Date("10/2/2011"));
//		
//		// 4 rounds for pool
//		Round r1 = new Round();
//		r1.setName("Round 1");
//		r1.setAbbr("R1");
//		for (Long i=31L; i < 41L; ++i) {
//			r1.addMatchID(i);
//		}
//		
//		Round r2 = new Round();
//		r2.setName("Round 2");
//		r2.setAbbr("R2");
//		for (Long i=41L; i < 51L; ++i) {
//			r2.addMatchID(i);
//		}
//		
//		Round r3 = new Round();
//		r3.setName("Round 3");
//		r3.setAbbr("R3");
//		for (Long i=51L; i < 61L; ++i) {
//			r3.addMatchID(i);
//		}
//		
//		Round r4 = new Round();
//		r4.setName("Round 4");
//		r4.setAbbr("R4");
//		for (Long i=61L; i < 71L; ++i) {
//			r4.addMatchID(i);
//		}
//		
//		ofy.put(r1,r2,r3,r4);
//
//		poolComp.addRoundID(r1.getId());
//		poolComp.addRoundID(r2.getId());
//		poolComp.addRoundID(r3.getId());
//		poolComp.addRoundID(r4.getId());
//		
//		ofy.put(poolComp);
//		
//
//		Query<Group> tq = ofy.query(Group.class).filter("groupType", "TEAM");
//		
//		for (Group g : tq) {
//			if (g instanceof TeamGroup) {
//				CompetitionTeam ct = new CompetitionTeam();
//				ct.setCompetitionID(poolComp.getId());
//				ct.setTeamID(g.getId());
//				ofy.put(ct);
//			}
//		}
//
//		// add some to ko round comp
//		Competition koComp = new Competition();
//		koComp.setLongName("2011 Rugby World Cup Knockout Stage");
//		koComp.setShortName("2011 RWC Knockout");
//		koComp.setAbbr("Knockout");
//		koComp.setBegin(new Date());
//		koComp.setEnd(new Date("10/28/2011"));
//		
//		// 3 rounds for KO (consolation match is in final round)
//		Round qf = new Round();
//		qf.setName("Quarterfinals");
//		qf.setAbbr("Quarter");
//		
//		//nzl-arg
//		MatchGroup qf1 = new MatchGroup();
//		Key<Group> nzlKey = new Key<Group>(Group.class,12L);
//		Key<Group> argKey = new Key<Group>(Group.class,1L);
//		qf1.setHomeTeam(nzlKey);
//		qf1.setVisitingTeam(argKey);
//		Group nzl = ofy.get(nzlKey);
//		Group arg = ofy.get(argKey);
//		qf1.setDisplayName(nzl,arg);
//		ofy.put(qf1);
//		
//		//eng-fra
//		MatchGroup qf2 = new MatchGroup();
//		Key<Group> engKey = new Key<Group>(Group.class,4L);
//		Key<Group> fraKey = new Key<Group>(Group.class,6L);
//		qf2.setHomeTeam(engKey);
//		qf2.setVisitingTeam(fraKey);
//		Group eng = ofy.get(engKey);
//		Group fra = ofy.get(fraKey);
//		qf2.setDisplayName(eng,fra);
//		ofy.put(qf2);
//
//		//ire-wal
//		MatchGroup qf3 = new MatchGroup();
//		Key<Group> ireKey = new Key<Group>(Group.class,8L);
//		Key<Group> walKey = new Key<Group>(Group.class,20L);
//		qf3.setHomeTeam(ireKey);
//		qf3.setVisitingTeam(walKey);
//		Group ire = ofy.get(ireKey);
//		Group wal = ofy.get(walKey);
//		qf3.setDisplayName(ire,wal);
//		ofy.put(qf3);
//
//		//rsa-aus
//		MatchGroup qf4 = new MatchGroup();
//		Key<Group> rsaKey = new Key<Group>(Group.class,17L);
//		Key<Group> ausKey = new Key<Group>(Group.class,2L);
//		qf4.setHomeTeam(rsaKey);
//		qf4.setVisitingTeam(ausKey);
//		Group rsa = ofy.get(rsaKey);
//		Group aus = ofy.get(ausKey);
//		qf4.setDisplayName(rsa,aus);
//		ofy.put(qf4);
//		
//		qf.addMatchID(qf1.getId());
//		qf.addMatchID(qf2.getId());
//		qf.addMatchID(qf3.getId());
//		qf.addMatchID(qf4.getId());
//		
//		//semis
//		Round sf = new Round();
//		sf.setName("Semifinals");
//		sf.setAbbr("Semi");
//
//		//finals
//		Round f = new Round();
//		f.setName("Finals");
//		f.setAbbr("Finals");
//				
//		ofy.put(qf,sf,f);
//		
//		koComp.addRoundID(qf.getId());
//		koComp.addRoundID(sf.getId());
//		koComp.addRoundID(f.getId());
//		
//		ofy.put(koComp);
//		
//		// add the eight to ko round comp
//		ArrayList<Long> finalists = new ArrayList<Long>();
//		finalists.add(12L);
//		finalists.add(6L);
//		finalists.add(4L);
//		finalists.add(1L);
//		finalists.add(8L);
//		finalists.add(2L);
//		finalists.add(17L);
//		finalists.add(20L);
//		
//		for (Long g : finalists) {
//			CompetitionTeam ct = new CompetitionTeam();
//			ct.setCompetitionID(koComp.getId());
//			ct.setTeamID(g);
//			ofy.put(ct);
//		}
				
		return true;
	}
	
	public boolean setPoolRoundScores(Objectify ofy) {		
		// move all active players' current scores into the pool rating
		Query<Player> qp = ofy.query(Player.class).filter("active", true);
		for (Player p: qp) {
			p.setPoolStageRating(p.getOverallRating());
			ofy.put(p);
		}
		
		return true;
	}

	public ArrayList<String> setupSemis(Objectify ofy) {
		ArrayList<String> retval = new ArrayList<String>();
//		
//		Query<Round> qsf = ofy.query(Round.class).filter("abbr", "Semi");
//		Round sf = qsf.get();
//		
//		//wal-fra
//		MatchGroup sf1 = new MatchGroup();
//		Key<Group> walKey = new Key<Group>(Group.class,20L);
//		Key<Group> fraKey = new Key<Group>(Group.class,6L);
//		sf1.setHomeTeam(walKey);
//		sf1.setVisitingTeam(fraKey);
//		Group wal = ofy.get(walKey);
//		Group fra = ofy.get(fraKey);
//		sf1.setDisplayName(wal,fra);
//		ofy.put(sf1);
//		
//		//nzl-aus
//		MatchGroup sf2 = new MatchGroup();
//		Key<Group> nzlKey = new Key<Group>(Group.class,12L);
//		Key<Group> ausKey = new Key<Group>(Group.class,2L);
//		sf2.setHomeTeam(nzlKey);
//		sf2.setVisitingTeam(ausKey);
//		Group nzl = ofy.get(nzlKey);
//		Group aus = ofy.get(ausKey);
//		sf2.setDisplayName(nzl,aus);
//		ofy.put(sf2);
//		
//		sf.addMatchID(sf1.getId());
//		sf.addMatchID(sf2.getId());
//		
//		ofy.put(sf);
		
		return retval;
	}

	public ArrayList<String> setupFinals(Objectify ofy) {
		ArrayList<String> retval = new ArrayList<String>();
		
//		Query<Round> qsf = ofy.query(Round.class).filter("abbr", "Finals");
//		Round f = qsf.get();
//		
//		//wal-aus
//		MatchGroup f1 = new MatchGroup();
//		Key<Group> walKey = new Key<Group>(Group.class,20L);
//		Key<Group> ausKey = new Key<Group>(Group.class,2L);
//
//		f1.setHomeTeam(walKey);
//		f1.setVisitingTeam(ausKey);
//
//		Group wal = ofy.get(walKey);
//		Group aus = ofy.get(ausKey);
//
//		f1.setDisplayName(wal,aus);
//		ofy.put(f1);
//		
//		//nzl-fra
//		MatchGroup f2 = new MatchGroup();
//		Key<Group> nzlKey = new Key<Group>(Group.class,12L);
//		Key<Group> fraKey = new Key<Group>(Group.class,6L);
//
//		f2.setHomeTeam(nzlKey);
//		f2.setVisitingTeam(fraKey);
//		Group nzl = ofy.get(nzlKey);
//		Group fra = ofy.get(fraKey);
//		f2.setDisplayName(nzl,fra);
//		ofy.put(f2);
//		
//		f.addMatchID(f1.getId());
//		f.addMatchID(f2.getId());
//		
//		ofy.put(f);
		
		return retval;
	}

}
