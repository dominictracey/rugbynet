package net.rugby.foundation.test.jenkins;


import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.TopTenTestModule;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesRenderer;
import net.rugby.foundation.topten.server.utilities.notes.TwitterNotesRenderer;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ CoreTestModule.class, AdminTestModule.class, TopTenTestModule.class })
public class NotesTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	private TopTenNotesRenderer renderer;
	private INotesCreator creator;
	private ITopTenListFactory ttlf;
	private IRatingSeriesFactory rsf;
	private TwitterNotesRenderer twitter;


	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(TopTenNotesRenderer renderer, INotesCreator creator, ITopTenListFactory ttlf, IRatingSeriesFactory rsf, TwitterNotesRenderer twitter) {
		this.renderer = renderer;
		this.creator = creator;
		this.ttlf = ttlf;
		this.rsf = rsf;
		this.twitter = twitter;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testBasicFactoryOperation()  {
		IRatingSeries rs = rsf.get(75000L);
		
//		Long ttlid = rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(0).getTopTenListId();
//		
//		ITopTenList ttl = null;
//		if (ttlid != null) {
//			ttl = ttlf.get(ttlid);
//		}
		
//		List<INote> notes = creator.createNotes(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(0));
//
//		assert(notes != null);
//		assert(notes.size() == 10);
//		
//		ITopTenList ttl = ttlf.get(rs.getRatingGroups().get(0).getRatingMatrices().get(0).getRatingQueries().get(0).getTopTenListId());
//		
//		String val = renderer.render(notes, ttl, false);
//		
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, val);
//		
//		val = renderer.render(notes, null, true);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Top Ten:\n" + val);
//		
//		val = twitter.render(notes, null, true);
//		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Twitter:\n" + val);


	}
	

}
