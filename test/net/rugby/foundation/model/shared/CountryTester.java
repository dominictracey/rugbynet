package net.rugby.foundation.model.shared;


import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import net.rugby.foundation.admin.server.init.CountryLoader;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })
public class CountryTester {
	  private ICountryFactory cf;

//	    private final LocalServiceTestHelper helper =
//	            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
//
//	        @Before
//	        public void setUp() {
//	            helper.setUp();
//	        }
//	  	  
//	  	  @After
//	  	  public void tearDown() {
//	  		  helper.tearDown();
//	  	  }
	  	  
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

	  /**
	   * Add as many tests as you like.
	   */
	  public void testSimple() {                                              // (3)
	    assert(true);
	  }
	  
	  @Test
	  public void testGetCountryByName() {
		  
		  ICountry c = cf.getByName("New Zealand");
		  assertTrue(c != null);
		  assertTrue(c.getAbbr().equals("NZL"));
		  
		  c = cf.getById(5001L);
		  assertTrue(c != null);
		  assertTrue(c.getAbbr().equals("NZL"));
		  
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
