/**
 * 
 */
package net.rugby.foundation.admin.server;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.workflow.IWorkflow;
import net.rugby.foundation.admin.server.workflow.IWorkflowFactory;
import net.rugby.foundation.admin.shared.CompetitionWorkflow;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ AdminTestModule.class, CoreTestModule.class, Game1TestModule.class })
public class WorkFlowTester {
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
	
	  private IWorkflow wf;
	  private IWorkflowFactory wff;
	  
	  @Inject
	  public void setFactory(IWorkflowFactory wff) {
			this.wff = wff;
	  }

	  @Test
	  public void testComp1Workflow() throws InterruptedException {
		
		  wff.setId(40001L);
		  wf = wff.get();
		  wf.setLog(new ArrayList<String>());
		  
		  wf.process(CompetitionWorkflow.Events.CronCheck.toString(),Long.toString(1L),null);
						
		  Assert.assertTrue(wf.getLog().size() == 7);
	  }
}
