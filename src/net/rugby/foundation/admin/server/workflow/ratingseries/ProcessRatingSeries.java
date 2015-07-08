package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.model.IRatingSeriesManager;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.admin.shared.ISeriesConfiguration.Status;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class ProcessRatingSeries extends Job1<ProcessRatingSeriesResult, ISeriesConfiguration> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	private ISeriesConfigurationFactory scf;
	private IRatingSeriesFactory rsf;
	private IRatingGroupFactory rgf;

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
	public Value<ProcessRatingSeriesResult> run(ISeriesConfiguration seriesConfig) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.scf = injector.getInstance(ISeriesConfigurationFactory.class);
			this.rsf = injector.getInstance(IRatingSeriesFactory.class);
			this.rgf = injector.getInstance(IRatingGroupFactory.class);
			
			if (seriesConfig == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Generate rating series pipeline invoked with null.");
				return null;
			}
			
			// first set the pipelineId we are running as
			// comes back as pipeline-key("asd-423-sdf-sdgg") - strtok on double quote
			seriesConfig.setPipelineId(this.getPipelineKey().toString().split("\"")[1]);
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

			IRatingGroup group = null;
			int i = 1;
			// do we need a new rating group or is there one in place we need to continue processing?
			if (rm.readyForNewGroup(seriesConfig)) {
				group = rm.doRatingGroup(series, seriesConfig.getTargetRound());
			} else {
				for (IRatingGroup rg : series.getRatingGroups()) {
					if (rg.getUniversalRoundOrdinal() == seriesConfig.getTargetRoundOrdinal()) {
						// only do partial processing on BY_MATCH series
						if (seriesConfig.getMode().equals(RatingMode.BY_MATCH)) {
							group = rg;
							break;
						} else { 
							// otherwise delete the existing group and create another
//							series.getRatingGroupIds().remove(rg.getId());
//							rsf.put(series);
//							rgf.deleteTTLs(rg);
//							rgf.delete(rg);
//							group = rm.doRatingGroup(series, seriesConfig.getTargetRound());
							
							// What does it mean to get here?
							// Maybe there are some queries that need to be retried or something?
							assert(false);
							break;
						}
					}
				}
			}
			
			if (group != null) {
 				// for each of the RatingQueries, generate a new jorb to process it
				List<Value<Boolean>> successes = new ArrayList<Value<Boolean>>();
				for (IRatingMatrix rmx : group.getRatingMatrices()) {
					for (IRatingQuery rq : rmx.getRatingQueries()) {
						if (rq.getStatus().equals(IRatingQuery.Status.NEW)) {
							FutureValue<Boolean> retval = futureCall(new ProcessRatingQuery(), immediate(rq));
							successes.add(retval);
						}
					}
				}

				futureCall(new CompileProcessReport(), futureList(successes), immediate(seriesConfig));

				ProcessRatingSeriesResult retval = new ProcessRatingSeriesResult(group, this.getPipelineKey().toString());

				return new ImmediateValue<ProcessRatingSeriesResult>(retval);
			} else {
				ProcessRatingSeriesResult retval = new ProcessRatingSeriesResult(null, this.getPipelineKey().toString());

				return new ImmediateValue<ProcessRatingSeriesResult>(retval);

			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
			return null;
		}

	}

}
