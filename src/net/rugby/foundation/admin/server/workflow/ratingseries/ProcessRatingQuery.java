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
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.UniversalRound;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class ProcessRatingQuery extends Job2<ProcessRatingQueryResult, Long, Long> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRatingQueryFactory rqf;
	transient private IMatchRatingEngineSchemaFactory mresf;
	transient private IQueryRatingEngineFactory qref;
	transient private IPlayerRatingFactory prf;
	transient private ITopTenListFactory ttlf;
	transient private IRoundFactory rf;
	transient private IUniversalRoundFactory urf;
	transient private IMatchGroupFactory mgf;
	transient private ICompetitionFactory cf;
	transient private ITeamGroupFactory tgf;
	transient private IRatingGroupFactory rgf;
	
	private IRatingSeriesFactory rsf;


	public ProcessRatingQuery() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<ProcessRatingQueryResult> run(Long rqid, Long rsid) {

		IMatchGroup match = null;  // to support delayed guid linking
		IRatingQuery preQuery = null; // for tracking player movement
		
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.prf = injector.getInstance(IPlayerRatingFactory.class);
		this.rqf = injector.getInstance(IRatingQueryFactory.class);
		this.mresf = injector.getInstance(IMatchRatingEngineSchemaFactory.class);
		this.qref = injector.getInstance(IQueryRatingEngineFactory.class);
		this.ttlf = injector.getInstance(ITopTenListFactory.class);
		this.rsf = injector.getInstance(IRatingSeriesFactory.class);
		this.rgf = injector.getInstance(IRatingGroupFactory.class);
		
		ProcessRatingQueryResult retval = new ProcessRatingQueryResult();
		
		
		IRatingSeries rs = rsf.get(rsid);
		
		// traverse to find the rq. don't trust the one in memcache you get from rqf.get()

		IRatingGroup rg = null;
		IRatingMatrix rm = null;
		IRatingQuery rq = null;
		
		boolean found = false;
		for (Long rgId : rs.getRatingGroupIds()) {
			IRatingGroup g = rgf.get(rgId);
			for (IRatingMatrix m : g.getRatingMatrices()) {
				for (IRatingQuery q : m.getRatingQueries()) {
					if (q.getId().equals(rqid)) {
						found = true;
						rg = g;
						rm = m;
						rq = q;
						break;
					}
				}
			}
		}
		
		if (!found) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Passed in a bad RatingQuery ID (" + rqid + ") that we couldn't find in the RatingSeries.");
			retval.log.add("Passed in a bad RatingQuery ID (" + rqid + ") to ProcessRatingQuery that we couldn't find in the RatingSeries.");
			retval.success = false;
			retval.queryId = rqid;
			return immediate(retval);
		}
		
		retval.queryId = rq.getId();
		
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
			
			retval.log.add("Re-running rating query " + rq.getLabel());
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

		retval.success = false;
		boolean inForm = false;
		boolean bestYear = false;
		boolean impact = false;
		
		try {
			retval.success = mre.setQuery(rq);

			if (retval.success) {
				mre.generate(mres,rq.getScaleStanding(),rq.getScaleComp(),rq.getScaleTime(), rq.getScaleMinutesPlayed(), false);
				retval.log.add("Running rating query " + rq.getLabel());
				//rq.Status is set by the engine
				if (rq.getStatus() == Status.COMPLETE) {
					retval.success = true;
				} else {
					retval.success = false; // might have ERROR
				}
			} else {
				// stats aren't ready
				rq.setStatus(Status.NEW);
				rqf.put(rq);
				retval.log.add("Attempt to run rating query " + rq.getLabel() + " before stats are ready.");
			}
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem generating ratings for " + rq.getLabel(), ex);
			rq.setStatus(Status.ERROR);
			rqf.put(rq);
			retval.success = false;
			retval.log.add("Running rating query " + rq.getLabel() + " had errors. See server logs.");
		}
		
		if (retval.success) {
			
			
			
			this.rf = injector.getInstance(IRoundFactory.class);
			this.urf = injector.getInstance(IUniversalRoundFactory.class);
			this.mgf = injector.getInstance(IMatchGroupFactory.class);
			this.cf = injector.getInstance(ICompetitionFactory.class);
			this.tgf = injector.getInstance(ITeamGroupFactory.class);
			
			// the engine will set the status to complete if successful
			//IRatingQuery procked = rqf.get(rq.getId());

			Long sponsorId = null;
					
			// now create the TTL
			String title = "Top Ten ";
			String context = "";
			
			if (rm == null && rq.getRatingMatrixId() != null) {
				rq = rqf.buildUplinksForQuery(rq);
			}
			
			if (rs.getMode().equals(RatingMode.BY_MATCH)) {
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
						retval.success = false;
						retval.log.add("Could not find match in setting title for RQ " + rq.getId() + " teamIds " + rq.getTeamIds().toString());
					}
				}
			} else if (rs.getMode().equals(RatingMode.BY_POSITION)) {
				// is this In Form or Last Round?
				if (rm.getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form ";
					inForm = true;
				} else if (rm.getCriteria().equals(Criteria.BEST_YEAR)) {
					bestYear = true;
				} else if (rm.getCriteria().equals(Criteria.AVERAGE_IMPACT)) {
					title += "Impact ";
					impact = true;
				}
				
				title += rq.getPositions().get(0).getPlural();
				
				Long hostId = rs.getHostCompId();
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
				
				if (inForm || bestYear || impact) {
					// figure out the last list for this position
					//rm.getRatingQueries().indexOf(rq); // << This breaks on rerun, need to code by hand
					int queryIndex = -1;
					int j = 0;
					for (IRatingQuery q : rm.getRatingQueries()) {
						if (q.getId().equals(rq.getId())) {
							queryIndex = j;
							break;
						}
						++j;
					}
					
					if (rs.getRatingGroupIds().size() > 1 && queryIndex != -1) {
						// we want to look back one ratingGroup - since new ones are added to the front of the list we look at index=1
						Criteria criteria = rm.getCriteria();
						IRatingGroup backOneGroup = rgf.get(rs.getRatingGroupIds().get(1));
						for (IRatingMatrix m: backOneGroup.getRatingMatrices()) {
							if (rm.getCriteria().equals(criteria)) {
								preQuery = m.getRatingQueries().get(queryIndex);
								break;
							}
						}
					}
				}
				
			} else if (rs.getMode().equals(RatingMode.BY_TEAM)) {
				assert(rq.getTeamIds().size() == 1);
				
				ITeamGroup team = tgf.get(rq.getTeamIds().get(0));
				sponsorId = team.getSponsorId();
				
				// is this In Form, Best or Impact?
				if (rm.getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form ";
					inForm = true;
				} else if (rm.getCriteria().equals(Criteria.BEST_YEAR)) {
					bestYear = true;
				} else if (rm.getCriteria().equals(Criteria.AVERAGE_IMPACT)) {
					title += "Impact ";
					impact = true;
				}
				
				title += "Players for " + team.getDisplayName();
				
				if (rq.getCompIds().size() < 2) {
					// only include the name of the host comp if it is not virtual
					Long hostId = rs.getHostCompId();
					if (hostId != null) {
						ICompetition hostComp = cf.get(hostId);
						if (hostComp.getTTLTitleDesc() != null && !hostComp.getTTLTitleDesc().isEmpty()) {
							title += " in " + hostComp.getTTLTitleDesc();
						}
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
				
				if (inForm || bestYear || impact) {
					// figure out the last list for this position
					//rm.getRatingQueries().indexOf(rq); // << This breaks on rerun, need to code by hand
					int queryIndex = -1;
					int j = 0;
					for (IRatingQuery q : rm.getRatingQueries()) {
						if (q.getId().equals(rq.getId())) {
							queryIndex = j;
							break;
						}
						++j;
					}
					
					if (rs.getRatingGroupIds().size() > 1 && queryIndex != -1) {
						// we want to look back one ratingGroup - since new ones are added to the front of the list we look at index=1
						Criteria criteria = rm.getCriteria();
						IRatingGroup backOneGroup = rgf.get(rs.getRatingGroupIds().get(1));
						for (IRatingMatrix m: backOneGroup.getRatingMatrices()) {
							if (rm.getCriteria().equals(criteria)) {
								preQuery = m.getRatingQueries().get(queryIndex);
								break;
							}
						}
					}
				}
				
			} else if (rs.getMode().equals(RatingMode.BY_COMP)) {
				// BY_COMP can be either:
				//			a Round List "Top Ten Performances in Round 8 of the Aviva Premiership"
				//			an overall In Form list "Top Ten In Form Players through Round 8 of the Aviva Premiership"
				//
				// they can also be comp-specific or global. If global we use the UR date and omit the comp name
				//			"Top Ten In Form Players through 5 October"   << in form
				//			"Top Ten Performances from the week of 5 October"   << round
			
				
				// is this In Form or Last Round?
				inForm = false;
				if (rm.getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form Players ";
					inForm = true;
				} else {
					title += "Performances ";
				}
				
				// the round we are talking about is the UR of the Rating Group
				UniversalRound ur = rg.getUniversalRound();
				
				if (rs.getCompIds().size() > 1) {
					//global - need to go by UR				
					if (inForm) {
						title += "Through " + ur.longDesc;
					} else {
						title += "From The " + ur.longDesc;
					}
				} else {
					// comp specific
					Long compId = rs.getCompIds().get(0);
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
			

			if (sponsorId == null) {
				sponsorId = getSponsorId(rs, rq); 
			}
			
			
			TopTenSeedData data = new TopTenSeedData(rq.getId(), title, "", rs.getHostCompId(), rq.getRoundIds(), 10, sponsorId);
			data.setContext(context);
			ITopTenList ttl = ttlf.create(data, preQuery);
			
			
			ttlf.put(ttl);
			
			// if this is a match list, link the ttl guid to the match so we can support the result panel in the UI
			if (rs.getMode().equals(RatingMode.BY_MATCH) && match != null) {
				match.setGuid(ttl.getGuid());
				mgf.put(match);
			}
			
			retval.log.add("Created top ten list " + ttl.getTitle());
			
			
		}
		
		return immediate(retval);

	}

	private Long getSponsorId(IRatingSeries rs, IRatingQuery rq) {
		try {
		Long sponsorId = null;
		if (rs.getSponsorId() != null) {
			sponsorId = rs.getSponsorId();
		} else if (rs.getHostComp() != null && rs.getHostComp().getSponsorId() != null) {
			sponsorId = rs.getHostComp().getSponsorId();
		}
		
		return sponsorId;
		} catch (Throwable ex) {
			String queryName = "UNKNOWN";
			if (rq != null && rq.getLabel() != null)  {
				queryName = rq.getLabel();
			}
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Problem getting sponsor for rating query " + queryName, ex);
			return null;
		}
	}

	

}
