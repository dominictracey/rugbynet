package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.model.IRatingSeriesManager;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS8Rated;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.Status;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class ProcessRatingSeries extends Job3<MS8Rated, Long, Long, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private ISeriesConfigurationFactory scf;
	transient private IUniversalRoundFactory urf;
	transient private IRoundFactory rf;
	transient private IMatchGroupFactory mf;
	transient private IRatingGroupFactory rgf;
	
	private boolean retryFailedQueries = true;

	public ProcessRatingSeries() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}


	protected class NameAndId {
		String name;
		Long id;

		NameAndId(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	/**
	 * return generator jobId
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<MS8Rated> run(Long seriesConfigId, Long matchId, Long ratingGroupId) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.rgf = injector.getInstance(IRatingGroupFactory.class);
			
			ISeriesConfiguration seriesConfig = scf.get(seriesConfigId);
			
			if (seriesConfig == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Generate rating series pipeline invoked with null.");
				return null;
			}
			
			// first set the pipelineId we are running as
			// comes back as pipeline-key("asd-423-sdf-sdgg") - strtok on double quote
			seriesConfig.setPipelineId(this.getPipelineKey().toString().split("\"")[1]);
			
			// also, if we have a matchId, force the target round to be the round containing the match
			UniversalRound target = seriesConfig.getTargetRound();
			IMatchGroup match = null;
			if (matchId != null && matchId != 0L) {
				match = mf.get(matchId);
				this.urf = injector.getInstance(IUniversalRoundFactory.class);
				this.rf = injector.getInstance(IRoundFactory.class);
				if (match != null) {
					target = urf.get(rf.get(match.getRoundId()));
					seriesConfig.setTargetRound(target);
					seriesConfig.setTargetRoundOrdinal(target.ordinal);
				}
			}
			
			scf.put(seriesConfig);

			// do we have a target round?
			if (seriesConfig.getTargetRound() == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Generate rating series pipeline invoked with no target round.");
				return null;			
			}

			IRatingSeriesManager rm = injector.getInstance(IRatingSeriesManager.class);
			IRatingSeries series = seriesConfig.getSeries();
			if (series == null) {
				series = rm.initialize(seriesConfig);
			} 
			
			if (seriesConfig.getStatus() != Status.PENDING) {
				seriesConfig.setStatus(Status.PENDING);
				scf.put(seriesConfig);
			}

			// Note that we have a passed in ratingGroupId that we choose not to use.
			// We secure it before we call in here because we had some concurrency issues with multiple groups being created when this
			// job is called in rapid fire succession. The orchestration doesn't do this so it passes in null for that value.
			IRatingGroup group = null;
			if (ratingGroupId != null) {
				group = rgf.get(ratingGroupId);
			} else {
				group = rm.getRatingGroup(seriesConfig, target);
			}
			
			if (group != null) {

				// if we are running as part of the weekend workflow, we may just want to process the single RatingQuery for a match, see if that is the case
				
 				// for each of the RatingQueries, generate a new jorb to process it
				List<Value<ProcessRatingQueryResult>> _successes = new ArrayList<Value<ProcessRatingQueryResult>>();
				for (IRatingMatrix rmx : group.getRatingMatrices()) {
					for (IRatingQuery rq : rmx.getRatingQueries()) {
		
						if (rq.getStatus() == IRatingQuery.Status.NEW || rq.getStatus() == IRatingQuery.Status.ERROR) {
							if (match == null || (series.getMode() == RatingMode.BY_MATCH && rq.getTeamIds().contains(match.getHomeTeamId()) && rq.getTeamIds().contains(match.getVisitingTeamId()))) {
								// if the query has previously failed, we may want to retry it
								if (retryFailedQueries && rq.getStatus() == IRatingQuery.Status.ERROR) {
									rq.setStatus(IRatingQuery.Status.NEW);
									IRatingQueryFactory rqf = injector.getInstance(IRatingQueryFactory.class);
									if (rqf != null)
										rqf.put(rq);
								}
								if (rq.getStatus() == IRatingQuery.Status.NEW) {
									FutureValue<ProcessRatingQueryResult> retval = futureCall(new ProcessRatingQuery(), immediate(rq.getId()), immediate(series.getId()));
									_successes.add(retval);
								}
							}
						}
					}
				}

				Value<MS8Rated> retval = futureCall(new CompileProcessReport(), futureList(_successes), immediate(seriesConfig.getId()), immediate(matchId), immediate(target.ordinal));

				return retval;
			} else {
				ProcessRatingSeriesResult retval = new ProcessRatingSeriesResult(null, this.getPipelineKey().toString());
				MS8Rated wrapper = new MS8Rated();
				wrapper.success = false;
				wrapper.processSubTreeResults = retval;
				return immediate(wrapper);

			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			MS8Rated wrapper = new MS8Rated();
			wrapper.success = false;
			wrapper.processSubTreeResults = null;
			wrapper.log.add(ex.getLocalizedMessage());
			return immediate(wrapper);
		}

	}

}

