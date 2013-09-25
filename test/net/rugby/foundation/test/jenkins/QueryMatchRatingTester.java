package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamMatchStats;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class QueryMatchRatingTester {
	private ITeamGroupFactory tf;
	private IPlayerMatchStatsFactory pmsf;
	private IPlayerFactory pf;
	private ITeamMatchStatsFactory tmsf;
	
	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());


	private boolean populated = false;
	private IQueryRatingEngineFactory qref;
	private IMatchGroupFactory mf;
	private IMatchRatingEngineSchemaFactory mresf;


	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(ITeamGroupFactory tf, IPlayerFactory pf, IMatchGroupFactory mf, ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf, 
			IQueryRatingEngineFactory qref, IMatchRatingEngineSchemaFactory mresf) {
		this.tf = tf;
		this.pf = pf;
		this.mf = mf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.qref = qref;
		this.mresf = mresf;
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

	@Test
	public void testCreateEngine() {
		
		IQueryRatingEngine qre = qref.get(new ScrumMatchRatingEngineSchema20130713());
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
	}
	
	@Test
	public void testPopulate() {
		IQueryRatingEngine qre = qref.get(new ScrumMatchRatingEngineSchema20130713());

		addMatch(100L, qre);
		addMatch(101L, qre);
		addMatch(102L, qre);
		
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
	}
	
	private void addMatch(Long mid, IQueryRatingEngine qre) {
		qre.addPlayerStats(pmsf.getByMatchId(mid));
		IMatchGroup m = mf.get(mid);
		List<ITeamMatchStats> list = new ArrayList<ITeamMatchStats>();
		list.add(tmsf.getHomeStats(m));
		list.add(tmsf.getVisitStats(m));
		qre.addTeamStats(list);
	}
	
	@Test
	public void testGenerate1() {

		IQueryRatingEngine qre = qref.get(new ScrumMatchRatingEngineSchema20130713());

		addMatch(100L, qre);
		addMatch(101L, qre);
		addMatch(102L, qre);
		
		qre.generate(mresf.getDefault());
		
		StatisticalSummary ss = qre.getStatisticalSummary();
		
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
	}

}
