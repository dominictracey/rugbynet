package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;


import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.util.CountryLoader;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.server.TopTenTestModule;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, TopTenTestModule.class, CoreTestModule.class, AdminTestModule.class })
public class CountryTester {
	private ICountryFactory cf;

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(ICountryFactory cf) {//, ILeaderboardFactory lbf, ILeagueFactory lf) {
		this.cf = cf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testGetCountryByName() {

		ICountry c = cf.getByName("New Zealand");
		assertTrue(c != null);
		assertTrue(c.getAbbr().equals("NZL"));

		c = cf.getById(5001L);
		assertTrue(c != null);
		assertTrue(c.getAbbr().equals("NZL"));

		assertTrue(27 == 27);
	}

	@Test
	public void testLoader()  {
		CountryLoader cl = new CountryLoader();
		cl.Run(cf);
		Iterator<Country> it = cf.getAll();
		ICountry c = it.next();
		int count = 0;
		while ( c != null) {
			Logger.getLogger("TestCountry").log(Level.INFO, c.getName());
			assertTrue(c.getAbbr() != null);
			assertTrue(c.getId() != null);
			assertTrue(c.getIrb() != null);
			assertTrue(c.getName() != null);

			count++;
			if (it.hasNext())
				c = it.next();
			else 
				c = null;
		}

		Logger.getLogger("TestCountry").log(Level.INFO, "Found " + count + " countries.");
	}

}
