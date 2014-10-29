package net.rugby.foundation.topten.server.factory.test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.server.factory.BaseNoteFactory;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;

public class TestNoteFactory extends BaseNoteFactory implements INoteFactory {


	@Inject
	public TestNoteFactory(INoteRefFactory nrf) {
		super(nrf);	
	}
	
	@Override
	protected List<INote> getFromPersistentDatastoreByUROrdinal(int urOrd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected INote getFromPersistentDatastore(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected INote putToPersistentDatastore(INote t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(INote t) {
		// TODO Auto-generated method stub
		return false;
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
	protected List<INote> getFromPersistentDatastoreByList(ITopTenList ttl) {
		// TODO Auto-generated method stub
		return null;
	}

}
