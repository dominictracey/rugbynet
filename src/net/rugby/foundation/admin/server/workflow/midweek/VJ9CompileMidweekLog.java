package net.rugby.foundation.admin.server.workflow.midweek;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.midweek.results.VS0MidweekResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class VJ9CompileMidweekLog extends Job2<VS0MidweekResult, UniversalRound, List<MS8Rated>> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;


	public VJ9CompileMidweekLog() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}
	
	@Override
	public Value<VS0MidweekResult> run(UniversalRound ur, List<MS8Rated> seriesResults) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}
		
			
			VS0MidweekResult retval = new VS0MidweekResult();
			retval.success = true;  

			for (MS8Rated mr : seriesResults) {
				if (mr != null) {
					retval.log.addAll(mr.log);
					if (!mr.success) {
						retval.success = false;
					}
				} else {
					retval.success = false; //unfortunately we don't know why we have a null in the list
				}
			}

			return immediate(retval);
			
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
