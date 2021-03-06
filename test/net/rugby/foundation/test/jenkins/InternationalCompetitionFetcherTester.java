package net.rugby.foundation.test.jenkins;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IForeignCompetitionFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IForeignCompetitionFetcher;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class InternationalCompetitionFetcherTester {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IUrlCacher uc;
	private IMatchGroupFactory mf;

	private ITeamGroupFactory tf;

	private IForeignCompetitionFetcher cFetcher;

	private ICompetitionFactory cf;

	private IForeignCompetitionFetcherFactory cff;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IUrlCacher uc, IPlayerFactory pf, IMatchGroupFactory mf, ITeamGroupFactory tf, ICompetitionFactory cf, IForeignCompetitionFetcherFactory cff, IStandingFactory sf) {
		this.uc = uc;
		this.mf = mf;
		this.tf = tf;
		this.cff = cff;
		this.cf = cf;
	}
	
//	@Inject
//    public void setFactory(IUrlCacher uc, IPlayerFactory pf, IMatchGroupFactory mf, ITeamGroupFactory tf, ICompetitionFactory cf, ICompetitionFetcherFactory cff, IStandingFactory sf) {
//        this.uc = uc;
//        this.mf = mf;
//        this.tf = tf;
//        this.cff = cff;
//        this.cf = cf;
//    }

	/**
	 * Must refer to a valid module that sources this class.
	 */
	 public String getModuleName() { // (2)
		 return "net.rugby.foundation.model.Model";
	 }

//	 @Test
//	 public void testAutumnInternationals() {
//
//		 String url  = "testData\\194567-AUT-INTLS-Fixtures.htm";
//		 cFetcher = cff.getForeignCompetitionFetcher(url, CompetitionFetcherType.ESPNSCRUM_INTERNATIONALS );
//
//		 Map<String, ITeamGroup> teams  = cFetcher.getTeams();
//		 Map<String, IMatchGroup> matches = cFetcher.getMatches(url, teams);
//
//		 List<IRound> rounds = cFetcher.getRounds(url, matches);
//		 
//		 ICompetition c = cFetcher.getCompetition(url, rounds, (List<ITeamGroup>)teams.values());
//		 
//		 Iterator<ITeamGroup> it = c.getTeams().iterator();
//
//		 // check teams
//		 
//		 // check num of teams
//		 
//		 // check num rounds
//		 
//		 // check start and end of the first and last round
//		 
//		 // check name of round
//		 
//		 // check a match's date 
//		 
//		 // check match in first round
//		 
//		 // check match in third round
//		 
//		 // check match with New Zealand (two words)
//		 
//		 // check match on Sunday
//		 
//		 // check match on Friday
//	 }
	 
	 @Test
	 public void testGetMatches() {
	     String url  = "testData\\194567-AUT-INTLS-Fixtures.htm";
         cFetcher = cff.getForeignCompetitionFetcher(url, CompetitionType.AUTUMN_INTERNATIONALS );
         cFetcher.setURL(url);
         Map<String, ITeamGroup> teams = cFetcher.getTeams();
         Map<String, IMatchGroup> matches = cFetcher.getMatches(url, teams);
         
         assertTrue(matches != null);
         assertTrue(matches.size() > 0);
	 }
	 
	 @Test
	 public void testGetRounds() {
//       public List<IRound> getRounds(String url, Map<String, IMatchGroup> matches);
	     String url  = "testData\\194567-AUT-INTLS-Fixtures.htm";
         cFetcher = cff.getForeignCompetitionFetcher(url, CompetitionType.AUTUMN_INTERNATIONALS );
         cFetcher.setURL(url);
         Map<String, ITeamGroup> teams = cFetcher.getTeams();
         Map<String, IMatchGroup> matches = cFetcher.getMatches(url, teams);
         assertTrue(teams != null);
         assertTrue(matches != null);
         
         List<IRound> rounds = cFetcher.getRounds(url,  matches);
         assertTrue(rounds != null);
         assertTrue(rounds.size() > 0);
	 }
	 
	 @Test
	 public void testSetUrl() {
//       public void setURL(String url);
	 }
	 
	 @Test
	 public void testGetTeams() {
	     String url  = "testData\\194567-AUT-INTLS-Fixtures.htm";
         cFetcher = cff.getForeignCompetitionFetcher(url, CompetitionType.AUTUMN_INTERNATIONALS );

         Map<String, ITeamGroup> teams  = cFetcher.getTeams();
         
         assertTrue(teams!= null);
         assertTrue(teams.values().size() > 0);
	 }
	 
	 @Test
	 public void testGetCompetition() {
//	 public ICompetition getCompetition(String homePage, List<IRound> rounds, List<ITeamGroup> teams);
	     
	     String url  = "testData\\194567-AUT-INTLS-Fixtures.htm";
         cFetcher = cff.getForeignCompetitionFetcher(url, CompetitionType.AUTUMN_INTERNATIONALS );

         Map<String, ITeamGroup> teams = cFetcher.getTeams();
         Map<String, IMatchGroup> matches = cFetcher.getMatches(url, teams);
         assertTrue(teams != null);
         assertTrue(matches != null);
         
         List<IRound> rounds = cFetcher.getRounds(url,  matches);
         assertTrue(rounds != null);
         assertTrue(rounds.size() > 0);
         
         List<ITeamGroup> teamVals = new ArrayList<ITeamGroup>();
         for(ITeamGroup t : teams.values()) {
             teamVals.add(t);
         }
         
         ICompetition comp = cFetcher.getCompetition(url, rounds, teamVals);
         assertTrue(comp != null);
         
         assertTrue(comp.getLongName() != null);
         
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
