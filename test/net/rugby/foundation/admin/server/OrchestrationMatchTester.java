/**
 * 
 */
package net.rugby.foundation.admin.server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.rugby.foundation.admin.server.orchestration.IOrchestration;
import net.rugby.foundation.admin.server.orchestration.IOrchestrationFactory;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
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
@GuiceModules({ Game1TestModule.class, AdminTestModule.class, CoreTestModule.class })
public class OrchestrationMatchTester {
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
	public String getModuleName() {                                        
		return "net.rugby.foundation.admin.Admin";
	}

	private IOrchestrationFactory of;
	private IMatchGroupFactory mgf;
	private ITeamGroupFactory tf;

	@Inject
	public void setFactory(IOrchestrationFactory of, IMatchGroupFactory mgf, ITeamGroupFactory tf) {
		this.of = of;
		this.mgf = mgf;
		this.tf = tf;
	}

	@Test
	public void testLockMatchOrchestration() {

		IOrchestration<IMatchGroup> matchLocker = of.get(mgf.get(205L),AdminOrchestrationActions.MatchActions.LOCK);
		matchLocker.setExtraKey(2L);
		IMatchGroup m = matchLocker.getTarget();
		Assert.assertFalse(m.getLocked());

		matchLocker.execute();

		Assert.assertTrue(m.getLocked());

		matchLocker = of.get(mgf.get(207L),AdminOrchestrationActions.MatchActions.LOCK);
		matchLocker.setExtraKey(2L);
		m = matchLocker.getTarget();
		Assert.assertFalse(m.getLocked());

		matchLocker.execute();

		Assert.assertTrue(m.getLocked());
	}

	@Test
	public void testFetchMatchOrchestration() {
		IMatchGroup m = mgf.create();
		Calendar cal = new GregorianCalendar();
		ITeamGroup v = tf.create();
		v.setId(97L);
		v.setAbbr("TLN");
		v.setDisplayName("Toulon");
		m.setVisitingTeam(v);
		ITeamGroup h = tf.create();
		h.setId(96L);
		h.setDisplayName("Cardiff Blues");
		h.setAbbr("CAR");
		m.setHomeTeam(h);
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0);  
		cal.set(Calendar.MILLISECOND, 0); 
		cal.set(Calendar.DAY_OF_MONTH, 19); 
		cal.set(Calendar.MONTH, Calendar.OCTOBER);
		cal.set(Calendar.YEAR, 2013);
		Date matchDate = cal.getTime();
		m.setDate(matchDate);
		m.setLocked(true);
		
		IOrchestration<IMatchGroup> matchFetcher = of.get(m,AdminOrchestrationActions.MatchActions.FETCH);
		matchFetcher.setExtraKey(2L);
		m = matchFetcher.getTarget();
		Assert.assertTrue(m.getLocked());

		matchFetcher.execute();

		Assert.assertTrue(m.getStatus() == Status.FINAL_HOME_WIN);
	}
}
