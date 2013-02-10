package net.rugby.foundation.model.shared;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.CoreTestModule;
import net.rugby.foundation.core.server.factory.IPlayerMatchRatingFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.game1.server.Game1TestModule;
import net.rugby.foundation.test.GuiceJUnitRunner;
import net.rugby.foundation.test.GuiceJUnitRunner.GuiceModules;
import net.rugby.foundation.admin.server.AdminTestModule;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ Game1TestModule.class, CoreTestModule.class, AdminTestModule.class })

public class PlayerMatchInfoTester {

	private IPlayerMatchRatingFactory pmrf;
	private IPlayerMatchStatsFactory pmsf;

	@Inject
	public void setFactory(IPlayerMatchStatsFactory pmsf, IPlayerMatchRatingFactory pmrf) {
		this.pmrf = pmrf;
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
			IPlayerMatchInfo pmi = new PlayerMatchInfo();
			pmi.setMatchRating(pmrf.get(2000L));
			pmi.setPlayerMatchStats(pmsf.getById(1000L));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(out);

			oos.writeObject(pmi);
			oos.close();
			assertTrue(out.toByteArray().length > 0);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}




}
