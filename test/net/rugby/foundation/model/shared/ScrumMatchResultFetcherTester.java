package net.rugby.foundation.model.shared;


import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.factory.espnscrum.ScrumResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })
public class ScrumMatchResultFetcherTester {
	  private IMatchGroupFactory mgf;
	  //private ICompetitionFactory cf;
//	private ILeaderboardFactory lbf;
//	private ILeagueFactory lf;
	    private final LocalServiceTestHelper helper =
	            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	        @Before
	        public void setUp() {
	            helper.setUp();
	        }
	  	  
	  	  @After
	  	  public void tearDown() {
	  		  helper.tearDown();
	  	  }
	  	  
	  @Inject
	  public void setFactory(IMatchGroupFactory mgf) {//, ILeaderboardFactory lbf, ILeagueFactory lf) {
			this.mgf = mgf;
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
	  public void testCreateMatch() throws ParseException {
		  
		  mgf.setId(200L);
		  IMatchGroup mg = mgf.getGame();
		  
////		  DateTimeFormat dateFormatter = DateTimeFormat.getFormat("MMM dd, yyyy hh:mm z");
//		  DateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy hh:mm z");
//
//		  Date date = dateFormatter.parse("Oct 9, 2011 14:00 GMT");
//		  mg.setDate(date);
		  
		  ScrumResultFetcherFactory factory = new ScrumResultFetcherFactory();

		  IResultFetcher fetcher = factory.getResultFetcher(2L, null, IMatchResult.ResultType.SIMPLE_SCORE);
		  
		  
		  IMatchResult mr = fetcher.getResult(mg); //, "http://www.espnscrum.com/premiership-2011-12/rugby/series/142402.html", "London Wasps", "Bath Rugby");
		  
//		  assertTrue(mr != null);
//		  assertTrue(mr instanceof SimpleScoreMatchResult);
//		  assertTrue(((ISimpleScoreMatchResult)mr).getHomeScore() == 27);
//		  assertTrue(((ISimpleScoreMatchResult)mr).getVisitScore() == 24);
		  
	  }


}
