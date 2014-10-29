package net.rugby.foundation.topten.server.utilities.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.model.shared.INote.LinkType;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;


public class TacklesNotesCreator implements INotesCreator {

//	// 1 Player version:
//	protected final String p1Template_EN = Note.PLAYER1 + " was the top tackler" + Note.DETAILS + " " + Note.CONTEXT + " " + Note.LINK;
//	// 2 Player version:
//	protected final String p2Template_EN = Note.PLAYER1 + " and " + Note.PLAYER2 + " were the top tacklers " + Note.DETAILS + " " + Note.CONTEXT + Note.LINK;
//	// 3 Player version:
//	protected final String p3Template_EN = Note.PLAYER1 + ", " + Note.PLAYER2 + " and " + Note.PLAYER3 + " were the top tacklers " + Note.DETAILS + " " + Note.CONTEXT + Note.LINK;

	private IPlayerRatingFactory prf;
	private IRatingQueryFactory rqf;
	private INoteFactory nf;
	private ITopTenListFactory ttlf;

	@Inject
	public TacklesNotesCreator(IPlayerRatingFactory prf, IRatingQueryFactory rqf, INoteFactory nf, ITopTenListFactory ttlf) {
		this.prf = prf;
		this.rqf = rqf;
		this.nf = nf;
		this.ttlf = ttlf;
	}

	@Override
	public List<INote> createNotes(IRatingQuery rq) {
		List<INote> retval = new ArrayList<INote>();

		// if it's just for the last round, check how many tackles they made in the match and report the top players
		//		IRatingQuery rq = rqf.get(ttl.getQueryId());
		//		if (rq != null && rq.getRatingMatrix() == null && rq.getRatingMatrixId() != null) {
		//			rq.setRa
		//		}
		if (rq.getRatingMatrix() != null) {
			if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {
				INote note = nf.create();

				List<IPlayerRating> prl = prf.query(rq);

				// make up a list of the match stats
				List<IPlayerMatchStats> stats = new ArrayList<IPlayerMatchStats>();

				for (IPlayerRating pr : prl) {
					assert(pr.getMatchStats().size() == 1);
					stats.add(pr.getMatchStats().get(0));

				}

				// Sort by tackles made
				Collections.sort(stats, new Comparator<IPlayerMatchStats>() {
					@Override
					public int compare(IPlayerMatchStats o1, IPlayerMatchStats o2) {
						return Integer.compare(o2.getTacklesMade(), o1.getTacklesMade());
					}
				});

				int tacklesMade = stats.get(0).getTacklesMade();
				note.setDetails("(" + tacklesMade + ")");
				note.setSignificance(getImportance(tacklesMade));
				note.setRound(rq.getRatingMatrix().getRatingGroup().getUniversalRoundOrdinal());

				// output top tacklers
				// player1, player2 and player3 the top tacklers (Optional #) in [Context]
				List<Long> playerIds = new ArrayList<Long>();
				int i = 0;
				while (stats.get(i).getTacklesMade().equals(tacklesMade)) {
					playerIds.add(stats.get(i).getPlayerId());
					++i;
				}

				note.setPlayer1Id(playerIds.get(0));
				if (i > 1) {
					note.setPlayer2Id(playerIds.get(1));
				}

				if (i > 2) {
					note.setPlayer3Id(playerIds.get(2));
					note.setTemplateSelector("TTP3");
				} else if (i == 1) {
					note.setTemplateSelector("TTP1");
				} else {
					note.setTemplateSelector("TTP2");
				}

				ITopTenList ttl = ttlf.get(rq.getTopTenListId());
				if (ttl != null) {

					// the context is either a match or a whole round
					RatingMode mode = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode();
					if (mode.equals(RatingMode.BY_MATCH)) {

						note.setMatchId(prl.get(0).getMatchStats().get(0).getMatchId());
						note.setType(LinkType.Match);

						note.setLink(ttl.getGuid());
						note.setContextListId(ttl.getId());

						retval.add(note);
					} 
//					else if (mode == RatingMode.BY_COMP) {
//						// a round list
//						if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {
//
//							// is it a single comp?
//
//							note.setType(LinkType.Round);
//							note.setLink(ttl.getGuid());
//							note.setContextListId(ttl.getId());
//
//							retval.add(note);
//						}
//					}  else if (mode == RatingMode.BY_POSITION) {
//						// a round list
//						if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {
//
//							// is it a single comp?
//
//							note.setType(LinkType.Position);
//							note.setLink(ttl.getGuid());
//							note.setContextListId(ttl.getId());
//
//							retval.add(note);
//						}
//					}
				}
			} 
		}

		return retval;
	}



	private int getImportance(int tacklesMade) {
		int importance = 40; // 

		if (tacklesMade > 14) {
			importance = 60;
		} else if (tacklesMade > 18) {
			importance = 75;
		} else if (tacklesMade > 20) {
			importance = 85;
		}
		return importance;
	}
}