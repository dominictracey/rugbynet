/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.transaction.NotSupportedException;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.AdminEmailer;
import net.rugby.foundation.admin.server.orchestration.OrchestrationCore;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IClubhouseMembershipFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.ILeaderboard;
import net.rugby.foundation.game1.shared.ILeaderboardRow;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouseMembership;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchGroup.Status;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ISimpleScoreMatchResult;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class OrchestrationCreateLeaderboard extends OrchestrationCore<IClubhouseLeagueMap> {

	private ILeaderboardFactory lbf;
	private IEntryFactory ef;
	private ILeaderboard lb;
	private IAppUserFactory auf;
	private ICompetitionFactory cf;
	private ILeagueFactory lf;

	private ILeague league = null;
	private IClubhouseMembershipFactory chmf;
	private ICompetition comp;
	private ILeaderboardRowFactory lbrf;

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		return builder.param("id",target.getId().toString());
	}

	//@Inject
	public OrchestrationCreateLeaderboard(ILeagueFactory lf, ILeaderboardFactory lbf, IEntryFactory ef, IAppUserFactory auf, 
			ICompetitionFactory cf, IClubhouseMembershipFactory chmf, ILeaderboardRowFactory lbrf) {
		this.lbf = lbf;
		this.ef = ef;
		this.auf = auf;
		this.cf = cf;
		this.lf = lf;
		this.chmf = chmf;
		this.lbrf = lbrf;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.shared.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		try {
			// if this is the competition clubhouse, we need to make sure the users that have created
			// entries for this comp are members of the CC
			checkCompClubhouseLeague();


			//first update the League entries
			if (updateLeague()) {

				//does this league have entries?
				if (league.getEntryIds().size() == 0)
					return;

				//do we already have a leaderboard we want to replace?

				lb = lbf.getNew();  // gives back empty leaderboard object

				//league is a ILeague
				lb.setLeague(league);
				lb.setLeagueId(league.getId());


				lb.setCompId(target.getCompId());

				if (comp == null) {
					cf.setId(target.getCompId());
					comp = cf.getCompetition();
				}

				lb.setComp(comp);
				lb.setRound(lb.getComp().getPrevRound());
				lb.setRoundId(lb.getRound().getId());

				for (IRound r : lb.getComp().getRounds()) {
					lb.getRoundNames().add(r.getAbbr());
				}


				// score all the latest entries
				for (IEntry e : league.getEntryMap().values()) {
					ILeaderboardRow lbr = lbrf.getNew();
					lbr.setAppUserId(e.getOwnerId());
					auf.setId(e.getOwnerId());
					lbr.setAppUserName(auf.get().getNickname());

					lbr.setEntryId(e.getId());
					lbr.setEntryName(e.getName());

					lbr.setTieBreakerFactor(null);

					// regenerate scores
					Integer total = 0;
					for (IRound r: lb.getComp().getRounds()) {
						IRoundEntry re = e.getRoundEntries().get(r.getId());

						if (re != null) {
							Integer score = scoreEntry(re, r);
							lbr.getScores().add(score);
							lbr.setTieBreakerFactor(tieBreakerFactor(re,r));  // I think this means the last one sticks
							total += score;
						} else {
							lbr.getScores().add(null);
							if (r.equals(lb.getComp().getPrevRound()))
								lbr.setTieBreakerFactor(-100);
						}

						// no need to look at rounds that haven't been played yet
						if (r.equals(lb.getComp().getPrevRound()))
							break;
					}

					lbr.setTotal(total);

					//save entry score
					// DO NOT DO THIS - IT IS VERY BAD. ENDS UP DELETING PEOPLE'S PICKS!
					// THE MATCHES ARE LOCKED AND THE MATCHENTRY PUT WILL FAIL!!
					//			e.setScore(total);
					//			ef.put(e);

					lb.getRows().add(lbr);

				}

				//sort the leaderboard rows by total
				Comparator<ILeaderboardRow> comparator = Collections.reverseOrder();
				Collections.sort(lb.getRows(), comparator);
				Integer rank = 1;
				for (ILeaderboardRow lbr : lb.getRows()) {
					lbr.setRank(rank);
					rank++;
				}

				lbf.put(lb);
				Logger.getLogger(OrchestrationCreateLeaderboard.class.getName()).log(Level.WARNING,"Created leaderboard for CLM " + target.getId());

			}
			//now delete the one we are replacing
		} catch (Throwable caught) {
			String subj = "ERROR! Create Leaderboard exception: ";
			if (target != null) {
				subj += "(" + target.getId() + ")";
			}
			AdminEmailer ae = new AdminEmailer();
			ae.setSubject(subj);
			ae.setMessage(caught.getMessage());
			//ae.send();		
			Logger.getLogger(OrchestrationCreateLeaderboard.class.getName()).log(Level.SEVERE,"execute " + caught.getLocalizedMessage());
			System.out.println("Error in " + OrchestrationCreateLeaderboard.class.getName() + "execute " + caught.getLocalizedMessage());
		}

	}

	/**
	 * 
	 */
	private void checkCompClubhouseLeague() {
		try {
			if (comp == null) {
				cf.setId(target.getCompId());
				comp = cf.getCompetition();
			}

			// if this is the comp clubhouse, make sure that our membership is current
			if (target.getClubhouseId().equals(comp.getCompClubhouseId())) {
				Date now = new Date();
				// find the users with entries for this comp and add them if necessary
				//@REX I think we could call this with comp.getPrevRound() and be ok since we would just be adding users that entered last round
				ef.setRoundAndComp(null, comp);  			
				List<IEntry> entries = ef.getEntries();
				for (IEntry e: entries) {
					chmf.setClubhouseAndAppUserIds(target.getClubhouseId(),e.getOwnerId());

					// get a list - it will either have 0 (if they need to be added) or 1 (if they are already there).
					List<IClubhouseMembership> list = chmf.getList();

					if (list == null || list.isEmpty()) {
						auf.setId(e.getOwnerId());
						IAppUser au = auf.get();

						chmf.setId(null);
						IClubhouseMembership newOne = chmf.get();
						newOne.setAppUserID(e.getOwnerId());
						newOne.setClubhouseID(target.getClubhouseId());
						newOne.setUserName(au.getNickname());
						newOne.setJoined(now);
						chmf.put(newOne);

					}
				}
			}	
		} catch (Throwable caught) {
			String subj = "ERROR! Create Leaderboard exception: ";
			if (target != null) {
				subj += "(" + target.getId() + ")";
			}
			AdminEmailer ae = new AdminEmailer();
			ae.setSubject(subj);
			ae.setMessage(caught.getMessage());
			ae.send();	
		}
	}

	/**
	 * Puts any entries that are supposed to be in this league in there.
	 * @return false if can't find the targeted league
	 * @param league
	 * @throws NotSupportedException 
	 */
	private Boolean updateLeague() { //throws NotSupportedException {

		if (league == null) {
			lf.setId(target.getLeagueId());		
			league = lf.get();
		}

		if (league != null) {
			// the league is associated with a clubhouse, get all the members of that clubhouse

			chmf.setClubhouseId(target.getClubhouseId());

			List<IClubhouseMembership> memberships = chmf.getList();
			league.getEntryIds().clear();
			league.getEntryMap().clear();
			if (memberships != null) {
				// for each user, get any entries that they have for the comp we are talking about 
				//	and add them to the league if they aren't already there.				
				for (IClubhouseMembership membership : memberships) {
					ef.setUserIdAndCompId(membership.getAppUserID(), target.getCompId());
					List<IEntry> entries = ef.getEntries();
					// for each entry, add them to the league
					for (IEntry e: entries) {
						if (!league.getEntryIds().contains(e)) {
							league.addEntry(e);
						}
					}
				}
				lf.put(league);
			}
			return true;
		} else {
			return false;
		}
	}

	private Integer scoreEntry(IRoundEntry re, IRound r) {
		try {
			Integer score = 0;
			for (IMatchGroup mg : r.getMatches()) {
				if (re.getMatchPickMap().get(mg.getId()) != null) {
					if (mg.getStatus() == Status.FINAL_HOME_WIN || mg.getStatus() == Status.FINAL_HOME_WIN_OT) {
						if (re.getMatchPickMap().get(mg.getId()).getTeamPickedId().equals(mg.getHomeTeamId())) {
							score++;
						}
					} else if (mg.getStatus() == Status.FINAL_VISITOR_WIN || mg.getStatus() == Status.FINAL_VISITOR_WIN_OT) {
						if (re.getMatchPickMap().get(mg.getId()).getTeamPickedId().equals(mg.getVisitingTeamId())) {
							score++;
						}
					} 
				}
			}

			return score;
		} catch (Throwable caught) {	
			Logger.getLogger(OrchestrationCreateLeaderboard.class.getName()).log(Level.SEVERE,"scoreEntry " + caught.getLocalizedMessage());
			return 0;
		}

	}

	/*
	 * algorithm 	- get correct team in TB match + 50 pts.
	 * 				- get exact correct team score + 25 pts.
	 * 				- difference from actual score -1 pt.
	 * 
	 * So maximum is 100 pts
	 */
	private Integer tieBreakerFactor(IRoundEntry re, IRound r) {
		int tbf = 0;

		try {
			IMatchGroup mg = re.getTieBreakerMatch();
			ISimpleScoreMatchResult mr = mg.getSimpleScoreMatchResult();
			if (re.getMatchPickMap().get(mg.getId()) != null) {
				if (mg.getStatus() == Status.FINAL_HOME_WIN || mg.getStatus() == Status.FINAL_HOME_WIN_OT) {
					if (re.getMatchPickMap().get(mg.getId()).getTeamPickedId().equals(mg.getHomeTeamId())) {
						tbf += 50;
						if (re.getTieBreakerHomeScore() != null && re.getTieBreakerVisitScore() != null) {
							if (re.getTieBreakerHomeScore().equals(mr.getHomeScore()))
								tbf += 25;
							else
								tbf -= Math.abs(re.getTieBreakerHomeScore() - mr.getHomeScore());

							if (re.getTieBreakerVisitScore().equals(mr.getVisitScore()))
								tbf += 25;
							else
								tbf -= Math.abs(re.getTieBreakerVisitScore() - mr.getVisitScore());
						} else {
							tbf -= 25; // they didn't specify both scores
						}
					} else  {
						// if they didn't pick right they get -50 points
						tbf = -50;
					}
				} else if (mg.getStatus() == Status.FINAL_VISITOR_WIN || mg.getStatus() == Status.FINAL_VISITOR_WIN_OT) {
					if (re.getMatchPickMap().get(mg.getId()).getTeamPickedId().equals(mg.getVisitingTeamId())) {
						tbf += 50;
						if (re.getTieBreakerHomeScore() != null && re.getTieBreakerVisitScore() != null) {
							if (re.getTieBreakerHomeScore().equals(mr.getHomeScore()))
								tbf += 25;
							else
								tbf -= Math.abs(re.getTieBreakerHomeScore() - mr.getHomeScore());
							if (re.getTieBreakerVisitScore().equals( mr.getVisitScore()))
								tbf += 25;
							else
								tbf -= Math.abs(re.getTieBreakerVisitScore() - mr.getVisitScore());
						} else {
							tbf -= 35; // they didn't specify both scores
						}
					} else  {
						// if they didn't pick right they get -50 points
						tbf = -50;
					}
				}
			} 		else { // they didn't make a pick on last match
				tbf = -70;
			}



			return tbf;
		} catch (Throwable caught) {	
			Logger.getLogger(OrchestrationCreateLeaderboard.class.getName()).log(Level.SEVERE,"tieBreakerFactor " + caught.getLocalizedMessage());
			return -100;
		}
	}


	public ILeaderboard getLeaderboard()
	{
		return lb;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		// don't need

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		// don't need
		return null;
	}
}
