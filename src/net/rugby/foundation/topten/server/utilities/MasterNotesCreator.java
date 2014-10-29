package net.rugby.foundation.topten.server.utilities;

import java.util.List;

import com.google.inject.Inject;

import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;
import net.rugby.foundation.topten.server.utilities.notes.TacklesNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TriesScoredNotesCreator;

public class MasterNotesCreator implements INotesCreator {

	private TacklesNotesCreator tnc;
	private TriesScoredNotesCreator tsnc;
	private INoteRefFactory nrf;
	private INoteFactory nf;
	private TopTenNotesCreator ttc;

	@Inject
	public MasterNotesCreator(TopTenNotesCreator ttc, TacklesNotesCreator tnc, TriesScoredNotesCreator tsnc, INoteFactory nf, INoteRefFactory nrf) {
		this.ttc = ttc;
		this.tsnc = tsnc;
		this.tnc = tnc;
		this.nf = nf;
		this.nrf = nrf;
	}

	@Override
	public List<INote> createNotes(IRatingQuery rq) {
		List<INote> retval = tnc.createNotes(rq);
		retval.addAll(tsnc.createNotes(rq));
		retval.addAll(ttc.createNotes(rq));
		
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
		
		return retval;
	}
	
	

}
