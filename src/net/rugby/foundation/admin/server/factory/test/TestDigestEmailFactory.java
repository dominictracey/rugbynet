package net.rugby.foundation.admin.server.factory.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.shared.DigestEmail;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;

public class TestDigestEmailFactory extends BaseCachingFactory<IDigestEmail> implements IDigestEmailFactory {

	private IBlurbFactory bf;

	public TestDigestEmailFactory(IBlurbFactory bf) {

		this.bf = bf;
	}
	
	@Override
	public IDigestEmail create() {
		try {
			IDigestEmail digestEmail = new DigestEmail();

			return digestEmail;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IDigestEmail getFromPersistentDatastore(Long id) {
		try {
			return null;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + ex.getMessage(), ex);
			return null;
		}
	}
//
//	private void populatedigestEmail(DigestEmail digestEmail) {
//		if (digestEmail.getBlurbs() == null) {
//			digestEmail.setBlurbs(new ArrayList<IBlurb>());
//		}
//		
//		for (Long bid : digestEmail.getBlurbIds()) {
//			digestEmail.getBlurbs().add(bf.get(bid));
//		}
//		
//	}

	@Override
	protected IDigestEmail putToPersistentDatastore(IDigestEmail digestEmail) {
		try {
			return digestEmail;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IDigestEmail digestEmail) {
		try {
			return true;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public void populatedigestEmail(IDigestEmail digestEmail) {
		// TODO Auto-generated method stub
		
	}



}
