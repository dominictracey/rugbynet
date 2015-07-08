package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;


import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.model.IRatingSeriesManager;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.TopTenTestModule;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ CoreTestModule.class, AdminTestModule.class, TopTenTestModule.class })
public class RatingSeriesTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());

	private IRatingSeriesFactory rsf;

	private ICompetitionFactory cf;

	private IRatingSeriesManager rsm;

	private ITopTenListFactory ttlf;

	private ISeriesConfigurationFactory scf;

	private IUniversalRoundFactory urf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(IRatingSeriesFactory rsf, ICompetitionFactory cf, IRatingSeriesManager rsm, ITopTenListFactory ttlf, 
			ISeriesConfigurationFactory scf, IUniversalRoundFactory urf) {
		this.rsf = rsf;
		this.cf = cf;
		this.rsm = rsm;
		this.ttlf = ttlf;
		this.scf = scf;
		this.urf = urf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testBasicFactoryOperation()  {
		IRatingSeries rs = rsf.create();

		assertTrue(rs != null);
		assertTrue(rs.getCreated() != null);
		assertTrue(rs.getUpdated() == null);
		//assertTrue(rs.getComps() != null);
		assertTrue(rs.getCompIds() != null);
		assertTrue(rs.getCountries() != null);
		assertTrue(rs.getCountryIds() != null);
	}
	
	@Test
	public void testBasicGet()  {
		IRatingSeries rs = rsf.get(75000L);
		
		assertTrue(rs != null);
		assertTrue(rs.getCreated() != null);
		assertTrue(rs.getUpdated() == null);
	}
	
	@Test
	public void testBasicPut()  {
		IRatingSeries rs = rsf.create();
		rs.getActiveCriteria().add(Criteria.BEST_YEAR);
		rs.getCompIds().add(1L);
		assertTrue(rs.getId() == null);
		rsf.put(rs);
		assertTrue(rs.getId() != null);
		assertTrue(rs.getCompIds().size() == 1);
		assertTrue(rs.getActiveCriteria().get(0) == Criteria.BEST_YEAR);
	}

	@Test
	public void testBasicGenerate()  {
		
		ISeriesConfiguration rsc = scf.get(4750000L);

		assertTrue(rsc != null);
		assertTrue(rsc.getId() == 4750000L);
		assertTrue(rsc.getCompIds().contains(1L));
		assertTrue(rsc.getMode() == RatingMode.BY_POSITION);
		
		IRatingSeries rs = rsm.initialize(rsc);
		assertTrue(rs.getActiveCriteria().contains(Criteria.BEST_YEAR));
		assertTrue(rs.getMode() == RatingMode.BY_POSITION);
		assertTrue(rs.getCompIds().contains(1L));
//		assertTrue(rs.getComps().contains(cf.get(1L)));

		rsm.doRatingGroup(rs, urf.get(DateTime.now()));
		
		assertTrue(rs.getRatingGroupIds().size() == 1);
		assertTrue(rs.getRatingGroups().size() == 1);
		assertTrue(rs.getRatingGroups().get(0).getRatingSeries() == rs);
		assertTrue(rs.getRatingGroups().get(0).getRatingSeriesId() == rs.getId());
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().size() == 1);
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingGroup() == rs.getRatingGroups().get(0));
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingGroupId() == rs.getRatingGroups().get(0).getId());
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getCriteria() == Criteria.BEST_YEAR);
		
		// ratingQueries
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().size() == 10);
		assertTrue(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(0).getRoundIds().size() == 2);
		
		// TTLs
//		for (IRatingQuery rq : rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries()) {
//			TopTenSeedData data = new TopTenSeedData(rq.getId(), "", "", rq.getCompIds().get(0), rq.getRoundIds(), 10);
//			ITopTenList ttl = ttlf.create(data);
//			assertTrue(ttl.getItemIds().size() == 10);
//			// assert they are all the same position
//			position lastPos = null;
//			for (ITopTenItem tti : ttl.getList()) {
//				position pos = tti.getPlayer().getPosition();
//				if (lastPos != null) {
//					assertTrue(pos == lastPos);
//				}
//				lastPos = pos;
//			}
//		}
	}
}
