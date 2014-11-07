package net.rugby.foundation.admin.server.workflow.ratingseries;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.BPMServletContextListener;
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


	public ProcessRatingQuery() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	@Override
	public Value<Boolean> run(IRatingQuery rq) {

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
		
		// first see if we are re-running, in which case clear out any ratings already in place
		if (!rq.getStatus().equals(Status.NEW)) {
			prf.deleteForQuery(rq);
		}

		rq.setStatus(Status.RUNNING);
		rqf.put(rq);


		// get the engine
		IRatingEngineSchema mres = mresf.getDefault();
		assert (mres != null);
		IQueryRatingEngine mre = qref.get(mres, rq);
		assert (mre != null);

		boolean ok = false;
		
		try {
			ok = mre.setQuery(rq);
			if (ok) {
				mre.generate(mres,rq.getScaleStanding(),rq.getScaleComp(),rq.getScaleTime());
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
			if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_MATCH)) {
				title += "Players from ";
				IRound r = rf.get(rq.getRoundIds().get(0));
				IMatchGroup match = null;
				if (r != null) {
					for (IMatchGroup m : r.getMatches()) {
						if (rq.getTeamIds().contains(m.getHomeTeamId()) && rq.getTeamIds().contains(m.getVisitingTeamId())) {
							match = m;
							break;
						}
					}
					if (match != null) {
						title +=  match.getHomeTeam().getShortName() + "v" + match.getVisitingTeam().getShortName();
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Could not find match in setting title for RQ " + rq.getId() + " teamIds " + rq.getTeamIds().toString());
					}
				}
			} else if (rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode().equals(RatingMode.BY_POSITION)) {
				// is this In Form or Last Round?
				boolean inForm = false;
				if (rq.getRatingMatrix().getCriteria().equals(Criteria.IN_FORM)) {
					title += "In Form ";
					inForm = true;
				}
				
				title += rq.getPositions().get(0).getPlural();
				
				if (inForm) {
					title += "Through ";
				} else {
					title += "From ";
				}
				
				// now we need the last round
				if (rq.getRoundIds().size() == 1) {
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
					title += last.getName();
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
				boolean inForm = false;
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
					ICompetition comp = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getComps().get(0);
					
					for (IRound r : comp.getRounds()) {
						UniversalRound urs = urf.get(r);
						if (urs.ordinal == ur.ordinal) {
							title += "From The " + ur.longDesc + " In The " + comp.getShortName();
							break;
						}
					}
				}
				
				
				
			}
			
			
			TopTenSeedData data = new TopTenSeedData(rq.getId(), title, "", null, rq.getRoundIds(), 10);
			ITopTenList ttl = ttlf.create(data);
			ttlf.put(ttl);
			
			return new ImmediateValue<Boolean>(procked.getStatus().equals(Status.COMPLETE));
		}
		
		return new ImmediateValue<Boolean>(false);

	}

}
