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
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class OrchestrationMatchTester {
//    private final LocalServiceTestHelper helper =
//            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

        @Before
        public void setUp() {
           // helper.setUp();
        }
  	  
  	  @After
  	  public void tearDown() {
  		  //helper.tearDown();
  	  }
  	  

	  /**
	   * Must refer to a valid module that sources this class.
	   */
	  public String getModuleName() {                                         // (2)
	    return "net.rugby.foundation.admin.Admin";
	  }
	
	  private IOrchestrationFactory of;
	private IMatchGroupFactory mgf;
	  
	  @Inject
	  public void setFactory(IOrchestrationFactory of, IMatchGroupFactory mgf) {
			this.of = of;
			this.mgf = mgf;
	  }
	  
	  @Test
	  public void testLockMatchOrchestration() {
			
			mgf.setId(205L);
			IOrchestration<IMatchGroup> matchLocker = of.get(mgf.getGame(),AdminOrchestrationActions.MatchActions.LOCK);
			matchLocker.setExtraKey(2L);
			IMatchGroup m = matchLocker.getTarget();
			Assert.assertFalse(m.getLocked());
			
			matchLocker.execute();

			Assert.assertTrue(m.getLocked());
			
			mgf.setId(207L);
			matchLocker = of.get(mgf.getGame(),AdminOrchestrationActions.MatchActions.LOCK);
			matchLocker.setExtraKey(2L);
			m = matchLocker.getTarget();
			Assert.assertFalse(m.getLocked());
			
			matchLocker.execute();

			Assert.assertTrue(m.getLocked());
	  }
	  
	  @Test
	  public void testFetchMatchOrchestration() {
						
			mgf.setId(203L);
			IOrchestration<IMatchGroup> matchFetcher = of.get(mgf.getGame(),AdminOrchestrationActions.MatchActions.FETCH);
			matchFetcher.setExtraKey(2L);
			IMatchGroup m = matchFetcher.getTarget();
			Assert.assertTrue(m.getLocked());
			
			matchFetcher.execute();

			Assert.assertTrue(m.getStatus() == Status.FINAL_HOME_WIN);
	  }
}
