/**
 * 
 */
package net.rugby.foundation.admin.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.orchestration.IOrchestration;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ AdminMainModule.class, CoreTestModule.class })
public class OrchestrationCompTester {
	
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
  	  
	  /**
	   * Must refer to a valid module that sources this class.
	   */
	  public String getModuleName() {                                         // (2)
	    return "net.rugby.foundation.admin.Admin";
	  }
	
	  private IOrchestrationFactory of;
	  private ICompetitionFactory cf;
	  
	  @Inject
	  public void setFactory(IOrchestrationFactory of, ICompetitionFactory cf) {
			this.of = of;
			this.cf = cf;
	  }
	  
	  @Test
	  public void testUpdateNextAndPrevOrchestration() {
						
			cf.setId(1L);
			IOrchestration<ICompetition> compUpdater = of.get(cf.getCompetition(), AdminOrchestrationActions.CompActions.UPDATENEXTANDPREV);
			
			ICompetition c = compUpdater.getTarget();
			Assert.assertTrue(c.getNextRound().getId() == 4L);
			Assert.assertTrue(c.getPrevRound().getId() == 3L);
			
			compUpdater.execute();
			
			c = compUpdater.getTarget();
			
			Assert.assertTrue(c.getNextRound().getId() ==  5L);
			Assert.assertTrue(c.getPrevRound().getId() == 4L);
	  }
	  
	  @Test
	  public void testCompCompleteOrchestration() {
			
			cf.setId(3L);
			IOrchestration<ICompetition> compUpdater = of.get(cf.getCompetition(), AdminOrchestrationActions.CompActions.UPDATENEXTANDPREV);
			

			ICompetition c = compUpdater.getTarget();
			Assert.assertTrue(c.getUnderway());
			
			compUpdater.setTarget(c);			
			compUpdater.execute();
			
			Assert.assertFalse(c.getUnderway());
	  }
}
