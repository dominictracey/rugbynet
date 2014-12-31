package net.rugby.foundation.topten.server.utilities.notes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.INote.LinkType;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;


public class TriesScoredNotesCreator implements INotesCreator {

	private IPlayerRatingFactory prf;
	private IMatchGroupFactory mgf;
	private INoteFactory nf;
	private ITopTenListFactory ttlf;

	@Inject
	public TriesScoredNotesCreator(IPlayerRatingFactory prf, IMatchGroupFactory mgf, INoteFactory nf, ITopTenListFactory ttlf) {
		this.prf = prf;
		this.mgf = mgf;
		this.nf = nf;
		this.ttlf = ttlf;
	}

//	protected final String oneTryTemplate_EN = Note.PLAYER1 + " scored a try" + " " + Note.CONTEXT + " " + Note.LINK;
//	protected final String twoTriesTemplate_EN = Note.PLAYER1 + " had a brace of tries" + " " + Note.CONTEXT + " " + Note.LINK;
//	protected final String threeTriesTemplate_EN = Note.PLAYER1 + " had a hat trick" + " " + Note.CONTEXT + " " + Note.LINK;
//	protected final String moreTriesTemplate_EN = Note.PLAYER1 + " had" + Note.DETAILS + " tries" + Note.CONTEXT + " " + Note.LINK;

	@Override
	public List<INote> createNotes(IRatingQuery rq) {
		List<INote> retval = new ArrayList<INote>();
		if (rq.getRatingMatrix() != null) {
			if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {


				List<IPlayerRating> prl = prf.query(rq);
				ITopTenList ttl = ttlf.get(rq.getTopTenListId());

				if (ttl == null) return null;

				List<Long> topXplayerIds = new ArrayList<Long>();
				for (int i = 0; i <10; ++i)	{
					topXplayerIds.add(prl.get(i).getPlayerId());
				}

				// make up a list of the match stats
				List<IPlayerMatchStats> stats = new ArrayList<IPlayerMatchStats>();

				for (IPlayerRating pr : prl) {
					assert(pr.getMatchStats().size() == 1);
					stats.add(pr.getMatchStats().get(0));

				}

				// Sort by tries scored
				Collections.sort(stats, new Comparator<IPlayerMatchStats>() {
					@Override
					public int compare(IPlayerMatchStats o1, IPlayerMatchStats o2) {
						return Integer.compare(o2.getTries(), o1.getTries());
					}
				});

				int j = 0;
				while (stats.get(j).getTries() > 0) {

					// only make note if they made the top ten
					// @TODO should also allow players of interest?
					if (topXplayerIds.contains(stats.get(j).getPlayerId())) {
						INote note = nf.create();

						int tries = stats.get(j).getTries();
						note.setDetails(" " + tries);
						note.setSignificance(getImportance(tries));
						note.setRound(rq.getRatingMatrix().getRatingGroup().getUniversalRoundOrdinal());
						note.setPlayer1Id(stats.get(j).getPlayerId());
						if (tries == 1) {
							note.setTemplateSelector("1T");
						} else if (tries == 2) {
							note.setTemplateSelector("2T");
						} else if (tries == 3) {
							note.setTemplateSelector("3T");
						} else if (tries > 3) {
							note.setTemplateSelector("MT");
						}
						
						note.setTeamId(stats.get(0).getTeamId());

						// the context of scoring tries is a match
						RatingMode mode = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode();
						if (mode.equals(RatingMode.BY_MATCH)) {

							note.setMatchId(prl.get(0).getMatchStats().get(0).getMatchId());
							note.setType(LinkType.Match);

							note.setLink(ttl.getGuid());  // should probably use the TTI.getPlaceGuid() instead here.
							note.setContextListId(ttl.getId());

							retval.add(note);
						} 
						
						
//						else if (mode == RatingMode.BY_COMP) {
//							// a round list
//							if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {
//
//								// is it a single comp?
//								note.setType(LinkType.Round);
//								note.setLink(ttl.getGuid());
//								note.setContextListId(ttl.getId());
//
//								retval.add(note);
//							}
//						}  else if (mode == RatingMode.BY_POSITION) {
//							// a round list
//							if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {
//
//								// is it a single comp?
//
//								note.setType(LinkType.Position);
//								note.setLink(ttl.getGuid());
//								note.setContextListId(ttl.getId());
//
//								retval.add(note);
//							}
//						}
					}
					++j;
				}
			}
		} 

	return retval;
}

private int getImportance(int tries) {
	int importance = 40; // 

	if (tries == 1) {
		importance = 32;
	} else if (tries == 2) {
		importance = 62;
	} else if (tries > 2) {
		importance = 87;
	}
	return importance;
}
}
