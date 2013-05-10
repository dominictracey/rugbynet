package net.rugby.foundation.admin.server.workflow.matchrating;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.IPlayerMatchRating;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.ITeamMatchStats;

import com.google.appengine.tools.pipeline.FutureList;
import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job1;
import com.google.appengine.tools.pipeline.Value;

//@Singleton
public class GenerateMatchRatings extends Job1<List<IPlayerMatchRating>, IMatchGroup> implements Serializable {

	private static final long serialVersionUID = 483113213168220162L;

	public enum Home_or_Visitor { HOME, VISITOR }


	public GenerateMatchRatings() {
		//Logger.getLogger(this.getClass().getCanonicalName()).setLevel(Level.FINE);
	}



	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<List<IPlayerMatchRating>> run(IMatchGroup match) {

		if (match.getForeignUrl() == null) {
			// need to get score and find match details url before we do this
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Need to get scores and populate match URL before trying to get match stats for match " + match.getDisplayName());
			return null;
		}
		
		String url = match.getForeignUrl()+"?view=scorecard";

		//		// get the comp from the match
		//		rf.setId(match.getRoundId());
		//		IRound r = rf.getRound();
		//		cf.setId(r.getCompId());
		//		comp = cf.getCompetition();

		Logger.getLogger("FetchedPlayer").log(Level.INFO,"Starting generate match ratings for url " + url);

		//	    FutureValue<ITeamGroup> homeTeam = futureCall(new FetchTeamFromScrumReport(tf), immediate(Home_or_Visitor.HOME), immediate(url));
		//	    FutureValue<ITeamGroup> visitTeam = futureCall(new FetchTeamFromScrumReport(tf), immediate(Home_or_Visitor.VISITOR), immediate(url));

		FutureValue<ITeamMatchStats> homeTeamStats = futureCall(new FetchTeamMatchStats(), immediate(match.getHomeTeam()), immediate(match), immediate(Home_or_Visitor.HOME), immediate(url));
		FutureValue<ITeamMatchStats> visitorTeamStats = futureCall(new FetchTeamMatchStats(), immediate(match.getVisitingTeam()), immediate(match), immediate(Home_or_Visitor.VISITOR), immediate(url));

		List<Long> homeIds = getIds(Home_or_Visitor.HOME, url);
		List<Long> visitIds = getIds(Home_or_Visitor.VISITOR, url);

		List<FutureValue<IPlayer>> homePlayers = new ArrayList<FutureValue<IPlayer>>();
		List<FutureValue<IPlayer>> visitorPlayers = new ArrayList<FutureValue<IPlayer>>();

		int count = 0;
		for (Long id : homeIds) {
			FutureValue<IPlayer> homePlayer = futureCall(new FetchPlayerByScrumId(), immediate((ICompetition)null), immediate("home player " + count++),  immediate(url), immediate(id), immediate(1L));
			homePlayers.add(homePlayer);
		}

		count = 0;
		for (Long id : visitIds) {
			FutureValue<IPlayer> visitPlayer = futureCall(new FetchPlayerByScrumId(), immediate((ICompetition)null), immediate("visit player " + count++),  immediate(url), immediate(id), immediate(1L));
			visitorPlayers.add(visitPlayer);
		}	   

		List<FutureValue<IPlayerMatchStats>> homePlayerMatchStats = new ArrayList<FutureValue<IPlayerMatchStats>>();
		List<FutureValue<IPlayerMatchStats>> visitorPlayerMatchStats = new ArrayList<FutureValue<IPlayerMatchStats>>();

		count = 0;
		for (FutureValue<IPlayer> fp : homePlayers) {
			FutureValue<IPlayerMatchStats> stats = futureCall(new FetchPlayerMatchStats(), fp, immediate(match), immediate(Home_or_Visitor.HOME), immediate(count++), immediate(url));
			homePlayerMatchStats.add(stats);

		}

		FutureList<IPlayerMatchStats> hpms = new FutureList<IPlayerMatchStats>(homePlayerMatchStats);

		count = 0;
		for (FutureValue<IPlayer> fp : visitorPlayers) {
			FutureValue<IPlayerMatchStats> stats = futureCall(new FetchPlayerMatchStats(), fp, immediate(match), immediate(Home_or_Visitor.VISITOR), immediate(count++), immediate(url));
			visitorPlayerMatchStats.add(stats);

		}
		FutureList<IPlayerMatchStats> vpms = new FutureList<IPlayerMatchStats>(visitorPlayerMatchStats);

		//  assert(visitorPlayerMatchStats.size() == MAX_ROSTER);

		// now we can invoke the engine
		FutureValue<List<IPlayerMatchRating>> ratings = futureCall(new CreateMatchRatings(), immediate(match), hpms, vpms, homeTeamStats, visitorTeamStats);

		
		
		return ratings;

	}


	private List<Long> getIds(Home_or_Visitor home, String url) {
		boolean found = false;
		boolean isVisitor = false;

		if (home == Home_or_Visitor.VISITOR) {
			isVisitor = true;
		}

		List<Long> ids = new ArrayList<Long>();
		UrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;


		if (lines == null) {
			return null;
		}

		Iterator<String> it = lines.iterator();
		while (it.hasNext() && !found) {

			line = it.next();
			// first we scan to the right date
			if (line.contains("<h2>Teams")) {

				//skip down to players
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to teams...");

//				boolean foundTries = false;
//				boolean foundCons = false;
//				boolean foundPens = false;
//				boolean foundDrops = false;
//				// tries
//				
//				for (int i=0; i<10; ++i) {
//					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
//					line = it.next(); 
//					if (line.contains("Tries")) {
//						foundTries = true;
//					} else if (line.contains("Cons")) {
//						foundCons = true;
//					} else if (line.contains("Pens")) {
//						foundPens = true;
//					} else if (line.contains("Drops")) {
//						foundDrops = true;
//					}
//				}
//				
//				// cons
//				if (!foundCons !line.contains("<tr>")) {
//					for (int i=0; i<10; ++i) {
//						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
//						line = it.next(); 
//						if (line.contains("Tries")) {
//							foundTries = true;
//						} else if (line.contains("Cons")) {
//							foundCons = true;
//						} else if (line.contains("Pens")) {
//							foundPens = true;
//						} else if (line.contains("Drops")) {
//							foundDrops = true;
//						}
//					}
//				}
//				
//				// pens
//				if (!foundPens && !line.contains("<tr>")) {
//					for (int i=0; i<10; ++i) {
//						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
//						line = it.next(); 
//						if (line.contains("Tries")) {
//							foundTries = true;
//						} else if (line.contains("Cons")) {
//							foundCons = true;
//						} else if (line.contains("Pens")) {
//							foundPens = true;
//						} else if (line.contains("Drops")) {
//							foundDrops = true;
//						}
//					}
//				}
//				
//				// drops
//				if (!line.contains("<tr>")) {
//					for (int i=0; i<10; ++i) {
//						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
//						line = it.next(); 
//					}
//				}
				
				// there can be a bunch of optional 10-line sections here for tries, cons, pens and drops
				// easiest to just look for the top of the player section
				while (it.hasNext() && !line.contains("<tr>")) {
					line = it.next();
				}
				
				if (!it.hasNext()) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Couldn't find top of the player section in teams tab when skipping through the summary headers (tries, cons, pens, drops)");
					return null;
				}
				
				// divTeams
				for (int i=0; i<8; ++i) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					line = it.next(); 
				}
				
				//line = it.next(); // tbody

				// get 15 home starters
				for (int i=0; i<15; ++i) {
					Long id = getId(it);
					if (!isVisitor)
						ids.add(id);
				}

				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to home subs...");
				// skip to subs
				for (int i=0; i<7; ++i) {  
					line = it.next();
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
				}

				// get 6 or 7 home subs
				Long id = getId(it);
				while (id != null) {
					if (!isVisitor)
						ids.add(id);
					id = getId(it);
				}

				if (!isVisitor) {
					found = true;
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to end of home div...");
					//skip to end of home div
					//for (int i=0; i<2; ++i) {
					while (it.hasNext() && !line.contains("</td")) {
						line = it.next(); 
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// sitting on home's closing </td>
					assert(line.contains("</td>"));
					//skip down to visiting team players
					//for (int i=0; i<8; ++i) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to visiting team...");
					while (it.hasNext() && !line.contains("</tr>")) {
						line = it.next(); 
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// get 15 visiting team players
					for (int i=0; i<15; ++i) {
						id = getId(it);
						if (isVisitor && id != null)
							ids.add(id);
					}

					// skip to subs
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Skipping down to visiting subs...");
					for (int i=0; i<7; ++i) {  
						line = it.next();
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);
					}

					// get 7 visitor subs
					id = getId(it);
					while (id != null) {
						if (isVisitor)
							ids.add(id);
						id = getId(it);
					}

					found = true;

				}
			}
		}

		if (found) {
			return ids;
		} else {
			return null;
		}
	}

	/*
	 * sets playerOn to be true/false depending on how they finished the match
	 */
	Long getId(Iterator<String> it) {

		String line = it.next();
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINEST,line);

		if (!line.contains("<tr")) {
			return null;  // hit the end of the substitutes
		}

		// there are 15 lines to a player section
		for (int i=0; i<6; ++i) {
			line = it.next();
		}

		//<a href="/scrum/rugby/player/14650.html" class="liveLineupTextblk" target="_top">Conrad Smith</a>
		String id = "";
		if (line.split("[/|.]").length > 4) {
			id = line.split("[/|.]")[4].trim();
		}
		
		String name = "unknown";
		if (line.split("[<|>]").length > 5) {
			name = line.split("[<|>]")[4];
		}

		//check for card
		line = it.next();  //</td>
		//line = it.next();

		if (line.contains("<td")) {
			//skip card
			for (int i=0; i<4; ++i) {
				line = it.next();
			}
		}

		// just read innermost </tr>
		for (int i=0; i<3; ++i) {
			line = it.next();
		}

		// iterator on outer </tr>
		if (id.isEmpty()) {
			return null;
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.FINE,"Found player " + name + " (" + id + ")");
			return Long.parseLong(id);
		}

	}
}
