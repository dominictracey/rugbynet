package net.rugby.foundation.admin.server.workflow.matchrating;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.workflow.player.ComplexJob;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchRating;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class GenerateMatchRatings extends Job1<List<IMatchRating>, String> {

	private static final long serialVersionUID = 483113213168220162L;
	//private IPlayerFactory pf;
	
	public enum Home_or_Visitor { HOME, VISITOR }
	
	//@Inject
	public GenerateMatchRatings(IPlayerFactory pf) {
		//this.pf = pf;
	}
	
	
	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<List<IMatchRating>> run(String url) {

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Starting generate match ratings for url " + url);

	    FutureValue<ITeamGroup> homeTeam = futureCall(new FetchTeamFromScrumReport(), immediate(Home_or_Visitor.HOME), immediate(url));
	    FutureValue<ITeamGroup> visitTeam = futureCall(new FetchTeamFromScrumReport(), immediate(Home_or_Visitor.VISITOR), immediate(url));
	    
	    FutureValue<List<IPlayer>> homePlayers = futureCall(new FetchPlayersForTeam(), homeTeam, immediate(url));
	    FutureValue<List<IPlayer>> visitorPlayers = futureCall(new FetchPlayersForTeam(), visitTeam, immediate(url));
	    
	    
	    
//	    PromisedValue<ITeamGroup> visitTeam = newPromise(ITeamGroup.class);
//	    PromisedValue<Integer> z = newPromise(Integer.class);
//	    ComplexJob cj = new ComplexJob();
//	    FutureValue<Integer> intermediate = futureCall(cj, x, y, z);
	    
		return immediate(null);

	}
	
}
