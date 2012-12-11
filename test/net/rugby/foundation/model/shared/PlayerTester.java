package net.rugby.foundation.model.shared;


import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.init.CountryLoader;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.core.server.factory.test.TestTeamFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })
public class PlayerTester {
	  private ITeamGroupFactory tf;

	    private final LocalServiceTestHelper helper =
	            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

		private IPlayerFactory pf;

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
			((TestPlayerFactory)pf).setTeamFactory(tf);
	  }
	  /**
	   * Must refer to a valid module that sources this class.
	   */
	  public String getModuleName() {                                         // (2)
	    return "net.rugby.foundation.model.Model";
	  }

	  /**
	   * Add as many tests as you like.
	   */
	  public void testSimple() {                                              // (3)
	    assert(true);
	  }
	  
	  @Test
	  public void testGetCountryByName() {
		  
		  ITeamGroup t = tf.getTeamByName("New Zealand");
		  assertTrue(t != null);
		  assertTrue(t.getAbbr().equals("NZL"));
		  
		  Iterator<IPlayer> it = ((IGroup)t).getMembers();
		  int count = 0;
		  IPlayer p;
		  while (it.hasNext()) {
			  p = it.next();
			  Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + p.getDisplayName());
			  count++;
		  }
		  
		  Logger.getLogger("TestPlayer").log(Level.INFO, "Found " + count + " players for " + t.getAbbr());
		  
	  }

//	  @Test
//	  public void testLoader()  {
//		  CountryLoader cl = new CountryLoader();
//		  cl.Run(cf);
//		  Iterator<Country> it = cf.getAll();
//		  ICountry c = it.next();
//		  int count = 0;
//		  while ( c != null) {
//			  Logger.getLogger("TestCountry").log(Level.INFO, c.getName());
//			  assertTrue(c.getAbbr() != null);
//			  assertTrue(c.getId() != null);
//			  assertTrue(c.getIrb() != null);
//			  assertTrue(c.getName() != null);
//
//			  count++;
//			  if (it.hasNext())
//				  c = it.next();
//			  else 
//				  c = null;
//		  }
//		  
//		  Logger.getLogger("TestCountry").log(Level.INFO, "Found " + count + " countries.");
//	  }

}
