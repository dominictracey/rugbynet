package net.rugby.foundation.topten.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.NoteRef;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;

public class OfyNoteRefFactory extends BaseCachingFactory<INoteRef> implements INoteRefFactory {

	@Override
	public INoteRef create() {
		try {
			return new NoteRef();
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected INoteRef getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			return ofy.get(NoteRef.class, id);
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected INoteRef putToPersistentDatastore(INoteRef t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// make sure we don't save the xlink more than once
			Query<NoteRef> qRef = ofy.query(NoteRef.class).filter("noteId", t.getId());
			for (NoteRef ref : qRef) {
				if (ref.getContextId().equals(t.getContextId()) && ref.getNoteId().equals(t.getNoteId())) {
					return ref;
				}
			}
			
			ofy.put(t);
			return t;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(INoteRef t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			return true;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	public List<INoteRef> getForNote(INote t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			List<INoteRef> retval = new ArrayList<INoteRef>();
			Query<NoteRef> qRef = ofy.query(NoteRef.class).filter("noteId", t.getId());
			retval.addAll(qRef.list());
			return retval;
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForNote" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	public List<INoteRef> getForList(ITopTenList ttl) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			List<INoteRef> retval = new ArrayList<INoteRef>();
			Query<NoteRef> qRef = ofy.query(NoteRef.class).filter("contextId", ttl.getId());
			retval.addAll(qRef.list());
			return retval;
			
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "getForList" + e.getLocalizedMessage(), e);
			return null;
		}
	}

}
