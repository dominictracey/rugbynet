package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.List;

import org.joda.time.DateTime;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class CompileProcessReport extends Job2<Boolean, List<Boolean>, ISeriesConfiguration> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	private ISeriesConfigurationFactory scf;


	public CompileProcessReport() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<Boolean> run(List<Boolean> successes, ISeriesConfiguration sc) {

		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
		
		boolean allGood = true;
		for (Boolean b : successes) {
			if (!b) {
				allGood = false;
				break;
			}
		}
		
		// if they are all good, set the series status to OK
		if (allGood) {
			sc.setStatus(ISeriesConfiguration.Status.OK);
			sc.setLastRun(DateTime.now().toDate());
			sc.setLastRoundOrdinal(sc.getTargetRoundOrdinal());
			sc.setTargetRoundOrdinal(sc.getTargetRoundOrdinal()+1);
			scf.put(sc);
		} else {
			sc.setStatus(ISeriesConfiguration.Status.PENDING);
			sc.setLastRun(DateTime.now().toDate());
			scf.put(sc);			
		}
		
		return new ImmediateValue<Boolean>(allGood);
	}

}
