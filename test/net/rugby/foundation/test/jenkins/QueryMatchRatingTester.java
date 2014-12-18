package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Inject;

import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamMatchStatsFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema20130713;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
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

	private final LocalServiceTestHelper queueHelper =
			new LocalServiceTestHelper(new LocalTaskQueueTestConfig());
	
	private boolean populated = false;
	private IQueryRatingEngineFactory qref;
	private IMatchGroupFactory mf;
	private IMatchRatingEngineSchemaFactory mresf;
	private IRatingQueryFactory rqf;


	@Before
	public void setUp() {
		helper.setUp();
		queueHelper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
//		queueHelper.tearDown();
	}

	@Inject
	public void setFactory(ITeamGroupFactory tf, IPlayerFactory pf, IMatchGroupFactory mf, ITeamMatchStatsFactory tmsf, IPlayerMatchStatsFactory pmsf, 
			IQueryRatingEngineFactory qref, IMatchRatingEngineSchemaFactory mresf, IRatingQueryFactory rqf) {
		this.tf = tf;
		this.pf = pf;
		this.mf = mf;
		this.tmsf = tmsf;
		this.pmsf = pmsf;
		this.qref = qref;
		this.mresf = mresf;
		this.rqf = rqf;
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
//
//		addMatch(100L, qre);
//		addMatch(101L, qre);
//		addMatch(102L, qre);
		IRatingQuery rq = rqf.get(700L);
		assertTrue (rq != null);
		qre.setQuery(rq);
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
	}
	
//	private void addMatch(Long mid, IQueryRatingEngine qre) {
//		qre.addPlayerStats(pmsf.getByMatchId(mid));
//		IMatchGroup m = mf.get(mid);
//		List<ITeamMatchStats> list = new ArrayList<ITeamMatchStats>();
//		list.add(tmsf.getHomeStats(m));
//		list.add(tmsf.getVisitStats(m));
//		qre.addTeamStats(list);
//	}
	
//	@Test
//	public void testGenerate700() {
//
//		IQueryRatingEngine qre = qref.get(mresf.getDefault());
//
//		IRatingQuery rq = rqf.get(700L);
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(), true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
//
//	@Test
//	public void testGenerate701() {
//
//		IQueryRatingEngine qre = qref.get(mresf.getDefault());
//
//		IRatingQuery rq = rqf.get(701L);
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(), true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
//	
//	@Test
//	public void testGenerate702() {
//
//		IQueryRatingEngine qre = qref.get(mresf.getDefault());
//
//		IRatingQuery rq = rqf.get(702L);
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(), true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
//	
//	@Test
//	public void testGenerate703() {
//
//		IQueryRatingEngine qre = qref.get(mresf.getDefault());
//
//		IRatingQuery rq = rqf.get(703L);
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(), true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
//	
//	@Test
//	public void testGenerate704() {
//
//		IRatingQuery rq = rqf.get(704L);
//		IQueryRatingEngine qre = qref.get(mresf.getDefault(), rq);
//		
//		assertTrue(rq.isTimeSeries());
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(),true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		rq = rqf.get(704L);
//		assertTrue(rq.getStatus() == Status.COMPLETE);
//		
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
//	
//	@Test
//	public void testGenerate705() {
//
//		IRatingQuery rq = rqf.get(705L);
//		IQueryRatingEngine qre = qref.get(mresf.getDefault(), rq);
//		
//		assertTrue(rq.isTimeSeries());
//		
//		qre.setQuery(rq);
//		qre.generate(mresf.getDefault(),true,true,true);
//		
//		StatisticalSummary ss = qre.getStatisticalSummary();
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		
//		assertTrue(ss.getMean()>498 && ss.getMean()<502);
//		rq = rqf.get(705L);
//		assertTrue(rq.getStatus() == Status.COMPLETE);
//		
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ss.toString());
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, qre.toString());
//	}
	
//	@Test
//	public void testOrchestrationQueue() {
//		// put the query in the database
//		IRatingQuery rq = rqf.create();
//		rq.getCompIds().add(1L);
//		rq.getRoundIds().add(2L);
//
//		rq = rqf.put(rq);
//		
//		// now trigger the backend processing task
//		Queue queue = QueueFactory.getDefaultQueue();
//	    TaskOptions to = Builder.withUrl("/admin/orchestration/IRatingQuery").
//	    		param(AdminOrchestrationActions.RatingActions.getKey(), RatingActions.GENERATE.toString()).
//	    		param(AdminOrchestrationTargets.Targets.getKey(), AdminOrchestrationTargets.Targets.RATING.toString()).
//	    		param("id",rq.getId().toString()).
//	    		param("extraKey", "0L");
//	    		
//	    queue.add(to);	
//	    
//	}
}