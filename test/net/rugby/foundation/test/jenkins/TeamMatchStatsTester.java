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
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })
public class TeamMatchStatsTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	private ITeamMatchStatsFactory tmsf;
	private IMatchGroupFactory mf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(ITeamMatchStatsFactory tmsf, IMatchGroupFactory mf) {
		this.tmsf = tmsf;
		this.mf = mf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testGetTMS() {
		IMatchGroup m = mf.get(400L);
		
		assertTrue(m != null);
		
		ITeamMatchStats tms = tmsf.getHomeStats(m);
		assertTrue(tms != null);
		assertTrue(tms.getScrumsPutIn() >= tms.getScrumsWonOnOwnPut());
		assertTrue(tms.getRucks() >= tms.getRucksWon());
		assertTrue(tms.getMauls() >= tms.getMaulsWon());
		assertTrue(tms.getLineoutsThrownIn() >= tms.getLineoutsWonOnOwnThrow());

	}

}
