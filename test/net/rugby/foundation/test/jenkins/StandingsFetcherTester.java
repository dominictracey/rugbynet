package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class StandingsFetcherTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IUrlCacher uc;
	private IPlayerFactory pf;

	private IMatchGroupFactory mf;

	private ITeamGroupFactory tf;

	private IStandingsFetcher sFetcher;

	private ICompetitionFactory cf;

	private IStandingFactory sf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IUrlCacher uc, IPlayerFactory pf, IMatchGroupFactory mf, ITeamGroupFactory tf, ICompetitionFactory cf, IStandingsFetcher sFetcher, IStandingFactory sf) {
		this.uc = uc;
		this.pf = pf;
		this.mf = mf;
		this.tf = tf;
		this.sFetcher = sFetcher;
		this.sf = sf;
		this.cf = cf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testHeinekenCupRoundOne() {  

		cf.setId(4L);
		ICompetition c = cf.getCompetition();	

		sFetcher.setComp(c);
		sFetcher.setRound(c.getRounds().get(0));
		sFetcher.setUrl("testData\\191757-heineken-standings-round-1.htm");
		sFetcher.setUc(uc);

		Iterator<ITeamGroup> it = c.getTeams().iterator();
		int count = 0;
		while (it.hasNext()) {

			IStanding s = sFetcher.getStandingForTeam(it.next());

			if (s != null) {
				if (s.getTeam().getDisplayName().equals("Scarlets")) {
					assertTrue(s.getStanding().equals(1));
					count++;
				} else if (s.getTeam().getDisplayName().equals("Munster")) {
					assertTrue(s.getStanding().equals(3));
					count++;
				} else  if (s.getTeam().getDisplayName().equals("Toulon")) {
					assertTrue(s.getStanding().equals(1));
					count++;
				}  else  if (s.getTeam().getDisplayName().equals("Clermont Auvergne")) {
					assertTrue(s.getStanding().equals(4));
					count++;
				} else   if (s.getTeam().getDisplayName().equals("Gloucester Rugby")) {
					assertTrue(s.getStanding().equals(2));
					count++;
				} else  if (s.getTeam().getDisplayName().equals("Leinster")) {
					assertTrue(s.getStanding().equals(1));
					count++;
				}
			} else {
				assertTrue(false); // did not process correctly
			}

			assertTrue(count == 6);
		} 
	}


	private IMatchGroup getQuinsScarlets() {
		IMatchGroup m = mf.create();
		m.setForeignId(191605L);

		ITeamGroup v = tf.create();
		v.setId(95L);
		v.setAbbr("SCA");
		m.setVisitingTeam(v);
		ITeamGroup h = tf.create();
		h.setId(94L);
		h.setAbbr("QUI");
		m.setHomeTeam(h);
		return m;
	}
}