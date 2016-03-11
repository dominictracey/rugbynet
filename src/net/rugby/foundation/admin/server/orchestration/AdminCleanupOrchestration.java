/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class AdminCleanupOrchestration extends OrchestrationCore<IRatingQuery> {
	private Long compId;

	private IOrchestrationConfigurationFactory ocf;

	private IRatingQueryFactory rqf;

	private IPlayerRatingFactory prf;

	private IMatchGroupFactory mgf;

	public AdminCleanupOrchestration(IOrchestrationConfigurationFactory ocf, IRatingQueryFactory rqf, IPlayerRatingFactory prf, IMatchGroupFactory mgf) {
		this.ocf = ocf;
		this.rqf = rqf;
		this.prf = prf;
		this.mgf = mgf;
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
//		rqf.deleteAll();
//		prf.deleteAll();

		PipelineService service = PipelineServiceFactory.newPipelineService();

		List<? extends IMatchGroup> list = mgf.getMatchesWithPipelines();
		Iterator<? extends IMatchGroup> it = list.iterator();
		while (it.hasNext()) {
			IMatchGroup m = it.next();
			String id = m.getFetchMatchStatsPipelineId();
			m.setFetchMatchStatsPipelineId(null);
			mgf.put(m);
			try {
				service.deletePipelineRecords(id,true,false);
			} catch (NoSuchObjectException nsox) {
				// it's ok, just was a dangling reference in the match record
			}
		}
		
		// clean up the rating details that are more than two weeks old
		prf.cleanUp();
		
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
