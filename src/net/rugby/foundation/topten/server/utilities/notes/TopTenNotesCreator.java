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
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;


public class TopTenNotesCreator implements INotesCreator {

//	// 1 Player version:
//	protected final String p1Template_EN = Note.PLAYER1 + " was the top tackler" + Note.DETAILS + " " + Note.CONTEXT + " " + Note.LINK;
//	// 2 Player version:
//	protected final String p2Template_EN = Note.PLAYER1 + " and " + Note.PLAYER2 + " were the top tacklers " + Note.DETAILS + " " + Note.CONTEXT + Note.LINK;
//	// 3 Player version:
//	protected final String p3Template_EN = Note.PLAYER1 + ", " + Note.PLAYER2 + " and " + Note.PLAYER3 + " were the top tacklers " + Note.DETAILS + " " + Note.CONTEXT + Note.LINK;

	protected final int SIGNIFICANCE = 1;
	
	private IPlayerRatingFactory prf;
	private IRatingQueryFactory rqf;
	private INoteFactory nf;
	private ITopTenListFactory ttlf;

	@Inject
	public TopTenNotesCreator(IPlayerRatingFactory prf, IRatingQueryFactory rqf, INoteFactory nf, ITopTenListFactory ttlf) {
		this.prf = prf;
		this.rqf = rqf;
		this.nf = nf;
		this.ttlf = ttlf;
	}

	@Override
	public List<INote> createNotes(IRatingQuery rq) {
		List<INote> retval = new ArrayList<INote>();

		if (rq.getRatingMatrix() != null) {
			RatingMode mode = rq.getRatingMatrix().getRatingGroup().getRatingSeries().getMode();
			
			//if (rq.getRatingMatrix().getCriteria().equals(Criteria.ROUND)) {				
				ITopTenList ttl = ttlf.get(rq.getTopTenListId());
				if (ttl != null) {
					for (ITopTenItem i : ttl.getList()) {
						INote note = nf.create();
						
						note.setSignificance(SIGNIFICANCE);
						note.setRound(rq.getRatingMatrix().getRatingGroup().getUniversalRoundOrdinal());
						note.setPlayer1Id(i.getPlayerId());
						note.setTemplateSelector("TT");
						note.setLink(i.getPlaceGuid());  // note that we don't use the TTL GUID here
						if (mode.equals(RatingMode.BY_COMP)) {
							note.setType(LinkType.Comp);
						} else if (mode.equals(RatingMode.BY_COUNTRY)) {
							note.setType(LinkType.Country);
						} else if (mode.equals(RatingMode.BY_MATCH)) {
							note.setType(LinkType.Match);
						} else if (mode.equals(RatingMode.BY_POSITION)) {
							note.setType(LinkType.Position);
						} else if (mode.equals(RatingMode.BY_TEAM)) {
							note.setType(LinkType.Team);
						}

						note.setContextListId(ttl.getId());		
						
						retval.add(note);
					}
				}
			//}
		}


		return retval;
	}
}