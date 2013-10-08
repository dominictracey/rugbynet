package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class MatchResultFetcherTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IUrlCacher uc;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IUrlCacher uc) {//, ILeaderboardFactory lbf, ILeagueFactory lf) {
		this.uc = uc;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testTestUrlFetcher() {
		uc.setUrl("match1");
		List<String> list = uc.get();
		assertTrue(list.size() == 2335);
	}
	
	@Test
	public void testTestMatchStatsFetcher() {
		uc.setUrl("match1");
		List<String> list = uc.get();
		assertTrue(list.size() == 2335);
	}

}
