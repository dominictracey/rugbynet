package net.rugby.foundation.topten.server.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		// tackle notes
		List<INote> retval = tnc.createNotes(rq);
		// tries scored notes
		retval.addAll(tsnc.createNotes(rq));

		// link these notes to the universal round
		int uro = rq.getRatingMatrix().getRatingGroup().getUniversalRound().ordinal;
		for (INote note : retval) {
			nf.put(note); // get an Id
			INoteRef ref = nrf.create();
			ref.setContextId(rq.getTopTenListId());
			ref.setNoteId(note.getId());
			ref.setUrId(uro);
			nrf.put(ref);
		}

		// create xlinks
		// now also get for the round and see if any of the players have x-linked notes
		List<INote> xlinks = nf.getByUROrdinal(uro);
		ITopTenList ttl = ttlf.get(rq.getTopTenListId());
		List<Long> playerIds = new ArrayList<Long>();
		for (ITopTenItem item : ttl.getList()) {
			for (INote n : xlinks) {
				if (!n.getContextListId().equals(ttl.getId()) && n.getPlayer1Id().equals(item.getPlayerId())) {
					INoteRef ref = nrf.create();
					ref.setContextId(rq.getTopTenListId());
					ref.setNoteId(n.getId());
					ref.setUrId(uro);
					nrf.put(ref);
					playerIds.add(item.getPlayerId());
				}
			}
		}

		// don't link the izzon notes to the list they are about
		List<INote> izzon = ttc.createNotes(rq);
		for (INote note : izzon) {
			nf.put(note); // just save them for others to x-link to
		}
		retval.addAll(izzon);

		// we can put them on other lists though
		// cycle through all the notes and create crosslinks to other
		for (INote note : retval) {
			for (Long compId : rq.getCompIds()) {
				Map<RatingMode,Long> modes = rsf.getModesForComp(compId);
				for (RatingMode mode : modes.keySet()) {
					IRatingSeries series = rsf.get(compId, mode);
					if (series != null && !series.getId().equals(rq.getRatingMatrix().getRatingGroup().getRatingSeriesId())) {
						// get the right RatingGroup
						List<IRatingGroup> groups = series.getRatingGroups();
						int i = groups.size()-1;
						IRatingGroup group = groups.get(i);
						// go backwards
						while (group != null) {
							if (group.getUniversalRoundOrdinal() == uro) {
								break;
							}
							groups.get(++i);
						}

						if (group != null) {
							// search through the lists to see if we can create xlinks
							for (IRatingMatrix matrix : group.getRatingMatrices()) {
								for (IRatingQuery query : matrix.getRatingQueries()) {
									if (query.getTopTenListId() != null) {
										ITopTenList list = ttlf.get(query.getTopTenListId());
										for (ITopTenItem item : list.getList()) {
											if (item.getPlayerId().equals(note.getPlayer1Id())) {
												// create xlink
												INoteRef ref = nrf.create();
												ref.setContextId(query.getTopTenListId());
												ref.setNoteId(note.getId());
												ref.setUrId(uro);
												nrf.put(ref);
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
}
