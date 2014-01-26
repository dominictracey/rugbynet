package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Value;
import com.google.inject.Injector;

public class CreateMatchRatings extends Job5<List<IPlayerMatchRating>, IMatchGroup, List<IPlayerMatchStats>, List<IPlayerMatchStats>, ITeamMatchStats, ITeamMatchStats> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5676927821878573678L;
	private transient IMatchRatingEngineFactory mref;
	private transient IMatchRatingEngineSchemaFactory mresf;


	public CreateMatchRatings() {


	}

	
	
	
	public Value<List<IPlayerMatchRating>> run(IMatchGroup match, List<IPlayerMatchStats> homePlayerStats, List<IPlayerMatchStats> visitorPlayerStats,
			ITeamMatchStats hStats,	ITeamMatchStats vStats ) {
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();
		//this.pf = injector.getInstance(IPlayerFactory.class);
		this.mref = injector.getInstance(IMatchRatingEngineFactory.class);
		this.mresf = injector.getInstance(IMatchRatingEngineSchemaFactory.class);
		
		IRatingEngineSchema mres = mresf.getDefault();
		assert (mres != null);
		IMatchRatingEngine mre = mref.get(mres);
		assert (mre != null);
		
		mre.setPlayerStats(homePlayerStats, visitorPlayerStats);
		mre.setTeamStats(hStats, vStats);		
		
		return immediate(mre.generate(mres, match));
	}
	
	public Value<IPlayerMatchStats> handleFailure(Throwable e) {
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Exception thrown creating match ratings: " + e.getLocalizedMessage());
//		PromisedValue<IPlayerMatchStats> x = newPromise(IPlayerMatchStats.class);
//		IAdminTask task = atf.getNewEditPlayerMatchStatsTask("Problem getting player match stats: for " + player.getDisplayName() + " in match " + match.getDisplayName() + " in slot " + slot, e.getLocalizedMessage(), player, match, hov, slot, fetcher.getStats(), true, getPipelineKey().getName(), getJobKey().getName(), x.getHandle());		
//		atf.put(task);
		return immediate(null);
	}

}
