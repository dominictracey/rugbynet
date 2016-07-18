package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;
import net.rugby.foundation.topten.server.rest.RoundNode;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class UpdateGraphRoundNodes extends Job3<List<String>, Long, String, String> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundNodeFactory rnf;
	transient private ICompetitionFactory cf;

	public UpdateGraphRoundNodes() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	
	@Override
	public Value<List<String>> run(Long compId, String action, String label) throws RetryRequestException {

		try {
			
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.cf = injector.getInstance(ICompetitionFactory.class);
			this.rnf = injector.getInstance(IRoundNodeFactory.class);
			
			ICompetition c = cf.get(compId);
			List<Value<String>> _retval = new ArrayList<Value<String>>();

			if ("create".equals(action)) {
				
				for (int i=1; i < 11; ++i) {  // 0 is UNUSED, 1 is PROP
					FutureValue<String> posVal = futureCall(new CreateGraphRoundNode(), immediate(compId), immediate(action), immediate(i), immediate("creation of graphing data for the " + Position.position.getAt(i).getPlural() + " of " + c.getLongName()));
					_retval.add(posVal);
				}				

				return futureList(_retval);
			} else if ("delete".equals(action)) {
				for (int i=1; i<11; ++i) {  // 0 is UNUSED, 1 is PROP
					List<RoundNode> rns = rnf.get(compId,i);
					for (RoundNode rn: rns) {
						rnf.delete(rn);
					}
					rnf.dropListFromCache(compId,i);
				}

				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Successful deletion of graphing data for " + c.getLongName());
				List<String> retval = new ArrayList<String>();
				retval.add("Successful deletion of graphing data.");
				return immediate(retval);

			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Unrecognized RoundNode management action " + action);
				List<String> retval = new ArrayList<String>();
				retval.add("Unrecognized RoundNode management action " + action);
				return immediate(retval);
			}
	
		} catch (Exception ex) {			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem managing RoundNodes.", ex);
			List<String> retval = new ArrayList<String>();
			retval.add("FAILURE: " + ex.getLocalizedMessage());
			return immediate(retval);
		}

	}

}
