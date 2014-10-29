package net.rugby.foundation.topten.server.factory.test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INoteRef;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.NoteRef;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;

public class TestNoteRefFactory extends BaseCachingFactory<INoteRef> implements INoteRefFactory {

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
			return null;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected INoteRef putToPersistentDatastore(INoteRef t) {
		try {
			return t;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + e.getLocalizedMessage(), e);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(INoteRef t) {
		try {
			return true;
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + e.getLocalizedMessage(), e);
			return false;
		}
	}

	@Override
	public List<INoteRef> getForNote(INote t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<INoteRef> getForList(ITopTenList ttl) {
		// TODO Auto-generated method stub
		return null;
	}

}
