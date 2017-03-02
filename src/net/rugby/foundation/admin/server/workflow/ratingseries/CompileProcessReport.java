package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class CompileProcessReport extends Job4<MS8Rated, List<ProcessRatingQueryResult>, Long, Long, Integer> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private ISeriesConfigurationFactory scf;
	transient private IMatchGroupFactory mf;
	transient private IRatingSeriesFactory rsf;
	transient private IRatingQueryFactory rqf;
	transient private IRatingGroupFactory rgf;


	public CompileProcessReport() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<MS8Rated> run(List<ProcessRatingQueryResult> successes, Long scid, Long matchId, Integer targetUROrdinal) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.rsf = injector.getInstance(IRatingSeriesFactory.class);
			this.rqf = injector.getInstance(IRatingQueryFactory.class);
			this.rgf = injector.getInstance(IRatingGroupFactory.class);

			// have to refetch to get pipelineId
			ISeriesConfiguration sc = scf.get(scid);

			ProcessRatingSeriesResult retval = new ProcessRatingSeriesResult();

			boolean allGood = true;
			//			for (ProcessRatingQueryResult b : successes) {
			//				if (!b.success) {
			//					allGood = false;
			//					break;
			//				}
			//			}

			// check to see if all the queries in the targetRound are complete
			//IRatingSeries rs = sc.getSeries();
			IRatingSeries rs = rsf.get(sc.getSeriesId());
			// find the RatingGroup for the target round
			IRatingGroup rg = rgf.getForUR(rs.getId(), targetUROrdinal);
			//				if (rg.getUniversalRoundOrdinal() == sc.getTargetRoundOrdinal()) {
			assert (rg.getRatingMatrices() != null);
			for (IRatingMatrix rm : rg.getRatingMatrices()) {
				assert (rm.getRatingQueries() != null);
				for (Long rqid : rm.getRatingQueryIds()) {
					IRatingQuery rq = rqf.get(rqid);
					if (rq.getStatus() != IRatingQuery.Status.COMPLETE) {
						allGood = false;
						break;  //TODO doesn't break out of outer loops.
					}
				}
			}
			//				}
			//			}

			assert(sc.getPipelineId() != null);

			// if they are all good, set the series status to OK
			if (allGood) {
				sc.setStatus(ISeriesConfiguration.Status.OK);
				sc.setLastRun(DateTime.now().toDate());
				sc.setLastRoundOrdinal(sc.getTargetRoundOrdinal());
				sc.setTargetRoundOrdinal(sc.getTargetRoundOrdinal()+1);
				retval.success = true;

			} else {
				sc.setStatus(ISeriesConfiguration.Status.PENDING);
				sc.setLastRun(DateTime.now().toDate());		
				retval.success = false;
			}

			scf.put(sc);

			retval.jobId = sc.getPipelineId();
			for (ProcessRatingQueryResult prqr : successes) {
				retval.log.addAll(prqr.log);
			}

			MS8Rated wrapper = new MS8Rated();
			wrapper.processSubTreeResults = retval;
			wrapper.log.addAll(retval.log);
			wrapper.success = false;

			// update the generating match's status
			if (matchId != null && matchId != 0L) {
				IMatchGroup m = mf.get(matchId);
				if (m != null && m.getGuid() != null && !m.getGuid().isEmpty()) {
					wrapper.success = true;
					m.setWorkflowStatus(WorkflowStatus.RATED);
					mf.put(m);
				}
			} else {
				wrapper.success = retval.success; // fucking stupid and confusing
			}
			
			// also, if this is a BY_POSITION series, drop the cached versions of the RoundNode lists to force the new one to be created.
			if (sc.getMode().equals(RatingMode.BY_POSITION)) {
				IRoundNodeFactory rnf = injector.getInstance(IRoundNodeFactory.class);
				for (int i = 0; i<10; ++i)
					rnf.dropListFromCache(sc.getHostCompId(),i);
			}

			return immediate(wrapper);

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			ProcessRatingSeriesResult retval = new ProcessRatingSeriesResult();
			retval.log.add("Failed to aggregate logs for series processing: " + ex.getLocalizedMessage());
			MS8Rated wrapper = new MS8Rated();
			wrapper.processSubTreeResults = retval;
			wrapper.success = false;
			return immediate(wrapper);
		}

	}

}
