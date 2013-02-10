package net.rugby.foundation.model.shared;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
//import com.google.gwt.i18n.shared.DateTimeFormat;
//import com.google.gwt.junit.client.GWTTestCase;
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class })

public class ScrumPlayerMatchStatsTester {
	private IPlayerMatchStatsFactory pmsf;


	//		    private final LocalServiceTestHelper helper =
	//		            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	//
	//		        @Before
	//		        public void setUp() {
	//		            helper.setUp();
	//		        }
	//		  	  
	//		  	  @After
	//		  	  public void tearDown() {
	//		  		  helper.tearDown();
	//		  	  }

	@Inject
	public void setFactory(IPlayerMatchStatsFactory pmsf) {
		this.pmsf = pmsf;
	}
	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() {                                         // (2)
		return "net.rugby.foundation.model.Model";
	}

	/**
	 * Add as many tests as you like.
	 */
	@Test
	public void testSimple() {                                              // (3)
		assert(true);
	}

	@Test
	public void testIsSerializable()  {

		try {		
			IPlayerMatchStats pms = pmsf.getById(1000L);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(out);

			oos.writeObject(pms);
			oos.close();
			assertTrue(out.toByteArray().length > 0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




}
