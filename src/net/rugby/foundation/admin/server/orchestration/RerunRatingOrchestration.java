/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.workflow.ratingseries.ProcessRatingQuery;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

/**
 * @author home
 *
 */
public class RerunRatingOrchestration extends OrchestrationCore<IRatingQuery> {

	private Long compId;
	private IRatingGroupFactory rgf;
	private IRatingMatrixFactory rmf;



	public RerunRatingOrchestration( IRatingGroupFactory rgf, IRatingMatrixFactory rmf) {
		this.rgf = rgf;
		this.rmf = rmf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		return builder.param("id",target.getId().toString());
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		try {


			PipelineService service = PipelineServiceFactory.newPipelineService();

			IRatingQuery rq = target;
			
			String pipelineId = "";

			try {
				if (rq.getRatingMatrixId() != null) {
					IRatingMatrix rm = rmf.get(rq.getRatingMatrixId());
					if (rm != null && rm.getRatingGroupId() != null) {
						IRatingGroup rg = rgf.get(rm.getRatingGroupId());
						if (rg != null) {
							pipelineId = service.startNewPipeline(new ProcessRatingQuery(), rq.getId(), rg.getRatingSeriesId(), new JobSetting.MaxAttempts(3));
						}
					}
				}
				
				if (!pipelineId.isEmpty()) {
					
				}
				
			} catch (RetriesExhaustedException ree) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Giving up", ree);
				return;
			}

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);


		} catch (Throwable caught) {
			String subj = "ERROR! Series creation exception: ";
			if (target != null) {
				//subj += target.getDisplayName() + "(" + target.getId() + ")";
			}

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Rerun Query Error", caught);

			AdminEmailer ae = new AdminEmailer();
			ae.setSubject(subj);
			ae.setMessage(caught.getMessage());
			ae.send();			
		}


	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		this.compId = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return compId;
	}

}
