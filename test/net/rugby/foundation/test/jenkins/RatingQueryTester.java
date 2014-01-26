package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;

import java.util.ArrayList;
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
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })
public class RatingQueryTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IRatingQueryFactory rqf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IRatingQueryFactory rqf) {
		this.rqf = rqf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testCreateRatingQuery() {

		IRatingQuery rq = rqf.create();
		
		assertTrue(rq.getId() == null);
		assertTrue(rq.getCompIds() != null);
		assertTrue(rq.getCountryIds() != null);
		assertTrue(rq.getPositions() != null);
		assertTrue(rq.getTeamIds() != null);
		assertTrue(rq.getRoundIds()!= null);
		
		rqf.put(rq);
		
		assertTrue(rq.getId() != null);
		
	}
	
	@Test
	public void testQueryRatingQuery700() {
		
		List<Long> compIds = new ArrayList<Long>();
		List<Long> roundIds = new ArrayList<Long>();
		
		compIds.add(1L);
		roundIds.add(2L);
		
		IRatingQuery rq = rqf.query(compIds,roundIds,null,null,null);
		
		
		assertTrue(rq.getId() == 700L);
		assertTrue(rq.getCompIds().contains(1L));
		assertTrue(rq.getCountryIds().isEmpty());
		assertTrue(rq.getPositions().isEmpty());
		assertTrue(rq.getTeamIds().isEmpty());
		assertTrue(rq.getRoundIds().contains(2L));
		
	}
	
	@Test
	public void testQueryRatingQuery701() {
		
		List<Long> compIds = new ArrayList<Long>();
		List<Long> roundIds = new ArrayList<Long>();
		List<Long> countryIds = new ArrayList<Long>();
		
		compIds.add(1L);
		roundIds.add(2L);
		countryIds.add(5001L);
		
		IRatingQuery rq = rqf.query(compIds,roundIds,null,countryIds,null);
		
		
		assertTrue(rq.getId() == 701L);
		assertTrue(rq.getCompIds().contains(1L));
		assertTrue(rq.getCountryIds().contains(5001L));
		assertTrue(rq.getPositions().isEmpty());
		assertTrue(rq.getTeamIds().isEmpty());
		assertTrue(rq.getRoundIds().contains(2L));
		
	}
	
}
