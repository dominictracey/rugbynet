package net.rugby.foundation.test.jenkins;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.server.TopTenTestModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TopTenTestModule.class, AdminTestModule.class, CoreTestModule.class })
public class StandingsFetcherTester {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IUrlCacher uc;

	private IStandingsFetcher sFetcher;

	private ICompetitionFactory cf;

	private IStandingsFetcherFactory sFetcherFactory;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IUrlCacher uc, IPlayerFactory pf, ICompetitionFactory cf, IStandingsFetcherFactory sFetcherFactory, IStandingFactory sf) {
		this.uc = uc;
		this.sFetcherFactory = sFetcherFactory;
		this.cf = cf;
	}

	/**
	 * Must refer to a valid module that sources this class.
	 */
	 public String getModuleName() { // (2)
		 return "net.rugby.foundation.model.Model";
	 }

	 @Test
	 public void testHeinekenCupRoundOne() {

		 ICompetition c = cf.get(4L);

		 sFetcher = sFetcherFactory.getFetcher(c.getRounds().get(0));
			
		 assertTrue(sFetcher != null);
		 
		 sFetcher.setComp(c);
		 sFetcher.setRound(c.getRounds().get(0));
		 sFetcher.setUrl("testData/191757-heineken-standings-round-1.htm");
		 sFetcher.setUc(uc);

		 Iterator<ITeamGroup> it = c.getTeams().iterator();
		 int count = 0;
		 while (it.hasNext()) {
			 ITeamGroup team = it.next();

			 IStanding s = sFetcher.getStandingForTeam(team);

			 if (s != null) {
				 if (s.getTeam().getDisplayName().equals("Scarlets")) {
					 assertTrue(s.getStanding().equals(1));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Munster")) {
					 assertTrue(s.getStanding().equals(3));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Toulon")) {
					 assertTrue(s.getStanding().equals(1));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Clermont Auvergne")) {
					 assertTrue(s.getStanding().equals(4));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Gloucester Rugby")) {
					 assertTrue(s.getStanding().equals(2));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Leinster")) {
					 assertTrue(s.getStanding().equals(1));
					 count++;
				 }
			 } else {
				 assertTrue(false); // did not process correctly
			 }
		 }
		 assertTrue(count == 6);
	 }
	 @Test
	 public void testSuperRugby() {


		ICompetition c = cf.get(5L);

		 assertTrue(c != null);
		 assertTrue(c.getTeams().size() == 15);

		 sFetcher = sFetcherFactory.getFetcher(c.getRounds().get(0));
		
		 assertTrue(sFetcher != null);
	 
		 sFetcher.setComp(c);
		 sFetcher.setRound(c.getRounds().get(0));
		 sFetcher.setUrl("testData/SuperRugbyTable.html");
		 sFetcher.setUc(uc);

		 Iterator<ITeamGroup> it = c.getTeams().iterator();
		 int count = 0;
		 while (it.hasNext()) {
			 ITeamGroup team = it.next();

 			 IStanding s = sFetcher.getStandingForTeam(team);

			 if (s != null) {
				 if (s.getTeam().getDisplayName().equals("Sharks")) {
					 assertTrue(s.getStanding().equals(1));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Chiefs")) {
					 assertTrue(s.getStanding().equals(3));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Hurricanes")) {
					 assertTrue(s.getStanding().equals(15));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Reds")) {
					 assertTrue(s.getStanding().equals(6));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Lions")) {
					 assertTrue(s.getStanding().equals(7));
					 count++;
				 } else if (s.getTeam().getDisplayName().equals("Force")) {
					 assertTrue(s.getStanding().equals(10));
					 count++;
				 }
			 } else {
				 assertTrue(false); // did not process correctly
			 }
		 }
		 assertTrue(count == 6);
	 }
}
