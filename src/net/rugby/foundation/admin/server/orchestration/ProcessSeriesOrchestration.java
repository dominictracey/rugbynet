/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.ISeriesConfigurationFactory;
import net.rugby.foundation.admin.server.workflow.ratingseries.ProcessRatingSeries;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.ISeriesConfiguration;

/**
 * @author home
 *
 */
public class ProcessSeriesOrchestration extends OrchestrationCore<ISeriesConfiguration> {

	private Long compId;
	private IOrchestrationConfigurationFactory ocf;
	private IAdminTaskFactory atf;
	private ISeriesConfigurationFactory rsf;


	public ProcessSeriesOrchestration(ISeriesConfigurationFactory rsf, 
			IOrchestrationConfigurationFactory ocf, IAdminTaskFactory atf) {

		this.rsf = rsf;
		this.ocf = ocf;
		this.atf = atf;
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

			ISeriesConfiguration sc = target;

			//IRatingSeries rs = rsf.get(sc.getSeriesId());
			
			String pipelineId = "";

			// first check if this match already has a pipeline going and kill it if it does
			if (sc.getPipelineId() != null && !sc.getPipelineId().isEmpty()) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Flushing out existing pipeline records.");
				
				// delete adminTasks first
				List<? extends IAdminTask> tasks = atf.getForPipeline(sc.getPipelineId());
				atf.delete((List<IAdminTask>) tasks);

				// now the pipeline records
				service.deletePipelineRecords(sc.getPipelineId(), true, false);
				sc.setPipelineId(null);
				rsf.put(sc);
			}

			try {
				pipelineId = service.startNewPipeline(new ProcessRatingSeries(), sc, new JobSetting.MaxAttempts(3));
			} catch (RetriesExhaustedException ree) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Giving up", ree);
				service.deletePipelineRecords(sc.getPipelineId(), true, false);
				sc.setPipelineId(null);
				rsf.put(sc);
				return;
			}
//			sc.setPipelineId(pipelineId);
//			rsf.put(sc);

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);


		} catch (Throwable caught) {
			String subj = "ERROR! Series creation exception: ";
			if (target != null) {
				//subj += target.getDisplayName() + "(" + target.getId() + ")";
			}

			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Process Series Error", caught);

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
