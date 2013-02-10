package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IMatchRatingEngine;
import net.rugby.foundation.admin.shared.IMatchRatingEngineSchema;
import net.rugby.foundation.admin.shared.ScrumMatchRatingEngineSchema20130121;
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



	public CreateMatchRatings() {


	}

	
	
	
	public Value<List<IPlayerMatchRating>> run(IMatchGroup match, List<IPlayerMatchStats> homePlayerStats, List<IPlayerMatchStats> visitorPlayerStats,
			ITeamMatchStats hStats,	ITeamMatchStats vStats ) {
		
		Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
		Injector injector = BPMServletContextListener.getInjectorForNonServlets();
		//this.pf = injector.getInstance(IPlayerFactory.class);
		this.mref = injector.getInstance(IMatchRatingEngineFactory.class);
		//this.pmrf = injector.getInstance(IPlayerMatchRatingFactory.class);
		
		IMatchRatingEngineSchema mres = new ScrumMatchRatingEngineSchema20130121();
		IMatchRatingEngine mre = mref.get(mres);
		
		mre.setPlayerStats(homePlayerStats, visitorPlayerStats);
		mre.setTeamStats(hStats, vStats);		
		
		return immediate(mre.generate(mres, match));
	}

}
