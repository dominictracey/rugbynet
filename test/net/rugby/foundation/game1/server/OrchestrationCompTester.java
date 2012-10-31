/**
 * 
 */
package net.rugby.foundation.game1.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.orchestration.IOrchestration;
import net.rugby.foundation.admin.server.orchestration.OrchestrationFactory;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.game1.server.BPM.OrchestrationCreateLeaderboard;
import net.rugby.foundation.game1.server.BPM.Game1OrchestrationActions;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

import com.google.inject.Inject;
import junit.framework.Assert;

/**
 * @author home
 *
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class OrchestrationCompTester {
	

        @Before
        public void setUp() {
            //helper.setUp();
        }
  	  
  	  @After
  	  public void tearDown() {
  		  //helper.tearDown();
  	  }
  	  
	  /**
	   * Must refer to a valid module that sources this class.
	   */
	  public String getModuleName() {                                         // (2)
	    return "net.rugby.foundation.game1.Game1";
	  }
	
	  private OrchestrationFactory of;
	  //private ICompetitionFactory cf;
	private ILeaderboardFactory lbf;
	private ILeagueFactory lf;
	private IClubhouseLeagueMapFactory chlmf;
	  
	  @Inject
	  public void setFactory(IClubhouseLeagueMapFactory chlmf, OrchestrationFactory of, ILeaderboardFactory lbf, ILeagueFactory lf) {
			this.of = of;	  
			//this.cf = cf;
			this.lbf = lbf;
			this.lf = lf;
			this.chlmf = chlmf;
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration() {
						
			chlmf.setId(50L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
			Assert.assertTrue(lb.getRows().size() == 8);

			checkLeaderboardRow(lb.getRows().get(0),1,7003L,2,1,3,-4);
			checkLeaderboardRow(lb.getRows().get(1),2,7006L,1,1,2,-3);
			checkLeaderboardRow(lb.getRows().get(2),3,7005L,null, 1,1,4);
			checkLeaderboardRow(lb.getRows().get(3),4,7000L,1,0,1,-50);  
			checkLeaderboardRow(lb.getRows().get(4),5,7006L,1,null,1,-100);
			checkLeaderboardRow(lb.getRows().get(5),6,7001L,0,0,0,-50);
			checkLeaderboardRow(lb.getRows().get(6),7,7004L,null,0,0,-50);
			checkLeaderboardRow(lb.getRows().get(7),8,7002L,0,null,0,-100);

	  }
	  
	  private void checkLeaderboardRow(ILeaderboardRow lbr, int rank, Long appUserId, Integer score1, Integer score2, Integer total, Integer tieBreakerFactor) {
			Assert.assertTrue(lbr.getAppUserId().equals(appUserId));
			Assert.assertTrue(lbr.getRank().equals(rank));
			if (score1 != null)
				Assert.assertTrue(lbr.getScores().get(0).equals(score1));
			else
				Assert.assertTrue(lbr.getScores().get(0) == null);
			
			if (score2 != null)				
				Assert.assertTrue(lbr.getScores().get(1).equals(score2));
			else if (lbr.getScores().size() > 1)
				Assert.assertTrue(lbr.getScores().get(1) == null);

			Assert.assertTrue(lbr.getTotal().equals(total));
			Assert.assertTrue(lbr.getTieBreakerFactor().equals(tieBreakerFactor));
		  
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration51() {
						
			chlmf.setId(51L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration52() {
						
			chlmf.setId(52L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
			Assert.assertTrue(lb.getRows().size() == 5);
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration53() {
						
			chlmf.setId(53L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
			Assert.assertTrue(lb.getRows().size() == 7);
			checkLeaderboardRow(lb.getRows().get(0),1,7000L,3,null,3,44);
			checkLeaderboardRow(lb.getRows().get(1),2,7004L,3,null,3,43);
			
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration54() {
						
			chlmf.setId(54L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration55() {
						
			chlmf.setId(55L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
	  }
	  
	  @Test
	  public void testCreateLeaderboardOrchestration57() {
						
			chlmf.setId(57L);			
			IOrchestration<IClubhouseLeagueMap> compUpdater = of.get(chlmf.get(),Game1OrchestrationActions.ClubhouseLeagueMapActions.CREATE_LEADERBOARD);
			
			compUpdater.execute();
			
			ILeaderboard lb = ((OrchestrationCreateLeaderboard)compUpdater).getLeaderboard();
			Assert.assertTrue(lb != null);
	  }

}
