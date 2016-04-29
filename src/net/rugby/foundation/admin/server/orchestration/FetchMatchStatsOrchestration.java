/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.RetriesExhaustedException;
import com.google.appengine.tools.pipeline.JobInfo.State;
import com.google.appengine.tools.pipeline.JobSetting;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.server.workflow.fetchstats.FetchMatchStats;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

/**
 * @author home
 *
 */
public class FetchMatchStatsOrchestration extends OrchestrationCore<IMatchGroup> {
	
	private Long compId;
	private IMatchGroupFactory mgf;
	private IOrchestrationConfigurationFactory ocf;
	private IAdminTaskFactory atf;

	
	public FetchMatchStatsOrchestration(IMatchGroupFactory mgf, 
			IOrchestrationConfigurationFactory ocf, IAdminTaskFactory atf) {

		this.mgf = mgf;
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
//			assert (compId != null);
//			if (compId != null) {
//				ocf.setCompId(compId);
//				IOrchestrationConfiguration conf = ocf.get();
//				if (conf.getMatchActions().get(MatchActions.FETCHSTATS.getValue())) {

					// don't fetch until locked
					if (target.getLocked()) {
//						Country c = new Country(5000L, "None", "NONE", "---", "Unassigned");
//						countryf.put(c);

						PipelineService service = PipelineServiceFactory.newPipelineService();

						IMatchGroup match = target;

						String pipelineId = "";

						// first check if this match already has a pipeline going and kill it if it does
						if (match.getFetchMatchStatsPipelineId() != null && !match.getFetchMatchStatsPipelineId().isEmpty()) {
							// delete adminTasks first
							List<? extends IAdminTask> tasks = atf.getForPipeline(match.getFetchMatchStatsPipelineId());
							atf.delete((List<IAdminTask>) tasks);

							// now the pipeline records
							service.deletePipelineRecords(match.getFetchMatchStatsPipelineId(), true, false);
							match.setFetchMatchStatsPipelineId(null);

						}

						//pipelineId = service.startNewPipeline(new GenerateMatchRatings(pf, tmsf, pmsf, countryf, mref, pmrf), match, new JobSetting.MaxAttempts(1));
						try {
							pipelineId = service.startNewPipeline(new FetchMatchStats(), match.getId(), new JobSetting.MaxAttempts(3));
						} catch (RetriesExhaustedException ree) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Bad stuff", ree);
						}
						match.setFetchMatchStatsPipelineId(pipelineId);
						mgf.put(match);

						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "pipelineId: " + pipelineId);

						//				while (true) {
						//					Thread.sleep(2000);

						//				}

//						return pipelineId;
//						if (mr != null) {
//							
//							mrf.put(mr);
//							target.setSimpleScoreMatchResult(((SimpleScoreMatchResult)mr));
//							target.setSimpleScoreMatchResultId(mr.getId());
//							target.setStatus(mr.getStatus());
//	
//							mgf.put(target);
//	
//							AdminEmailer ae = new AdminEmailer();
//							ae.setSubject("Match result fetched: " + target.getDisplayName() + "(" + target.getId() + ")");
//							ae.setMessage(mr.getStatus().toString());
//							ae.send();
//						}
//					}
//				}
			}
		} catch (Throwable caught) {
			String subj = "ERROR! Match stats fetching exception: ";
			if (target != null) {
				subj += target.getDisplayName() + "(" + target.getId() + ")";
			}
			
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Match Stats Fetcher Orchestration Error", caught);
			
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
