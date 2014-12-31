package net.rugby.foundation.core.server.factory.ofy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ISponsorFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.Sponsor;

public class OfySponsorFactory extends BaseCachingFactory<ISponsor> implements ISponsorFactory {

	@Override
	public ISponsor create() {
		try {
			ISponsor s = new Sponsor();
			s.setActive(true);
			return s;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected ISponsor getFromPersistentDatastore(Long id) {
		try {
			if (id != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ISponsor p = ofy.get(new Key<Sponsor>(Sponsor.class,id));
				return p;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected ISponsor putToPersistentDatastore(ISponsor t) {
		try {
			if (t != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.put(t);
				return t;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(ISponsor t) {
		try {
			if (t != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.delete(t);
				return true;
			} else {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to get with null. Call create() instead!");
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

}
