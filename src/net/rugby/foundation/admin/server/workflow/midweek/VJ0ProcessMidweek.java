package net.rugby.foundation.admin.server.workflow.midweek;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.midweek.results.VS0MidweekResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class VJ0ProcessMidweek extends Job1<VS0MidweekResult, UniversalRound> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private ICompetitionFactory cf;

	public VJ0ProcessMidweek() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	
	@Override
	public Value<VS0MidweekResult> run(UniversalRound universalRound) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.cf = injector.getInstance(ICompetitionFactory.class);
			
			// give the virtual comps a chance to process themselves as well
			List<Value<MS8Rated>> virtualCompResults = new ArrayList<Value<MS8Rated>>();
			List<ICompetition> virtualComps = cf.getVirtualComps();
			for (ICompetition c: virtualComps) {
				virtualCompResults.add(futureCall(new VJ2ProcessVirtualCompSeries(), immediate(c.getId()), immediate(RatingMode.BY_COMP), immediate(c.getLongName())));
				virtualCompResults.add(futureCall(new VJ2ProcessVirtualCompSeries(), immediate(c.getId()), immediate(RatingMode.BY_POSITION), immediate(c.getLongName())));
				virtualCompResults.add(futureCall(new VJ2ProcessVirtualCompSeries(), immediate(c.getId()), immediate(RatingMode.BY_TEAM), immediate(c.getLongName())));
			}
			
			return futureCall(new VJ9CompileMidweekLog(), immediate(universalRound), futureList(virtualCompResults));
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
