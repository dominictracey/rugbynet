package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class CreateGraphRoundNode extends Job4<String, Long, String, Integer, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundNodeFactory rnf;
	transient private ICompetitionFactory cf;

	public CreateGraphRoundNode() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}

	
	@Override
	public Value<String> run(Long compId, String action, Integer posOrdinal, String label) throws RetryRequestException {

		try {
			
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.cf = injector.getInstance(ICompetitionFactory.class);
			this.rnf = injector.getInstance(IRoundNodeFactory.class);
			
			ICompetition c = cf.get(compId);


			if ("create".equals(action)) {

				rnf.get(compId,posOrdinal);
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Completed creation of graphing data for the " + Position.position.getAt(posOrdinal).getPlural() + " of " + c.getLongName());
				

				return immediate("Completed creation of graphing data<br>");
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Unrecognized RoundNode management action " + action);
				return immediate("Unrecognized RoundNode management action " + action);
			}
	
		} catch (Exception ex) {			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem managing RoundNodes.", ex);
			return immediate("FAILURE: " + ex.getLocalizedMessage());
		}

	}

}
