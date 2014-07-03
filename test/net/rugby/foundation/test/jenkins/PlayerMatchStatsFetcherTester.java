package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IPlayerMatchStatsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IPlayerMatchStatsFetcher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class PlayerMatchStatsFetcherTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IUrlCacher uc;

	private IPlayerMatchStatsFetcherFactory pmsff;

	private IPlayerFactory pf;

	private IMatchGroupFactory mf;

	private ITeamGroupFactory tf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IUrlCacher uc, IPlayerFactory pf, IMatchGroupFactory mf, ITeamGroupFactory tf, IPlayerMatchStatsFetcherFactory pmsff) {
		this.uc = uc;
		this.pf = pf;
		this.mf = mf;
		this.tf = tf;
		this.pmsff = pmsff;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}
	
	@Test
	public void testJohnBarclay() {  // this one should work right off - the test timeline is rightside up. He went to the blood bin for 7 minutes, then
										// was subbed off at the end of the match.
		IPlayer p = pf.create();
		p.setScrumId(15578L);
		p.setSurName("Barclay");
		p.setDisplayName("John Barclay");
		p.setShortName("J Barclay");

		IMatchGroup m = getQuinsScarlets();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.VISITOR, 13, "testData\\191605-QUI-SCA-rightsideup.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(62));
		} else {
			assertTrue(false); // did not process correctly
		}
	}


	@Test
	public void testSamWarburton() {
		IPlayer p = pf.create();
		p.setScrumId(94300L);
		p.setSurName("Warburton");
		p.setDisplayName("Sam Warburton");
		p.setShortName("S Warburton");

		IMatchGroup m = getExeterCardiff();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.VISITOR, 13, "testData\\191613-EXE-CAR-upsidedown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(80));
		} else {
			assertTrue(false); // did not process correctly
		}
	}

	@Test
	public void testScottAndrews() {
		IPlayer p = pf.create();
		p.setScrumId(95749L);
		p.setSurName("Andrews");
		p.setDisplayName("Scott Andrews");
		p.setShortName("S Andrews");

		IMatchGroup m = getExeterCardiff();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.VISITOR, 17, "testData\\191613-EXE-CAR-upsidedown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(29));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testGarethDavies() {
		IPlayer p = pf.create();
		p.setScrumId(104681L);
		p.setSurName("Davies");
		p.setDisplayName("Gareth Davies");
		p.setShortName("G Davies");   // << NOTE THERE ARE TWO PEOPLE PLAYING FOR CARDIFF WITH THE LAST NAME OF DAVIES!

		IMatchGroup m = getExeterCardiff();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.VISITOR, 21, "testData\\191613-EXE-CAR-upsidedown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(5));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testChrisWhitehead() {
		IPlayer p = pf.create();
		p.setScrumId(104692L);
		p.setSurName("Whitehead");
		p.setDisplayName("Chris Whitehead");
		p.setShortName("C Whitehead");  

		IMatchGroup m = getExeterCardiff();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.HOME, 8, "testData\\191613-EXE-CAR-upsidedown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(48));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testKaiHorstmann() {
		IPlayer p = pf.create();
		p.setScrumId(27757L);
		p.setSurName("Horstmann");
		p.setDisplayName("Kai Horstmann");
		p.setShortName("K Horstmann");  

		IMatchGroup m = getExeterCardiff();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.HOME, 19, "testData\\191613-EXE-CAR-upsidedown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(47));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testMartinCastrogiovanni() { // left, then came back
		IPlayer p = pf.create();
		p.setScrumId(13946L);
		p.setSurName("Castrogiovanni");
		p.setDisplayName("Martin Castrogiovanni");
		p.setShortName("M Castrogiovanni");  

		IMatchGroup m = getToulonGlasgow();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.HOME, 9, "testData\\191615-TOU-GLA-upsideDown.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(60));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testRichieMcCaw() { // left, then came back
		IPlayer p = pf.create();
		p.setScrumId(13784L);
		p.setSurName("McCaw");
		p.setDisplayName("Richie McCaw");
		p.setShortName("RH McCaw");  

		IMatchGroup m = getnzleng();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.HOME, 9, "testData\\Rugby Union - ESPN Scrum - New Zealand v England at Hamilton.htm");
		
		if (fetcher.process()) {
		
			IPlayerMatchStats pms = fetcher.getStats();
			
			assertTrue(pms != null);
			assertTrue(pms.getTimePlayed().equals(82));
		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	@Test
	public void testDanniRossouw() { // So he is in a funny position so the parser should complain!
		// look for RuntimeException("Could not match the reserve player coming on to anyone coming off so we could determine what his position was.")

		IPlayer p = pf.create();
		p.setScrumId(13946L);
		p.setSurName("Rossouw");
		p.setDisplayName("Danni Rossouw");
		p.setShortName("D Rossouw");  

		IMatchGroup m = getToulonGlasgow();
		
		
		IPlayerMatchStatsFetcher fetcher = pmsff.getResultFetcher(p, m, Home_or_Visitor.HOME, 22, "testData\\191615-TOU-GLA-upsideDown.htm");
		IPlayerMatchStats pms = null;	
		if (fetcher.process()) {
			boolean caught = false;
			try {			
				pms = fetcher.getStats();
			} catch (RuntimeException ex) {
				assertTrue(ex.getMessage().equals("Could not match the reserve player coming on to anyone coming off so we could determine what his position was."));
				caught = true;
			}
				
			assertTrue(caught);

		} else {
			assertTrue(false); // did not process correctly
		}
	}
	
	private IMatchGroup getExeterCardiff() {
		IMatchGroup m = mf.create();
		m.setForeignId(191613L);
		
		ITeamGroup v = tf.create();
		v.setId(99L);
		v.setAbbr("CAR");
		m.setVisitingTeam(v);
		ITeamGroup h = tf.create();
		h.setId(98L);
		h.setAbbr("EXE");
		m.setHomeTeam(h);
		return m;
	}
	
	private IMatchGroup getToulonGlasgow() {
		IMatchGroup m = mf.create();
		m.setForeignId(191613L);
		
		ITeamGroup v = tf.create();
		v.setId(97L);
		v.setAbbr("GLA");
		m.setVisitingTeam(v);
		ITeamGroup h = tf.create();
		h.setId(96L);
		h.setAbbr("TLN");
		m.setHomeTeam(h);
		return m;
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
	
	private IMatchGroup getnzleng() {
		IMatchGroup m = mf.create();
		m.setForeignId(207953L);
		
		ITeamGroup v = tf.create();
		v.setId(98L);
		v.setAbbr("ENG");
		m.setVisitingTeam(v);
		ITeamGroup h = tf.create();
		h.setId(99L);
		h.setAbbr("NZL");
		m.setHomeTeam(h);
		return m;
	}
}
