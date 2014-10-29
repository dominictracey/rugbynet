package net.rugby.foundation.topten.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.model.shared.NoteRef;
import net.rugby.foundation.topten.server.factory.BaseNoteFactory;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;


public class OfyNoteFactory extends BaseNoteFactory implements INoteFactory {


	@Inject
	public OfyNoteFactory(INoteRefFactory nrf) {
		super(nrf);	
	}
	
	@Override
	public INote create() {
		try {
			return new Note();
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected INote getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			return ofy.get(Note.class, id);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getFromPersistentDatastore" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected INote putToPersistentDatastore(INote t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			return (INote) ofy.put(t);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "putToPersistentDatastore" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(INote t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			List<INoteRef> list = nrf.getForNote(t);
			for (INoteRef ref : list) {
				nrf.delete(ref);
			}
			
			ofy.delete(t);
			return true;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "deleteFromPersistentDatastore" + e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	protected List<INote> getFromPersistentDatastoreByUROrdinal(int urOrd) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Note> noteQ = ofy.query(Note.class).filter("round", urOrd);
			
			List<INote> retval = new ArrayList<INote>();
			retval.addAll(noteQ.list());
			return retval;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getFromPersistentDatastoreByUROrdinal" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected List<INote> getFromPersistentDatastoreByList(ITopTenList ttl) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<NoteRef> noteRefQ = ofy.query(NoteRef.class).filter("contextId", ttl.getId());
			List<INote> retval = new ArrayList<INote>();
			
			for (INoteRef ref : noteRefQ) {
				retval.add(get(ref.getNoteId()));
			}

			return retval;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getFromPersistentDatastoreByList" + e.getLocalizedMessage(), e);
			return null;
		}
	}



}
