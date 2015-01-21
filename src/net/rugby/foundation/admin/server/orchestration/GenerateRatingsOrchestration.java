/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.RatingQuery;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

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

	private IPlayerRatingFactory prf;

	private ITopTenListFactory ttlf;

	public GenerateRatingsOrchestration(IOrchestrationConfigurationFactory ocf, IMatchRatingEngineSchemaFactory mresf, IQueryRatingEngineFactory qref, 
			IRatingQueryFactory rqf, IPlayerRatingFactory prf, ITopTenListFactory ttlf) {
		this.ocf = ocf;
		this.mresf = mresf;
		this.qref = qref;
		this.rqf = rqf;
		this.prf = prf;
		this.ttlf = ttlf;
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
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, ((RatingQuery)target).toString());
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Attempt to invoke Generate Rating Orchestration with null RatingQuery provided");
		}
		if (target != null) {
			// if this has already been run and is a rerun request, clean up what is out there now.
//			boolean rerun = false;
//			if (!target.getStatus().equals(Status.NEW)) {
//				// player ratings
//				prf.deleteForQuery(target);
//				
//				// ttl stuff
//				if (target.getTopTenListId() != null) {
//					ttlf.delete(ttlf.get(target.getTopTenListId()));
//				}
//				rerun = true;
//			}
			
			// now we can run
			target.setStatus(Status.RUNNING);
			rqf.put(target);


			// get the engine
			IRatingEngineSchema mres = mresf.getDefault();
			assert (mres != null);
			IQueryRatingEngine mre = qref.get(mres, target);
			assert (mre != null);
			mre.setQuery(target);
			mre.generate(mres,target.getScaleStanding(),target.getScaleComp(),target.getScaleTime(),true);
			
//			// now re-create the TTL if needed
//			if (rerun) {
//				Long sponsorId = null;
//				if (target.getRatingMatrix().getRatingGroup().getRatingSeries().getSponsorId() != null) {
//					sponsorId = target.getRatingMatrix().getRatingGroup().getRatingSeries().getSponsorId();
//				} else if (target.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp() != null && target.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp().getSponsorId() != null) {
//					sponsorId = target.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp().getSponsorId();
//				}
//				
//				TopTenSeedData data = new TopTenSeedData(target.getId(), title, "", target.getRatingMatrix().getRatingGroup().getRatingSeries().getHostCompId(), target.getRoundIds(), 10, sponsorId);
//				data.setContext(context);
//				ITopTenList ttl = ttlf.create(data);
//				
//				
//				ttlf.put(ttl);
//			}

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
