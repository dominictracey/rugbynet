/**
 * 
 */
package net.rugby.foundation.admin.server.workflow.player;

import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.workflow.matchrating.FetchPlayerByScrumId;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.JobInfo;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;
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
	public void setFactory(ICompetitionFactory cf, IPlayerFactory pf, ICountryFactory countryf) {
		this.cf = cf;
		this.pf = pf;
		this.countryf = countryf;
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
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(pf, countryf), /*pf,*/ comp, "Hugo Southwell", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 14505L, 1L);

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
	public void testRemotePlayer() {

		cf.setId(1L);
		ICompetition comp = cf.getCompetition();

		PipelineService service = PipelineServiceFactory.newPipelineService();
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(pf, countryf), /*pf,*/ comp, "Neil Best", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 15048L, 1L);

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
		String pipelineId = service.startNewPipeline(new FetchPlayerByScrumId(pf, countryf), /*pf,*/ comp, "Hugo Southwell", "http://www.espnscrum.com/anglo-welsh-cup-2012-13/rugby/match/168022.html", 92047L, 1L);

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
}
