package net.rugby.foundation.test.jenkins;


import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.inject.Inject;

import net.rugby.foundation.admin.server.AdminTestModule;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.ConfigurationType;
import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.topten.server.TopTenTestModule;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ CoreTestModule.class, AdminTestModule.class, TopTenTestModule.class })
public class SeriesConfigurationTester {

	private final LocalServiceTestHelper helper =
			new LocalServiceTestHelper(new LocalMemcacheServiceTestConfig());
	private ISeriesConfigurationFactory scf;

	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Inject
	public void setFactory(ISeriesConfigurationFactory scf) {
		this.scf = scf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	@Test
	public void testBasicFactoryOperation()  {
		ISeriesConfiguration rs = scf.create();

		assertTrue(rs != null);
	}
	
	@Test
	public void testGet()  {
		ISeriesConfiguration rs = scf.get(4750000L);

		assertTrue(rs != null);
		assertTrue(rs.getId() == 4750000L);
		assertTrue(rs.getCompId() == 1L);
		assertTrue(rs.getType() == ConfigurationType.BY_POSITION);
	}
	
	@Test
	public void testBadGet()  {
		ISeriesConfiguration rs = scf.get(10000L);

		assertTrue(rs == null);
	}
	
	
	
}
