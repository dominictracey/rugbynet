package net.rugby.foundation.admin.server;


import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class PlayerMatchStatsTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IPlayerFactory pf;

	private IMatchGroupFactory mf;

	private IPlayerMatchStatsFetcherFactory pmsff;

	private boolean populated = false;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IPlayerFactory pf, IMatchGroupFactory mf, IPlayerMatchStatsFetcherFactory pmsff) {//, ILeaderboardFactory lbf, ILeagueFactory lf) {
		this.pf = pf;
		this.mf = mf;
		this.pmsff = pmsff;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testGetVisitStarter() {
		populate();
		
		IPlayer player = pf.get(9211001L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		int slot = 7;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 51);
		}

	}
	
	@Test
	public void testGetVisitSub1() {
		populate();
		
		IPlayer player = pf.get(9211002L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		int slot = 16;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 30);
		}

	}
	
	@Test
	public void testGetVisitSub2() {
		populate();
		
		IPlayer player = pf.get(9211006L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		int slot = 15;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getPosition().equals(position.HOOKER));
		}

	}
	
	private boolean populate() {
		if (!populated ) {
			((TestPlayerFactory)pf).Populate();
			populated = true;
		}
		return populated;
	}
	
	@Test
	public void testGetHomeStarter1() {
		populate();
		
		IPlayer player = pf.get(9211003L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		int slot = 10;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 77);
		}

	}
	
	
	@Test
	public void testGetHomeSub1() {
		populate();
		
		IPlayer player = pf.get(9211004L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		int slot = 18;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 4);
			assertTrue(stats.getCountryId().equals(5005L));
			assertTrue(stats.getPosition().equals(position.LOCK));
		}

	}
	
	@Test
	public void testGetHomeSub2() {
		populate();
		
		IPlayer player = pf.get(9211007L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		int slot = 17;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 25);
			assertTrue(stats.getCountryId().equals(5005L));
			assertTrue(stats.getPosition().equals(position.PROP));
		}

	}
	
	@Test
	public void testNoHomeRunOn() {
		populate();
		
		IPlayer player = pf.get(9211005L);
		IMatchGroup match = mf.get(400L);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		int slot = 21;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188689.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 0);
			assertTrue(stats.getCountryId().equals(5005L));
			assertTrue(stats.getPosition().equals(position.NONE));
		}

	}
	
	@Test
	public void testLirSarMakoVunipola() {
		populate();
		
		IPlayer player = pf.get(9211009L);
		IMatchGroup match = mf.get(401L);
		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		int slot = 16;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188683.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 33);
			assertTrue(stats.getPosition().equals(position.PROP));
		}

	}
	
	@Test
	public void testLirSarMattStevens() {
		populate();
		
		IPlayer player = pf.get(9211008L);
		IMatchGroup match = mf.get(401L);
		Home_or_Visitor hov = Home_or_Visitor.VISITOR;
		int slot = 9;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188683.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getTimePlayed() == 78);
			assertTrue(stats.getYellowCards() == 1);
		}

	}
	
	@Test
	public void testLirSarJimmyStevens() {
		populate();
		
		IPlayer player = pf.get(9211010L);
		IMatchGroup match = mf.get(401L);
		Home_or_Visitor hov = Home_or_Visitor.HOME;
		int slot = 15;
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/188683.html?view=scorecard";

		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(player, match, hov, slot, url);
		
		IPlayerMatchStats stats = null;

		if (fetcher.process()) {
			stats = fetcher.getStats();
			assertTrue(stats.getPosition().equals(position.HOOKER));
			assertTrue(stats.getTimePlayed() == 11);
			assertTrue(stats.getPenaltiesConceded() == 1);
		}

	}
}
