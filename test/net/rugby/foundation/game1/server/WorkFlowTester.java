/**
 * 
 */
package net.rugby.foundation.game1.server;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflowConfigurationFactory;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IClubhouseFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.BPM.WorkflowCompetition;
import net.rugby.foundation.game1.server.BPM.ICoreRuleFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Inject;
import junit.framework.Assert;

//import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
//import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
//import com.google.appengine.api.taskqueue.QueueFactory;
//import com.google.appengine.api.taskqueue.TaskOptions;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
//import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;


/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class WorkFlowTester {
	
	private ICompetitionFactory cf;
	private ICoreRuleFactory crf;
	private IClubhouseLeagueMapFactory chlmf;
	private ILeagueFactory lf;
	private IClubhouseFactory chf;
	private IMatchEntryFactory mef;
	private IRoundEntryFactory ref;
	private IEntryFactory ef;

	private IWorkflow wf;
	  
	public WorkFlowTester() {
		
	}
	
	@Inject
	public void SetFactories(ICompetitionFactory cf, ICoreRuleFactory crf, 
			IClubhouseLeagueMapFactory chlmf, ILeagueFactory lf, IClubhouseFactory chf,
			IMatchEntryFactory mef, IRoundEntryFactory ref, IEntryFactory ef) {
		this.cf = cf;
		this.crf = crf;
		this.chlmf = chlmf;
		this.lf = lf;
		this.chf = chf;
		this.mef = mef;
		this.ref = ref;
		this.ef = ef;
		
		//wf = new WorkflowCompetition(chlmf, cf, (ICoreRuleFactory) crf, lf, chf, mef, ref, ef);  //@REX something screwy w/ 3rd param

	}
	
	
    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalTaskQueueTestConfig());

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
	    return "net.rugby.foundation.admin.Admin";
	  }
	



	  @Test
	  public void testComp1Workflow() throws InterruptedException {
		
		  wf.setLog(new ArrayList<String>());
		  //wf.process();
						
//	        QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withTaskName("task29"));
//	        // give the task time to execute if tasks are actually enabled (which they
//	        // aren't, but that's part of the test)
//	        Thread.sleep(1000);
//	        LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
//	        QueueStateInfo qsi = ltq.getQueueStateInfo().get(QueueFactory.getDefaultQueue().getQueueName());
//	        assertEquals(1, qsi.getTaskInfo().size());
//	        assertEquals("task29", qsi.getTaskInfo().get(0).getTaskName());

	        
		  Assert.assertTrue(wf.getLog().size() == 7);
	  }
}
