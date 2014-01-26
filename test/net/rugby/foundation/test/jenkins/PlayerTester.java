package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.admin.server.AdminTestModule;
//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class PlayerTester {
	private ITeamGroupFactory tf;

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IPlayerFactory pf;
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
	public void setFactory(ITeamGroupFactory tf, IPlayerFactory pf) {//, ILeaderboardFactory lbf, ILeagueFactory lf) {
		this.tf = tf;
		this.pf = pf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	private boolean populate() {
		if (!populated) {
			((TestPlayerFactory)pf).Populate();
			populated = true;
		}
		return populated;
	}
	/**
	 * Add as many tests as you like.
	 */
	public void testSimple() {                                              // (3)
		assert(true);
	}

//	@Test
//	public void testGetNewZealandPlayers() {
//
//		ITeamGroup t = tf.getTeamByName("New Zealand");
//		assertTrue(t != null);
//		assertTrue(t.getAbbr().equals("NZL"));
//
//		Iterator<IPlayer> it = ((IGroup)t).getMembers();
//		int count = 0;
//		IPlayer p;
//		while (it.hasNext()) {
//			p = it.next();
//			Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + p.getDisplayName());
//			count++;
//		}
//
//		Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + count + " players for " + t.getAbbr());
//
//	}
//	@Test
//	public void testGetAustraliaPlayers() {
//
//		ITeamGroup t = tf.getTeamByName("Australia");
//		assertTrue(t != null);
//		assertTrue(t.getAbbr().equals("AUS"));
//
//		Iterator<IPlayer> it = ((IGroup)t).getMembers();
//		int count = 0;
//		IPlayer p;
//		while (it.hasNext()) {
//			p = it.next();
//			Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + p.getDisplayName());
//			count++;
//		}
//
//		Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + count + " players for " + t.getAbbr());
//
//	}
	
	@Test
	public void testGetRichieMcCaw() {
		populate();
		IPlayer p = pf.get(9001014L);
		assertTrue(p.getDisplayName().equals("Richie McCaw"));
		
		IPlayer p2 = pf.getByScrumId(13784L);
		assertTrue(p.getDisplayName().equals("Richie McCaw"));
		
		p2.setDisplayName("Richard McCaw");
		pf.put(p2);
		
		IPlayer p3 = pf.get(9001014L);
		assertTrue(p3.getDisplayName().equals("Richard McCaw"));
		
	}
	
	@Test
	public void testCreate() {
		populate();
		IPlayer p = pf.create();
		assertTrue(p.getDisplayName() == null);
		
		p.setDisplayName("Dominic Tracey");
		p.setId(198099128391L);
		pf.put(p);
		
		IPlayer p2 = pf.get(198099128391L);
		assertTrue(p2.getDisplayName().equals("Dominic Tracey"));
		
	}

}
