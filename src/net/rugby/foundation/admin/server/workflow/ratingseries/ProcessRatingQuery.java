package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.tools.pipeline.ImmediateValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class ProcessRatingQuery extends Job1<Boolean, IRatingQuery> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	private IRatingQueryFactory rqf;
	private IMatchRatingEngineSchemaFactory mresf;
	private IQueryRatingEngineFactory qref;
	private IPlayerRatingFactory prf;
	private ITopTenListFactory ttlf;
	private IRoundFactory rf;
	private IUniversalRoundFactory urf;
	private IMatchGroupFactory mgf;
	private ICompetitionFactory cf;

	private boolean impact;


	public ProcessRatingQuery() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<Boolean> run(IRatingQuery rq) {

		IMatchGroup match = null;  // to support delayed guid linking
		IRatingQuery preQuery = null; // for tracking player movement
		
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.rqf = injector.getInstance(IRatingQueryFactory.class);
		this.mresf = injector.getInstance(IMatchRatingEngineSchemaFactory.class);
		this.qref = injector.getInstance(IQueryRatingEngineFactory.class);
		this.prf = injector.getInstance(IPlayerRatingFactory.class);
		this.ttlf = injector.getInstance(ITopTenListFactory.class);
		this.rf = injector.getInstance(IRoundFactory.class);
		this.urf = injector.getInstance(IUniversalRoundFactory.class);
		this.mgf = injector.getInstance(IMatchGroupFactory.class);
		this.cf = injector.getInstance(ICompetitionFactory.class);

		
		// first see if we are re-running, in which case clear out any ratings already in place
		if (!rq.getStatus().equals(Status.NEW)) {
			prf.deleteForQuery(rq);
			
			if (rq.getTopTenListId() != null) {
				ITopTenList list = ttlf.get(rq.getTopTenListId());
				if (list != null) {
					ttlf.delete(list);
				}
				rq.setTopTenListId(null);
			}
		}

		rq.setStatus(Status.RUNNING);
		rqf.put(rq);


		// get the engine
		IRatingEngineSchema mres = mresf.getDefault();
		if (rq.getSchemaId() != null) {
			mres = mresf.get(rq.getSchemaId());
		}
		
		assert (mres != null);
		IQueryRatingEngine mre = qref.get(mres, rq);
		assert (mre != null);

		boolean ok = false;
		boolean inForm = false;
		boolean bestYear = false;
		
		try {
			ok = mre.setQuery(rq);

			if (ok) {
				mre.generate(mres,rq.getScaleStanding(),rq.getScaleComp(),rq.getScaleTime(), rq.getScaleMinutesPlayed(), false);
			} else {
				// stats aren't ready
				rq.setStatus(Status.NEW);
				rqf.put(rq);
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem generating ratings", ex);
			rq.setStatus(Status.ERROR);
			rqf.put(rq);
			ok = false;
		}
		
		if (ok) {
			// the engine will set the status to complete if successful
			IRatingQuery procked = rqf.get(rq.getId());

			// now create the TTL
			String title = "Top Ten ";
			String context = "";
			
			if (rq.getRatingMatrix() == null && rq.getRatingMatrixId() != null) {
				rq = rqf.buildUplinksForQuery(rq);
			}
			
			if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_MATCH)) {
				title += "Players from ";
				IRound r = rf.get(rq.getRoundIds().get(0));

				if (r != null) {
					for (IMatchGroup m : r.getMatches()) {
						if (rq.getTeamIds().contains(m.getHomeTeamId()) && rq.getTeamIds().contains(m.getVisitingTeamId())) {
							match = m;
							break;
						}
					}
					if (match != null) {						
						context = match.getHomeTeam().getShortName() + " vs. " + match.getVisitingTeam().getShortName();
						title += context;
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find match in setting title for RQ " + rq.getId() + " teamIds " + rq.getTeamIds().toString());
					}
				}
			} else if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_POSITION)) {
				// is this In Form or Last Round?
				if (rq.getRatingMatrix().getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form ";
					inForm = true;
				} else if (rq.getRatingMatrix().getCriteria().equals(Criteria.BEST_YEAR)) {
					bestYear = true;
				} else if (rq.getRatingMatrix().getCriteria().equals(Criteria.AVERAGE_IMPACT)) {
					title += "Impact ";
					impact = true;
				}
				
				title += rq.getPositions().get(0).getPlural();
				
				Long hostId = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getHostCompId();
				if (hostId != null) {
					ICompetition hostComp = cf.get(hostId);
					if (hostComp.getTTLTitleDesc() != null && !hostComp.getTTLTitleDesc().isEmpty()) {
						title += " of " + hostComp.getTTLTitleDesc();
					}
				}
				
				if (inForm || bestYear || impact) {
					title += " Through ";
				} else {
					title += " From ";
				}
				
				// now we need the last round
				if (rq.getRoundIds().size() == 0) {
					title += "NO MATCHING DATA"; // this is not a great place to be, no players make the list...
				} else if (rq.getRoundIds().size() == 1) {
					IRound r = rf.get(rq.getRoundIds().get(0));
					title += r.getName();
				} else {
					IRound last = null;
					for (Long rid : rq.getRoundIds()) {
						if (last == null) {
							last = rf.get(rid);
						} else {
							IRound t = rf.get(rid);
							if (t.getEnd().after(last.getEnd())) {
								last = t;
							}
						}
					}
					if (rq.getCompIds().size() < 2) {
						context = last.getName();
						title += context;
					} else {
						// virtual comp
						UniversalRound ur = urf.get(last);
						title += ur.longDesc;
					}
				}
				
				if (inForm) {
					// figure out the last list for this position
					//rq.getRatingMatrix().getRatingQueries().indexOf(rq); // << This breaks on rerun, need to code by hand
					int queryIndex = -1;
					int j = 0;
					for (IRatingQuery q : rq.getRatingMatrix().getRatingQueries()) {
						if (q.getId().equals(rq.getId())) {
							queryIndex = j;
							break;
						}
						++j;
					}
					
					if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getRatingGroups().size() > 1 && queryIndex != -1) {
						// we want to look back one ratingGroup - since new ones are added to the front of the list we look at index=1
						preQuery = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getRatingGroups().get(1).getRatingMatrices().get(0).getRatingQueries().get(queryIndex);
					}
				}
				
			} else if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_COMP)) {
				// BY_COMP can be either:
				//			a Round List "Top Ten Performances in Round 8 of the Aviva Premiership"
				//			an overall In Form list "Top Ten In Form Players through Round 8 of the Aviva Premiership"
				//
				// they can also be comp-specific or global. If global we use the UR date and omit the comp name
				//			"Top Ten In Form Players through 5 October"   << in form
				//			"Top Ten Performances from the week of 5 October"   << round
			
				
				// is this In Form or Last Round?
				inForm = false;
				if (rq.getRatingMatrix().getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form Players ";
					inForm = true;
				} else {
					title += "Performances ";
				}
				
				// the round we are talking about is the UR of the Rating Group
				UniversalRound ur = rq.getRatingMatrix().getRatingGroup().getUniversalRound();
				
				if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getCompIds().size() > 1) {
					//global - need to go by UR				
					if (inForm) {
						title += "Through " + ur.longDesc;
					} else {
						title += "From The " + ur.longDesc;
					}
				} else {
					// comp specific
					Long compId = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getCompIds().get(0);
					ICompetition comp = cf.get(compId);
					
					for (IRound r : comp.getRounds()) {
						UniversalRound urs = urf.get(r);
						if (urs.ordinal == ur.ordinal) {
							title += "from " + r.getName() + " of the " + comp.getShortName();
							context = r.getName();
							break;
						}
					}
				}
				
				
				
			}
			

			
			Long sponsorId = null;
			if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getSponsorId() != null) {
				sponsorId = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getSponsorId();
			} else if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp() != null && rq.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp().getSponsorId() != null) {
				sponsorId = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getHostComp().getSponsorId();
			}
			
			TopTenSeedData data = new TopTenSeedData(rq.getId(), title, "", rq.getRatingMatrix().getRatingGroup().getRatingSeries().getHostCompId(), rq.getRoundIds(), 10, sponsorId);
			data.setContext(context);
			ITopTenList ttl = ttlf.create(data, preQuery);
			
			
			ttlf.put(ttl);
			
			// if this is a match list, link the ttl guid to the match so we can support the result panel in the UI
			if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_MATCH) && match != null) {
				match.setGuid(ttl.getGuid());
				mgf.put(match);
			}
			
			return new ImmediateValue<Boolean>(procked.getStatus().equals(Status.COMPLETE));
		}
		
		return new ImmediateValue<Boolean>(false);

	}

	

}
