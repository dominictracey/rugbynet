package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.model.IRatingSeriesManager;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IRatingGroup;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class CheckRatingGroup extends Job3<Long, Long, Integer, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRatingSeriesManager rm;
	transient private ISeriesConfigurationFactory scf;
	transient private IUniversalRoundFactory urf;

	public CheckRatingGroup() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	
	@Override
	public Value<Long> run(Long seriesConfigId, Integer uROrdinal, String label) throws RetryRequestException {

		try {
			
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.rm = injector.getInstance(IRatingSeriesManager.class);
			this.urf = injector.getInstance(IUniversalRoundFactory.class);
			
			ISeriesConfiguration sc = scf.get(seriesConfigId);
			
			if (sc.getTargetRoundOrdinal() != uROrdinal) {
				sc.setTargetRoundOrdinal(uROrdinal);
				sc.setTargetRound(urf.get(uROrdinal));
				scf.put(sc);
			}
			
			IRatingGroup group = rm.getRatingGroup(scf.get(seriesConfigId), urf.get(uROrdinal));
			
			return immediate(group.getId());
	
		} catch (Exception ex) {			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem securing RatingGroup for series.", ex);
			return null;
		}

	}

}
