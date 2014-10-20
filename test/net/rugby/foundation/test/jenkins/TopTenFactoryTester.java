package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.test.TestPlayerFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.TopTenTestModule;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.ISocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.SocialMediaDirector;

//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, TopTenTestModule.class, AdminTestModule.class })
public class TopTenFactoryTester {

	private ITopTenListFactory ttf;
	String title = "Test TTL";
	String desc = "Desc";

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	private IPlayerFactory pf;
	private IPlayerRatingFactory prf;
	private IRatingQueryFactory rqf;
	private ITeamGroupFactory tgf;
	private IMatchRatingEngineSchemaFactory resf;
	private IConfigurationFactory ccf;

	@Inject
	public void setFactory(ITopTenListFactory ttf, IPlayerFactory pf, IPlayerRatingFactory prf, IRatingQueryFactory rqf, 
			ITeamGroupFactory tgf, IMatchRatingEngineSchemaFactory resf, IConfigurationFactory ccf) {
		this.ttf = ttf;
		this.pf = pf;
		this.prf = prf;
		this.rqf = rqf;
		this.tgf = tgf;
		this.resf = resf;
		this.ccf = ccf;
	}

	@Before
	public void setUp() {
		helper.setUp();
		// have to call this here so memcache is ready
		((TestPlayerFactory)pf).Populate();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.topten.server.factory";
	}

	/**
	 * Add as many tests as you like.
	 */
	public void testSimple() {                                              // (3)
		assert(true);
	}


	private void deleteAll() {
		ITopTenList ttl = ttf.getLastCreatedForComp(1L);

		while (ttl.getPrevId() != null) {
			Long prevId = ttl.getPrevId();

			ttf.delete(ttl);
			ttl = ttf.get(prevId);
		}

		// delete last one
		ttf.delete(ttl);

		ttl = ttf.getLastCreatedForComp(2L);

		if (ttl != null) {
			while (ttl.getPrevId() != null) {
				Long prevId = ttl.getPrevId();

				ttf.delete(ttl);
				ttl = ttf.get(prevId);
			}

			ttf.delete(ttl);
		}
	}


	@Test
	public void testGetFactory() {
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();

		ITopTenList ttl = ttf.get(1000L);
		assertTrue(ms.contains(1000L));

		assert (ttl.getList().size() == 10);


		deleteAll();

		assertFalse(ms.contains(1000L));
		assertFalse(ms.contains(1001L));
		assertFalse(ms.contains(1002L));
		assertFalse(ms.contains(1003L));
		assertFalse(ms.contains(1004L));

		//Logger.getLogger(getModuleName()).log(Level.INFO,ms.get.toString());
	}

	@Test
	public void createOne() {
		//		  (List<IPlayerMatchInfo> pmiList, String title,
		//					String description, Long compId, Long roundId, position pos,
		//					Long countryId, Long teamId)
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
		deleteAll();

		ITopTenList ttl = createTTL();

		assert (ttl != null);


		assertTrue(ms.contains(ttl.getId()));
		assertTrue(ttl.getNextId() == null);
		assertTrue(ttl.getNextPublishedId() == null);
		assertTrue(ttl.getPrevPublishedId() == null);

		// this isn't the case because we are using the test factory and getLastCreatedForComp returns a hard-coded value.
		//assertTrue(ttl.getPrevId() == null);

		assertTrue(ttl.getPublished() == null);
		assertTrue(ttl.getCompId().equals( 1L));
//		assertTrue(ttl.getContent() == desc);
		assertTrue(ttl.getTitle() == title);
		assertTrue(ttl.getItemIds().size() == 10);
		assertTrue(ttl.getLive() == false);
		assertTrue(ttl.getCreated() != null);

		ttf.delete(ttl);
		assertFalse(ms.contains(ttl.getId()));


	}

	@Test
	public void addOne() {
		//		  (List<IPlayerMatchInfo> pmiList, String title,
		//					String description, Long compId, Long roundId, position pos,
		//					Long countryId, Long teamId)
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();

		ITopTenList ttl = createTTL(); 

		assert (ttl != null);


		assertTrue(ms.contains(ttl.getId()));
		assertTrue(ttl.getNextId() == null);
		assertTrue(ttl.getNextPublishedId() == null);
		assertTrue(ttl.getPrevPublishedId() == null);

		ITopTenList prev = ttf.get(ttl.getPrevId());

		assertTrue(prev.getNextId().equals(ttl.getId()));

		deleteAll();


	}

	private ITopTenList createTTL() {
//		List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,1L);
		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);

		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc,1L, null, 10);
		return ttf.create(ttsd);
	}
	
	private ITopTenList createLinkedTTL() {
//		List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,1L);
		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);

		TopTenSeedData ttsd = new TopTenSeedData(7710001L, "Top Ten Props - Round 1", desc,1L, null, 10);
		ITopTenList last =  ttf.create(ttsd);
		
		ttsd = new TopTenSeedData(7700001L, "Top Ten Props - Round 2", desc, 1L, null, 10);
		return ttf.create(ttsd, last);
	}

	@Test 
	public void publish1002with1001NotInMemcache() {
		ITopTenList ttl = ttf.get(1002L);

		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		assertTrue(ttl.getPrevPublishedId() == null);

		ttf.publish(ttl);

		assertTrue(ttl.getLive());
		assertTrue(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId().equals(1001L));

		ITopTenList prevPub = ttf.get(1001L);
		assertTrue(prevPub.getLive());
		assertTrue(prevPub.getPublished() != null);
		assertTrue(prevPub.getNextPublishedId().equals(ttl.getId()));

		deleteAll();
	}

	@Test 
	public void publish1002with1001InMemcache() {
		ITopTenList prevPub = ttf.get(1001L);
		assertTrue(prevPub.getLive());
		assertTrue(prevPub.getPublished() != null);
		assertTrue(prevPub.getNextPublishedId() == null);

		ITopTenList ttl = ttf.get(1002L);

		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		assertTrue(ttl.getPrevPublishedId() == null);

		ttf.publish(ttl);

		assertTrue(ttl.getLive());
		assertTrue(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId().equals(1001L));

		prevPub = ttf.get(1001L);
		assertTrue(prevPub.getLive());
		assertTrue(prevPub.getPublished() != null);
		assertTrue(prevPub.getNextPublishedId().equals(ttl.getId()));

		deleteAll();
	}

	@Test 
	public void publish1003with1001NotInMemcache() {
		ITopTenList ttl = ttf.get(1003L);

		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		assertTrue(ttl.getPrevPublishedId() == null);

		ttf.publish(ttl);

		assertTrue(ttl.getLive());
		assertTrue(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId().equals(1001L));

		ITopTenList prevPub = ttf.get(1001L);
		assertTrue(prevPub.getLive());
		assertTrue(prevPub.getPublished() != null);
		assertTrue(prevPub.getNextPublishedId().equals(ttl.getId()));

		// 1002 was skipped over in pub chain
		ITopTenList notPub = ttf.get(1002L);
		assertFalse(notPub.getLive());
		assertFalse(notPub.getPublished() != null);
		assertTrue(notPub.getNextPublishedId() == null);
		assertTrue(notPub.getPrevPublishedId() == null);

		deleteAll();
	}

	@Test 
	public void publishAndUnpublish1003() {
		ITopTenList ttl = ttf.get(1003L);

		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		assertTrue(ttl.getPrevPublishedId() == null);

		ttf.publish(ttl); // publish

		assertTrue(ttl.getLive());
		assertTrue(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId().equals(1001L));

		ttf.publish(ttl);  // unpublish
		assertFalse(ttl.getLive());
		assertFalse(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId() == null);	
		assertTrue(ttl.getNextPublishedId() == null);
		assertTrue(ttl.getPrevId().equals(1002L));	
		assertTrue(ttl.getNextId().equals(1004L));

		// prev now last on chain
		ITopTenList prev = ttf.get(1001L);
		assertTrue(prev.getLive());
		assertTrue(prev.getPublished() != null);
		assertTrue(prev.getNextPublishedId() == null);


		deleteAll();
	}

	@Test 
	public void publish1003AndUnpublish1001() {
		ITopTenList ttl = ttf.get(1003L);

		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		assertTrue(ttl.getPrevPublishedId() == null);

		ttf.publish(ttl); // publish

		assertTrue(ttl.getLive());
		assertTrue(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId().equals(1001L));

		ITopTenList prev = ttf.get(1001L);
		ttf.publish(prev);  // unpublish
		assertFalse(prev.getLive());
		assertFalse(prev.getPublished() != null);
		assertTrue(prev.getPrevPublishedId() == null);	
		assertTrue(prev.getNextPublishedId() == null);
		assertTrue(prev.getPrevId().equals(1000L));	
		assertTrue(prev.getNextId().equals(1002L));

		// 1000 and 1003 now pub linked
		ITopTenList first = ttf.get(1000L);
		assertTrue(first.getLive());
		assertTrue(first.getPublished() != null);
		assertTrue(first.getNextPublishedId().equals(ttl.getId()));
		ttl = ttf.get(1003L);
		assertTrue(ttl.getPrevPublishedId().equals(first.getId()));

		deleteAll();
	}

	@Test
	public void unpublishEverything() {
		ITopTenList ttl = ttf.get(1001L);

		assertTrue(ttl != null);
		assertTrue(ttl.getLive());

		ttf.publish(ttl); // unpublish 1001

		assertFalse(ttl.getLive());
		assertFalse(ttl.getPublished() != null);
		assertTrue(ttl.getPrevPublishedId() == null);

		ITopTenList prev = ttf.get(1000L);
		ttf.publish(prev);  // unpublish 1000L
		assertFalse(prev.getLive());
		assertFalse(prev.getPublished() != null);
		assertTrue(prev.getPrevPublishedId() == null);	
		assertTrue(prev.getNextPublishedId() == null);
		assertTrue(prev.getNextId().equals(1001L));

		// there should be no publicly accessible TTLs now
		ttl = ttf.getLatestForComp(1L);
		assertTrue(ttl == null);


		deleteAll();
	}

	@Test
	public void deletePublished() {
		ITopTenList ttl = ttf.get(1001L);

		assertTrue(ttl != null);
		assertTrue(ttl.getLive());

		ttf.delete(ttl); // delete 1001


		ITopTenList prev = ttf.get(1000L);
		assertTrue(prev.getLive());
		assertTrue(prev.getNextPublishedId() == null);
		assertTrue(prev.getNextId().equals(1002L));

		// 1000 should be latest now
		ttl = ttf.getLatestForComp(1L);
		assertTrue(ttl.getId().equals(1000L));

		deleteAll();
	}

	@Test
	public void createInNewComp() {

		//List<IPlayerRating> pmiList = prf.query(rqf.get(700L));

		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc, 2L, null, 10);
		ITopTenList ttl = ttf.create(ttsd);


		assertTrue(ttl != null);
		assertFalse(ttl.getLive());


		// should be last now
		ITopTenList last = ttf.getLastCreatedForComp(2L);
		assertTrue(ttl.getId().equals(last.getId()));

		deleteAll();
	}

	@Test
	public void publishInNewComp() {

		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);
		//List<IPlayerRating> 700L = prf.query(rqf.get(700L));

		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc, 2L, null, 10);
		ITopTenList ttl = ttf.create(ttsd);


		assertTrue(ttl != null);
		assertFalse(ttl.getLive());

		// should be last now
		ITopTenList last = ttf.getLastCreatedForComp(2L);
		assertTrue(ttl.getId().equals(last.getId()));

		ITopTenList latest = ttf.getLatestForComp(2L);
		assertTrue(latest == null);

		ttf.publish(ttl);
		assertTrue(ttl.getLive());

		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl.getId()));

		deleteAll();
	}

	@Test
	public void publishTwoDeleteOneInNewComp() {

		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);

		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc, 2L, null, 10);
		ITopTenList ttl1 = ttf.create(ttsd);

		assertTrue(ttl1 != null);
		assertFalse(ttl1.getLive());

		ttl1 = ttf.publish(ttl1);
		assertTrue(ttl1.getLive());
		ITopTenList latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl1.getId()));


		ITopTenList ttl2 = ttf.create(ttsd);
		assertTrue(ttl2 != null);
		assertFalse(ttl2.getLive());

		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl1.getId()));

		ITopTenList last = ttf.getLastCreatedForComp(2L);
		assertTrue(last != null);
		assertTrue(last.getId().equals(ttl2.getId()));
		assertTrue(last.getPrevId().equals(latest.getId()));
		assertTrue(last.getPrevPublishedId() == null);
		assertTrue(latest.getNextPublishedId() == null);
		assertTrue(latest.getNextId().equals(last.getId()));

		ttl2 = ttf.publish(ttl2);
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl2.getId()));

		last = ttf.getLastCreatedForComp(2L);
		ttl1 = ttf.get(ttl1.getId());
		assertTrue(last != null);
		assertTrue(last.getId().equals(ttl2.getId()));
		assertTrue(last.getPrevId().equals(latest.getPrevId()));
		assertTrue(ttl2.getPrevPublishedId().equals(ttl1.getId()));
		assertTrue(last.getPrevPublishedId().equals(ttl1.getId()));
		assertTrue(ttl1.getNextPublishedId().equals(ttl2.getId()));
		assertTrue(ttl1.getNextPublishedId().equals(latest.getId()));
		assertTrue(ttl1.getNextId().equals(latest.getId()));
		assertTrue(latest.getNextPublishedId() == null);
		assertTrue(latest.getPrevId().equals(ttl1.getId()));

		ttl1 = ttf.delete(ttl1);
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl2.getId()));

		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last != null);
		assertTrue(last.getId().equals(ttl2.getId()));
		assertTrue(last.getPrevId() == null);
		assertTrue(last.getPrevPublishedId() == null);
		assertTrue(latest.getNextPublishedId() == null);
		assertTrue(latest.getPrevId() == null);

		deleteAll();
	}
	
	@Test
	public void createThreePublishAllUnpublishMiddle() {

		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
		ms.clearAll();

		// create 3
		// publish/unpublish 3
		// publish 1 > 3 > 2
		// unpublish 2
		// publish 2
		// delete 1
		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);
		//List<IPlayerRating> pmiList = prf.query(rqf.get(700L));

		TopTenSeedData ttsd = new TopTenSeedData(700L, "uno", "one", 2L, null, 10);
		ITopTenList ttl1 = ttf.create(ttsd); //create 1
		assertTrue(ttl1 != null);
		assertFalse(ttl1.getLive());
		ITopTenList last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getId().equals(ttl1.getId()));
		ITopTenList latest = ttf.getLatestForComp(2L);
		assertTrue(latest == null);
		
		ttsd = new TopTenSeedData(700L, "dos", "two", 2L, null, 10);
		ITopTenList ttl2 = ttf.create(ttsd); //create 2
		assertTrue(ttl2 != null);
		assertFalse(ttl2.getLive());
		assertTrue(ttl2.getPrevId().equals(ttl1.getId()));
		ttl1 = ttf.get(ttl1.getId());
		assertTrue(ttl1.getNextId().equals(ttl2.getId()));
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getId().equals(ttl2.getId()));
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest == null);
		
		ttsd = new TopTenSeedData(700L, "tres", "three", 2L, null, 10);
		ITopTenList ttl3 = ttf.create(ttsd); //create 3
		assertTrue(ttl3 != null);
		assertFalse(ttl3.getLive());
		assertTrue(ttl3.getPrevId().equals(ttl2.getId()));
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getId().equals(ttl3.getId()));		
		ttl2 = ttf.get(ttl2.getId());
		assertTrue(ttl2.getNextId().equals(ttl3.getId()));
		assertTrue(ttl2.getPrevId().equals(ttl1.getId()));		
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest == null);
		
		ttl3 = ttf.publish(ttl3); //publish 3
		assertTrue(ttl3.getLive());
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl3.getId()));
		assertTrue(latest.getPrevId().equals(ttl2.getId()));
		assertTrue(latest.getPrevPublishedId() == null);
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getId().equals(ttl3.getId()));
		
		ttl3 = ttf.publish(ttl3); // unpublish 3
		assertTrue(!ttl3.getLive());
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest == null);
		assertTrue(ttl3.getPrevPublishedId() == null);
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getId().equals(ttl3.getId()));
		
		ttl1 = ttf.publish(ttl1);  // publish 1
		assertTrue(ttl1.getLive());
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl1.getId()));
		assertTrue(latest.getPrevId() == null);
		assertTrue(latest.getPrevPublishedId() == null);
		assertTrue(latest.getNextPublishedId() == null);
		
		ttl2 = ttf.publish(ttl2);  // publish 2
		assertTrue(ttl2.getLive());
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl2.getId()));
		assertTrue(latest.getPrevId().equals(ttl1.getId()));
		assertTrue(latest.getPrevPublishedId().equals(ttl1.getId()));
		ttl1 = ttf.get(ttl1.getId());
		assertTrue(ttl1.getNextId().equals(ttl2.getId()));
		assertTrue(ttl1.getNextPublishedId().equals(ttl2.getId()));		
		
		ttl3 = ttf.publish(ttl3);  // publish 3
		assertTrue(ttl3.getLive());
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl3.getId()));
		assertTrue(latest.getPrevId().equals(ttl2.getId()));
		assertTrue(latest.getPrevPublishedId().equals(ttl2.getId()));		
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last != null);
		assertTrue(last.getId().equals(ttl3.getId()));
		assertTrue(last.getPrevPublishedId().equals(ttl2.getId()));  // << prevPubId is null
		assertTrue(last.getNextPublishedId() == null);
		ttl2 = ttf.get(ttl2.getId());
		assertTrue(ttl2.getPrevPublishedId().equals(ttl1.getId()));
		assertTrue(ttl2.getNextPublishedId().equals(ttl3.getId()));
		
		ttl2 = ttf.publish(ttl2); // unpublish 2
		assertTrue(!ttl2.getLive());
		assertTrue(ttl2.getNextPublishedId() == null);
		assertTrue(ttl2.getPrevPublishedId() == null);
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl3.getId()));
		assertTrue(latest.getPrevId().equals(ttl2.getId()));
		assertTrue(latest.getPrevPublishedId().equals(ttl1.getId()));
		
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getPrevPublishedId().equals(ttl1.getId()));
		assertTrue(last.getNextPublishedId() == null);
		
		ttl2 = ttf.publish(ttl2); // publish 2
		assertTrue(ttl2.getLive());
		assertTrue(ttl2.getNextPublishedId().equals(ttl3.getId()));
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl3.getId()));
		assertTrue(latest.getPrevId().equals(ttl2.getId()));
		assertTrue(latest.getPrevPublishedId().equals(ttl2.getId()));
		
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getPrevPublishedId().equals(ttl2.getId()));
		assertTrue(last.getNextPublishedId() == null);
		
		ttl1 = ttf.get(ttl1.getId());
		ttl1 = ttf.delete(ttl1);
		latest = ttf.getLatestForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl3.getId()));
		assertTrue(latest.getPrevId().equals(ttl2.getId()));
		assertTrue(latest.getPrevPublishedId().equals(ttl2.getId()));
		
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(last.getPrevPublishedId().equals(ttl2.getId()));
		assertTrue(last.getNextPublishedId() == null);
		
		deleteAll();
	}
	
	@Test
	public void publishOneCreateOne() {

		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);

		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc, 2L, null, 10);
		ITopTenList ttl = ttf.create(ttsd);


		assertTrue(ttl != null);
		assertFalse(ttl.getLive());
		
		// publish it
		ttf.publish(ttl);
		ttl = ttf.get(ttl.getId());
		assertTrue(ttl.getLive());

		// should be last and latest now
		ITopTenList last = ttf.getLastCreatedForComp(2L);
		assertTrue(ttl.getId().equals(last.getId()));

		ITopTenList latest = ttf.getLatestForComp(2L);
		assertTrue(ttl.getId().equals(latest.getId()));

		// create a new one
		ITopTenList ttl2 = ttf.create(ttsd);

		latest = ttf.getLatestForComp(2L);
		last = ttf.getLastCreatedForComp(2L);
		assertTrue(latest != null);
		assertTrue(latest.getId().equals(ttl.getId()));
		
		assertTrue(ttl2.getId().equals(last.getId()));
		
		// publish second
		ttf.publish(ttl2);
		
		// refresh ttls
		ttl = ttf.get(ttl.getId());
		ttl2 = ttf.get(ttl2.getId());
		
		// make sure next and prev are right
		assertTrue(ttl.getNextId().equals(ttl2.getId()));
		assertTrue(ttl.getNextPublishedId().equals(ttl2.getId()));
		assertTrue(ttl2.getPrevId().equals(ttl.getId()));
		assertTrue(ttl2.getPrevPublishedId().equals(ttl.getId()));
		
		// create third
		ITopTenList ttl3 = ttf.create(ttsd);
		
		// refresh ttls
		ttl = ttf.get(ttl.getId());
		ttl2 = ttf.get(ttl2.getId());
		
		assertTrue(ttl2.getNextId().equals(ttl3.getId()));
		assertTrue(ttl2.getNextPublishedId() == null);
		assertTrue(ttl2.getPrevId().equals(ttl.getId()));
		assertTrue(ttl2.getPrevPublishedId().equals(ttl.getId()));
		assertTrue(ttl3.getPrevId().equals(ttl2.getId()));
		assertTrue(ttl3.getPrevPublishedId() == null);
		assertTrue(ttl3.getNextId() == null);
		assertTrue(ttl2.getNextPublishedId() == null);
		deleteAll();
	}
	
	@Test
	public void teamLimitOf5() {
		//		  (List<IPlayerMatchInfo> pmiList, String title,
		//					String description, Long compId, Long roundId, position pos,
		//					Long countryId, Long teamId)
		MemcacheService ms = MemcacheServiceFactory.getMemcacheService();
		deleteAll();

//		List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,1L);
		//List<IPlayerMatchInfo> pmiList = pmif.getForComp(null,2L);

		int max = 5;
	
		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc, 1L, null, max);
		ITopTenList ttl = ttf.create(ttsd);
		
		assert (ttl != null);

		Iterator<ITopTenItem> it = ttl.getList().iterator();
//		assertTrue(ttl.getList().size() == 10);
		Map<Long, Integer> counter = new HashMap<Long,Integer>(); 
		while (it.hasNext()) {
			ITopTenItem tti = it.next();
			if (!counter.containsKey(tti.getTeamId())) {
				counter.put(tti.getTeamId(), 1);
			} else {
				counter.put(tti.getTeamId(), counter.get(tti.getTeamId()) + 1);
				assertTrue(counter.get(tti.getTeamId()) <= max);
			}
		}

		ttf.delete(ttl);
		assertFalse(ms.contains(ttl.getId()));

		deleteAll();

	}
	@Test
	public void generateEmail() {
		//		  (List<IPlayerMatchInfo> pmiList, String title,
		//					String description, Long compId, Long roundId, position pos,
		//					Long countryId, Long teamId)
		//MemcacheService ms = MemcacheServiceFactory.getMemcacheService();

		prf.getForMatch(5L, resf.getDefault());
		prf.getForMatch(6L, resf.getDefault());
		TopTenSeedData ttsd = new TopTenSeedData(700L, title, desc,1L, null, 10);
		ITopTenList ttl = ttf.create(ttsd);

		assert (ttl != null);
		int j = 1;
		for (ITopTenItem i: ttl.getList()){
			assertTrue(i.getPlayer().getTwitterHandle() != null);
			if (j++ > 5) {
				i.getPlayer().setTwitterHandle(null);
			}
		}
		ISocialMediaDirector smd = new SocialMediaDirector(tgf, ttf, ccf);

		smd.PromoteTopTenList(ttl);
//		assertTrue(ms.contains(ttl.getId()));
//		assertTrue(ttl.getNextId() == null);
//		assertTrue(ttl.getNextPublishedId() == null);
//		assertTrue(ttl.getPrevPublishedId() == null);
//
//		ITopTenList prev = ttf.get(ttl.getPrevId());
//
//		assertTrue(prev.getNextId().equals(ttl.getId()));

		deleteAll();


	}
	
	@Test
	public void checkNotes() {
		ITopTenList ttl = createLinkedTTL();
		assert ttl.getContent() != null;
		
	}
}
