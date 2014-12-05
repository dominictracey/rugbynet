package net.rugby.foundation.topten.server.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.utilities.notes.TacklesNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TriesScoredNotesCreator;

public class MatchNotesCreator implements INotesCreator {

	private TacklesNotesCreator tnc;
	private TriesScoredNotesCreator tsnc;
	private INoteRefFactory nrf;
	private INoteFactory nf;
	private TopTenNotesCreator ttc;
	private ITopTenListFactory ttlf;
	private IRatingSeriesFactory rsf;

	@Inject
	public MatchNotesCreator(TopTenNotesCreator ttc, TacklesNotesCreator tnc, TriesScoredNotesCreator tsnc, INoteFactory nf, INoteRefFactory nrf,
			ITopTenListFactory ttlf, IRatingSeriesFactory rsf) {
		this.ttc = ttc;
		this.tsnc = tsnc;
		this.tnc = tnc;
		this.nf = nf;
		this.nrf = nrf;
		this.ttlf = ttlf;
		this.rsf = rsf;
	}

	@Override
	public List<INote> createNotes(IRatingQuery rq) {

		// first add any notes we already have for this round to the current list
		int uro = rq.getRatingMatrix().getRatingGroup().getUniversalRound().ordinal;
		ITopTenList ttl = ttlf.get(rq.getTopTenListId());
		
		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "******* Creating notes for " + ttl.getTitle());

		
		if (ttl != null) {
			List<INote> existing = nf.getByUROrdinal(uro);
			for (INote note : existing) {
				for (ITopTenItem tti : ttl.getList()) {
					if (tti.getPlayerId().equals(note.getPlayer1Id())) {
						LinkNoteToList(note,ttl,uro);
					}
				}
			}

			// Now create new notes for this list
			// tackle notes
			List<INote> retval = tnc.createNotes(rq);
			// tries scored notes
			retval.addAll(tsnc.createNotes(rq));
			// izzon notes
			retval.addAll(ttc.createNotes(rq));

			// save notes
			for (INote note : retval) {
				nf.put(note); // get an Id
			}

			// we now have to create links from lists to notes:
			//	1) Link the notes we just created to lists in this universal round (including the current list)

			// cycle through all the notes and create crosslinks to other
			for (INote note : retval) {
				for (Long compId : rq.getCompIds()) {
					Map<RatingMode,Long> modes = rsf.getModesForComp(compId);
					for (RatingMode mode : modes.keySet()) {
						IRatingSeries series = rsf.get(compId, mode);
						if (series != null ) { //&& !series.getId().equals(rq.getRatingMatrix().getRatingGroup().getRatingSeriesId())) {
							// get the right RatingGroup
							List<IRatingGroup> groups = series.getRatingGroups();
							int i = groups.size()-1;
							IRatingGroup group = groups.get(i);
							// go backwards
							while (group != null) {
								if (group.getUniversalRoundOrdinal() == uro) {
									break;
								}
								if (i == 0) {
									group = null;
								} else {
									group = groups.get(--i);
								}
							}

							if (group != null) {
								// search through the lists to see if we can create xlinks
								for (IRatingMatrix matrix : group.getRatingMatrices()) {
									for (IRatingQuery query : matrix.getRatingQueries()) {
										if (query.getTopTenListId() != null) {
											ITopTenList list = ttlf.get(query.getTopTenListId());
											for (ITopTenItem item : list.getList()) {
												if (item.getPlayerId().equals(note.getPlayer1Id())) {
													// create xlink if this isn't an izzon note for the note target
													if (!(note.getContextListId().equals(list.getId()) && note.getTemplateSelector().equals("TT")))
															LinkNoteToList(note, list, uro);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return retval;
		}
		return null; // TTL not created or invalid query
	}

	private void LinkNoteToList(INote note, ITopTenList ttl, int uro) {
		INoteRef ref = nrf.create();
		ref.setContextId(ttl.getId());
		ref.setNoteId(note.getId());
		ref.setUrId(uro);
		nrf.put(ref);
		if (note.getSignificance() > 1)
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Linking note " + note.getTemplateSelector() + " for " + note.getPlayer1Id() + " to " + ttl.getTitle());
	}
}
