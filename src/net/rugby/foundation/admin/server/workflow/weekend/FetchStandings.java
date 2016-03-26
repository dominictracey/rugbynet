package net.rugby.foundation.admin.server.workflow.weekend;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IStandingsFetcherFactory;
import net.rugby.foundation.admin.server.factory.espnscrum.IUrlCacher;
import net.rugby.foundation.admin.server.model.IStandingsFetcher;
import net.rugby.foundation.admin.server.workflow.weekend.results.ProcessMatchResult;
import net.rugby.foundation.admin.server.workflow.weekend.results.StandingsResult;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.IStandingFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.IStanding;
import net.rugby.foundation.model.shared.ITeamGroup;
import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

//@Singleton
public class FetchStandings extends Job2<StandingsResult, Long, List<ProcessMatchResult>> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	private static Injector injector = null;

	transient private IRoundFactory rf;
	transient private IStandingsFetcherFactory sfff;
	transient private ICompetitionFactory cf;
	transient private IUrlCacher uc;
	transient private IStandingFactory sf;
	
	public FetchStandings() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}

	/**
	 * @param roundId - the id of the round you want to get standings for
	 */
	@Override
	public Value<StandingsResult> run(Long roundId, List<ProcessMatchResult> matchResults) {

		try {
			if (injector == null) {
				injector = BPMServletContextListener.getInjectorForNonServlets();
			}

			this.rf = injector.getInstance(IRoundFactory.class);
			this.cf = injector.getInstance(ICompetitionFactory.class);
			this.sfff = injector.getInstance(IStandingsFetcherFactory.class);
			this.uc = injector.getInstance(IUrlCacher.class);
			this.sf = injector.getInstance(IStandingFactory.class);
			
			IRound r = rf.get(roundId);

			StandingsResult retval = new StandingsResult();
			retval.log.add("Standings Fetcher");
			retval.roundId = roundId;
			
			if (r != null) {
				IStandingsFetcher fetcher = sfff.getFetcher(r);
				if (fetcher != null) {
					if (r.getCompId() != null) {
						ICompetition c = cf.get(r.getCompId());
						if (c != null) {
							fetcher.setComp(c);
							fetcher.setRound(r);
							fetcher.setUc(uc);
							
							// with the new espn.co.uk tables, we are seeing the new comps (as of SR 2016) 
							// not working with the old template syntax. Allow over-ride from admin.html
							if (c.getTableURL() != null && !c.getTableURL().isEmpty()) {
								fetcher.setUrl(c.getTableURL());
							} else {
								fetcher.setUrl(c.getForeignURL()+"?template=pointstable");
							}
							
							Iterator<ITeamGroup> it = c.getTeams().iterator();
							while (it.hasNext()) {
								ITeamGroup t = it.next();
								IStanding s = fetcher.getStandingForTeam(t);
								if (s != null) {
									sf.put(s);
									retval.log.add("Found standing " + s.getStanding() + " for team " + t.getDisplayName());
								} else {
									retval.log.add("No standing found for team " + t.getDisplayName());
								}
								
							}
							
						}
						retval.log.add("Bad comp");
					}
					retval.log.add("Bad compId in round");
				}
				//TODO create a FetchStandingsAdminTask
				retval.log.add("Don't have a standings fetcher for this comp type. Please complete it manually.");
			}
			return immediate(retval);
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);

			StandingsResult retval = new StandingsResult();
			retval.log.add("Exception caught in Standing Fetcher" + ex.getLocalizedMessage());
			
			return immediate(retval);
		}

	}

}
