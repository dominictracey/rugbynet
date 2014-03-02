/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.appengine.api.taskqueue.TaskOptions;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class GenerateRatingsOrchestration extends OrchestrationCore<IRatingQuery> {
	private Long compId;

	private IOrchestrationConfigurationFactory ocf;
	private IMatchRatingEngineSchemaFactory mresf;
	private IQueryRatingEngineFactory qref;

	private IRatingQueryFactory rqf;

	public GenerateRatingsOrchestration(IOrchestrationConfigurationFactory ocf, IMatchRatingEngineSchemaFactory mresf, IQueryRatingEngineFactory qref, IRatingQueryFactory rqf) {
		this.ocf = ocf;
		this.mresf = mresf;
		this.qref = qref;
		this.rqf = rqf;
	}	

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		if (target != null) {
			target.setStatus(Status.RUNNING);
			rqf.put(target);
			
//			ocf.setCompId(compId);
//			IOrchestrationConfiguration conf = ocf.get();
//			if (conf.getRatingActions().get(RatingActions.GENERATE.getValue())) {
				// get the engine
				IRatingEngineSchema mres = mresf.getDefault();
				assert (mres != null);
				IQueryRatingEngine mre = qref.get(mres, target);
				assert (mre != null);
				//pmif.query(compId, roundId, posi, countryId, teamId, null);
				mre.setQuery(target);
				mre.generate(mres,target.getScaleStanding(),target.getScaleComp(),target.getScaleTime());
//				List<IPlayerMatchInfo> pmis = pmif.query(compId, roundId, posi, countryId, teamId, null);
	//			return pmis;
	//		}
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		compId = id;
		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return compId;
	}

}
