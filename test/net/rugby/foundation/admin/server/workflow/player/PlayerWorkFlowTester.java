/**
 * 
 */
package net.rugby.foundation.admin.server.workflow.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.workflow.matchrating.FetchPlayerByScrumId;
import net.rugby.foundation.admin.server.workflow.matchrating.FetchPlayerMatchStats;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ AdminTestModule.class, CoreTestModule.class, Game1TestModule.class })
public class PlayerWorkFlowTester extends PipelineTest {
	//    private final LocalServiceTestHelper helper =
	//            new LocalServiceTestHelper(new LocalTaskQueueTestConfig());
	
	private transient ICompetitionFactory cf;

	private transient IPlayerFactory pf;

	private ICountryFactory countryf;

	private IPlayerMatchStatsFactory pmsf;

	private IMatchGroupFactory mf;

	private ITeamGroupFactory tf;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		// helper.setUp();
	}

	@After
	public void tearDown() throws Exception {
		//		  helper.tearDown();
		super.tearDown();
	}


	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.admin.Admin";
	}

	@Inject
	public void setFactory(ICompetitionFactory cf, IPlayerFactory pf, ICountryFactory countryf, IPlayerMatchStatsFactory pmsf, IMatchGroupFactory mf, ITeamGroupFactory tf) {
		this.cf = cf;
		this.pf = pf;
		this.countryf = countryf;
		this.pmsf = pmsf;
		this.mf = mf;
		this.tf = tf;
		((TestPlayerFactory)this.pf).setTeamFactory(tf);
	}
	
	@Test
	public void testDemo() {
		PipelineService service = PipelineServiceFactory.newPipelineService();
		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
		String pipelineId = service.startNewPipeline(new ComplexJob(), 11, 5, 7);

//		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
//        QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
//        assertEquals(1, qsi.getTaskInfo().size());
        
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				Integer p = (Integer)jobInfo.getOutput();

				Logger.getLogger(getModuleName()).log(Level.INFO, "Test value is " + p.toString());
				//Assert.assertTrue(p..);
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Test
	public void testDemoPromise() {
		PipelineService service = PipelineServiceFactory.newPipelineService();
		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
		String pipelineId = service.startNewPipeline(new ExternalAgentJob(), "dom@nator.com");

//		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
//        QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
//        assertEquals(1, qsi.getTaskInfo().size());
        
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				Integer p = (Integer)jobInfo.getOutput();

				Logger.getLogger(getModuleName()).log(Level.INFO, "Test value is " + p.toString());
				//Assert.assertTrue(p..);
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	/**
	 * Test for a player we already have in the database
	 * 
	 */
	@Test
	public void testExistingPlayer() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(), /*pf,*/ comp, "Hugo Southwell", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayer p = (IPlayer)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(p.getDisplayName().equals("Hugo Southwell"));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test for a player we don't have in the database, but is good at espnscrum.com
	 * 
	 */
	@Test
	public void testFetchNeilBest() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(), /*pf,*/ comp, "Neil Best", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 15048L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayer p = (IPlayer)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(p.getDisplayName().equals("Neil Best"));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Test for a player we don't have in the database, and isn't good at espnscrum.com
	 * 
	 */
	@Test
	public void testProblemPlayer() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(), /*pf,*/ comp, "Hugo Southwell", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 92047L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);
			Thread.sleep(15000);
			
//			IPlayer p = pf.getById(null);
//			p.setDisplayName("Spongebob");
//			service.submitPromisedValue("agR0ZXN0chULEg1waXBlbGluZS1zbG90IgIxNww", p);

			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayer pl = (IPlayer)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pl.getDisplayName().equals("Hugo Southwell"));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * Test fetching a player's match stats.
	 */
	
	@Test
	public void testFetchPlayerMatchStatsRichieMcCaw() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		cf.setId(1L);

		IPlayer p = pf.getById(9001014L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.HOME, 14, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getTacklesMade().equals(16));
				Assert.assertTrue(pms.getTimePlayed().equals(81));
				Assert.assertTrue(pms.getRuns().equals(12));
				Assert.assertTrue(pms.getPosition().equals(position.FLANKER));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFetchPlayerMatchStatsDanVickerman() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);

		IPlayer p = pf.getById(9002011L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.VISITOR, 11, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getTacklesMade().equals(7));
				Assert.assertTrue(pms.getTimePlayed().equals(56));
				Assert.assertTrue(pms.getRuns().equals(2));
				Assert.assertTrue(pms.getPosition().equals(position.LOCK));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchPlayerMatchStatsJamesSlipper() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);

		IPlayer p = pf.getById(9002017L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.VISITOR, 17, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getTacklesMade().equals(6));
				Assert.assertTrue(pms.getTimePlayed().equals(61));
				Assert.assertTrue(pms.getRuns().equals(6));
				Assert.assertTrue(pms.getPosition().equals(position.PROP));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testFetchPlayerMatchStatsRobSimmons() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);

		IPlayer p = pf.getById(9002018L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.VISITOR, 18, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getTacklesMade().equals(3));
				Assert.assertTrue(pms.getLineoutsWonOnThrow().equals(2));
				Assert.assertTrue(pms.getTimePlayed().equals(25));
				Assert.assertTrue(pms.getKicks().equals(0));
				Assert.assertTrue(pms.getPosition().equals(position.LOCK));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchPlayerMatchStatsAliWilliams() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);

		IPlayer p = pf.getById(9001018L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.HOME, 18, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getTacklesMade().equals(2));
				Assert.assertTrue(pms.getLineoutsWonOnThrow().equals(0));
				Assert.assertTrue(pms.getTimePlayed().equals(25));
				Assert.assertTrue(pms.getKicks().equals(0));
				Assert.assertTrue(pms.getPosition().equals(position.LOCK));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchPlayerMatchStatsPiriWeepu() {

		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);

		IPlayer p = pf.getById(9001007L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.HOME, 7, url);//, 14505L, 1L);

		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);


			waitForJobToComplete(pipelineId);

			jobInfo = service.getJobInfo(pipelineId);

			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );

				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();

				Assert.assertTrue(pms.getPoints().equals(12));
				Assert.assertTrue(pms.getTacklesMade().equals(3));
				Assert.assertTrue(pms.getLineoutsWonOnThrow().equals(0));
				Assert.assertTrue(pms.getTimePlayed().equals(61));
				Assert.assertTrue(pms.getKicks().equals(6));
				Assert.assertTrue(pms.getPosition().equals(position.SCRUMHALF));
			} else {
				Assert.assertTrue(false);
			}

		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testFetchPlayerMatchStatsSonnyBillWilliams() {
	
		String url = "http://www.espnscrum.com/scrum/rugby/current/match/93503.html?view=scorecard";
		mf.setId(300L);
		IMatchGroup match = mf.getGame();
		//cf.setId(1L);
	
		IPlayer p = pf.getById(9001021L);
		
		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerMatchStats(), p, match, Home_or_Visitor.HOME, 21, url);//, 14505L, 1L);
	
		// Later, check on the status and get the final output
		JobInfo jobInfo;
		try {
			jobInfo = service.getJobInfo(pipelineId);
	
	
			waitForJobToComplete(pipelineId);
	
			jobInfo = service.getJobInfo(pipelineId);
	
			JobInfo.State state = jobInfo.getJobState();
			if (JobInfo.State.COMPLETED_SUCCESSFULLY == state){
				System.out.println("Success!" );
	
				IPlayerMatchStats pms = (IPlayerMatchStats)jobInfo.getOutput();
				//String cheese = (String)jobInfo.getOutput();
	
				Assert.assertTrue(pms.getPoints().equals(0));
				Assert.assertTrue(pms.getTacklesMade().equals(0));
				Assert.assertTrue(pms.getLineoutsWonOnThrow().equals(0));
				Assert.assertTrue(pms.getTimePlayed().equals(9));
				Assert.assertTrue(pms.getYellowCards().equals(1));
				Assert.assertTrue(pms.getPosition().equals(position.CENTER));
			} else {
				Assert.assertTrue(false);
			}
	
		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
