package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class StandingTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	private IStandingFactory sf;
	private IRoundFactory rf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IStandingFactory sf, IRoundFactory rf) {
		this.sf = sf;
		this.rf = rf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testGetStandingById() {

		IStanding s = sf.get(29001L);

		assertTrue(s.getRoundId().equals(2L));
		assertTrue(s.getTeamId().equals(9001L));
	}
	
	@Test
	public void testGetStandingsByRound() {

		IRound r = rf.get(2L);
		List<IStanding> list = sf.getForRound(r);
		
		assertTrue(list.get(0).getRoundId().equals(2L));
		assertTrue(list.get(0).getTeamId().equals(9001L));
		assertTrue(list.size() == 4);
	}

}
