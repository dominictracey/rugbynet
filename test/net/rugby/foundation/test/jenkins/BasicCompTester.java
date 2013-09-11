/**
 * 
 */
package net.rugby.foundation.test.jenkins;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })
public class BasicCompTester {
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


	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void TestSimple() {
		assertTrue(true);
	}

	private ICompetitionFactory cf;
	private ITeamGroupFactory tf;

	@Inject
	public void setFactory(ICompetitionFactory cf, ITeamGroupFactory tf) {

		this.cf = cf;
		this.tf = tf;
	}

	@Test
	public void testGetAllComps() {

		List<ICompetition> comps = cf.getAllComps();


		Assert.assertTrue(comps.size() == 2);
	}

	@Test
	public void testGetUnderwayComps() {

		List<ICompetition> comps = cf.getUnderwayComps();


		Assert.assertTrue(comps.size() == 2);
	}

	@Test
	public void testNumRounds() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();


		Assert.assertTrue(comp.getRounds().size() == 4);

		cf.setId(2L);
		comp = cf.getCompetition();


		Assert.assertTrue(comp.getRounds().size() == 3);
	}	  

	@Test
	public void testNumTeams() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();


		Assert.assertTrue(comp.getTeams().size() == 6);

		cf.setId(2L);
		comp = cf.getCompetition();


		Assert.assertTrue(comp.getTeams().size() == 8);
	}	  

	@Test
	public void testNextAndPrevRounds() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();


		Assert.assertTrue(comp.getNextRound().getAbbr().equals("3"));
		Assert.assertTrue(comp.getPrevRound().getAbbr().equals("2"));

		cf.setId(2L);
		comp = cf.getCompetition();

		Assert.assertTrue(comp.getNextRound().getAbbr().equals("2"));
		Assert.assertTrue(comp.getPrevRound().getAbbr().equals("1"));


		Assert.assertTrue(comp.getTeams().size() == 8);
	}	  

	@Test
	public void testNumMatches() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		int count = 0;
		for (IRound r: comp.getRounds()) {
			count+= r.getMatches().size();
		}

		Assert.assertTrue(count == 9);

		cf.setId(2L);
		comp = cf.getCompetition();

		count = 0;
		for (IRound r: comp.getRounds()) {
			count+= r.getMatches().size();
		}

		Assert.assertTrue(count == 8);
	}	  

	@Test
	public void testMatchResults() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		int count = 0;
		for (IRound r: comp.getRounds()) {
			for (IMatchGroup m: r.getMatches()) {
				if (m.getSimpleScoreMatchResult() != null)
					++count;
			}
		}

		Assert.assertTrue(count == 4);

		cf.setId(2L);
		comp = cf.getCompetition();

		count = 0;
		for (IRound r: comp.getRounds()) {
			for (IMatchGroup m: r.getMatches()) {
				if (m.getSimpleScoreMatchResult() != null)
					++count;
			}
		}

		Assert.assertTrue(count == 3);		  
	}	

	@Test
	public void testGetTeamByName() {

		ITeamGroup nz = tf.getTeamByName("New Zealand");

		Assert.assertTrue(nz.getAbbr().equals("NZL"));
	}
}
