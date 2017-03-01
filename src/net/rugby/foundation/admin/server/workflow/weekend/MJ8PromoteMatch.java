package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IAdminTaskFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory;
import net.rugby.foundation.admin.server.rules.ICoreRuleFactory.MatchRule;
import net.rugby.foundation.admin.server.rules.IRule;
import net.rugby.foundation.admin.server.workflow.ResultWithLog;
import net.rugby.foundation.admin.server.workflow.RetryRequestException;
import net.rugby.foundation.admin.server.workflow.weekend.results.MS9Promoted;
import net.rugby.foundation.admin.shared.IAdminTask;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.promote.IPromoter;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.WorkflowStatus;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import org.joda.time.DateTime;

import com.google.appengine.tools.pipeline.Job3;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;


public class MJ8PromoteMatch extends Job3<MS9Promoted, Long, String, ResultWithLog> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IMatchGroupFactory mf;
	transient private ICoreRuleFactory crf;
	transient private IPlaceFactory spf;
	transient private IPromoter promoter;
	transient private IAdminTaskFactory atf;
	transient private IPlayerFactory pf;
	transient private ITopTenListFactory ttlf;

	public MJ8PromoteMatch() {
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.INFO);
	}


	@Override
	public Value<MS9Promoted> run(Long matchId, String label, ResultWithLog prior) throws RetryRequestException {

		try {

			MS9Promoted retval = new MS9Promoted();
			retval.matchId = matchId;

			// just let failure cascade to the end so it can finish
			if (prior == null || prior.success == false) {
				retval.log.add(this.getClass().getSimpleName() + " ...FAIL");
				retval.success = false;
				return immediate(retval);
			}

			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.mf = injector.getInstance(IMatchGroupFactory.class);
			this.crf = injector.getInstance(ICoreRuleFactory.class);
			this.spf = injector.getInstance(IPlaceFactory.class);
			this.promoter = injector.getInstance(IPromoter.class);
			this.atf = injector.getInstance(IAdminTaskFactory.class);
			this.pf = injector.getInstance(IPlayerFactory.class);
			this.ttlf = injector.getInstance(ITopTenListFactory.class);
			
			IMatchGroup match = mf.get(matchId);
			if (match != null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": checking for match promoting for " + match.getDisplayName());
			}
			WorkflowStatus fromState = WorkflowStatus.RATED;
			WorkflowStatus toState = WorkflowStatus.PROMOTED;
			IRule<IMatchGroup> rule = crf.get(match, MatchRule.MATCH_TO_PROMOTE);		

			// first check if we are already further along than this
			if (match.getWorkflowStatus().ordinal() > fromState.ordinal()) {
				retval.log.add(this.getClass().getSimpleName() + " ...OK");
				retval.success = true;
				return immediate(retval);
			}

			assert (match.getWorkflowStatus().equals(fromState));

			// 			
			if (rule.test()) {
				// update state
				IServerPlace place = spf.getForGuid(match.getGuid());
				if (place != null && place.getListId() != null) {
					List<IPlayer> result = promoter.promoteList(place.getListId());
					
					//TODO Create admin tasks for players without twitter handles and who have twitterNotAvailable = false or null. 
					ITopTenList ttl = ttlf.get(place.getListId());
					for (IPlayer p : result) {
						if (p.getTwitterHandle() != null && !p.getTwitterHandle().isEmpty()) {
							retval.log.add("Match List tweet to " + p.getDisplayName());
						} else if (p.getTwitterNotAvailable() == null || p.getTwitterNotAvailable().equals(false)) {
							retval.log.add("Need twitter handle for " + p.getDisplayName());
							Long ttiId = null;
							for (ITopTenItem tti : ttl.getList()){
								if (tti.getPlayerId().equals(p.getId())){
									ttiId = tti.getId();
									break;
								}
							}
							IAdminTask task = atf.getNewEditPlayerTwitterTask("Need twitter handle for " + p.getDisplayName(), "", p, ttiId, ttl.getId(), true, null,null,null);
							atf.put(task);
							p.getTaskIds().add(task.getId());
							pf.put(p);
						} else {
							retval.log.add("No twitter handle for " + p.getDisplayName());
						}
					}
				}
				
				match.setWorkflowStatus(toState);
				mf.put(match);
				retval.log.add(rule.getLog());
				retval.log.add(match.getDisplayName() + " promotion complete at " + DateTime.now().toString());
				retval.success = true;
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, this.getJobDisplayName() + ": completing promoting for " + match.getDisplayName());

				return immediate(retval);
			} else {
				throw new RetryRequestException(match.getDisplayName() + " not ready for promotion at " + DateTime.now().toString() + " (no guid for TTL yet)");
			}

		} catch (Exception ex) {			
			if (ex instanceof RetryRequestException) {
				throw ex;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
				if (matchId != null) {
					// error out the match
					if (injector == null) {
						injector = BPMServletContextListener.getInjectorForNonServlets();
					}

					this.mf = injector.getInstance(IMatchGroupFactory.class);
					IMatchGroup match = mf.get(matchId);
					match.setWorkflowStatus(WorkflowStatus.ERROR);
					mf.put(match);
				}	

				MS9Promoted retval = new MS9Promoted();
				retval.matchId = matchId;
				retval.success = false;
				retval.log.add(ex.getLocalizedMessage());
				return immediate(retval);
			}
		}
	}

}


