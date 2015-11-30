/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

import com.google.appengine.api.taskqueue.TaskOptions;
import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.factory.IResultFetcherFactory;
import net.rugby.foundation.admin.server.model.IResultFetcher;
import net.rugby.foundation.admin.shared.IOrchestrationConfiguration;
import net.rugby.foundation.admin.shared.AdminOrchestrationActions.MatchActions;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IMatchResultFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.SimpleScoreMatchResult;

/**
 * @author home
 *
 */
public class FetchBasicScoreMatchResultOrchestration extends OrchestrationCore<IMatchGroup> {
	
	private Long compId;
	private IResultFetcherFactory rff;
	private ICompetitionFactory cf;
	private IMatchResultFactory mrf;
	private IMatchGroupFactory mgf;
	private IOrchestrationConfigurationFactory ocf;


	
//	@Inject
//	public void setFactories(IResultFetcherFactory rff, ICompetitionFactory cf, IMatchResultFactory mrf, IMatchGroupFactory mgf, IOrchestrationConfigurationFactory ocf) {
//		this.rff = rff;
//		this.cf = cf;
//		this.mrf = mrf;
//		this.mgf = mgf;
//		this.ocf = ocf;
//	}

	
	public FetchBasicScoreMatchResultOrchestration(IResultFetcherFactory rff, ICompetitionFactory cf, IMatchResultFactory mrf, IMatchGroupFactory mgf, IOrchestrationConfigurationFactory ocf) {
		this.rff = rff;
		this.cf = cf;
		this.mrf = mrf;
		this.mgf = mgf;
		this.ocf = ocf;
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
			assert (compId != null);
			if (compId != null) {
				ocf.setCompId(compId);
				IOrchestrationConfiguration conf = ocf.get();
				if (conf.getMatchActions().get(MatchActions.FETCH.getValue())) {

					// don't fetch until locked
					if (target.getLocked()) {
						ICompetition comp = cf.get(compId);
	
						IResultFetcher fetcher = rff.getResultFetcher(compId, comp.getNextRound(), IMatchResult.ResultType.MATCHES);
	
						IMatchResult mr = fetcher.getResult(target);
	
						//save the new result
						// note that we leave any prior results fetched orphaned in a sense, though the MR does have the MatchId so you can query for a history of sorts.
						//	For instance, we may get into a situation where we want to know the halftime score. We might want to have the match contain a list of MR or a Map<Status,IMatchResult>
						//	so you could re-trace the progress of the match.
						if (mr != null) {
							
							mrf.put(mr);
							target.setSimpleScoreMatchResult(((SimpleScoreMatchResult)mr));
							target.setSimpleScoreMatchResultId(mr.getId());
							target.setStatus(mr.getStatus());
	
							mgf.put(target);
	
							AdminEmailer ae = new AdminEmailer();
							ae.setSubject("Match result fetched: " + target.getDisplayName() + "(" + target.getId() + ")");
							ae.setMessage(mr.getStatus().toString());
							ae.send();
						}
					}
				}
			}
		} catch (Throwable caught) {
			String subj = "ERROR! Match result fetching exception: ";
			if (target != null) {
				subj += target.getDisplayName() + "(" + target.getId() + ")";
			}
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
