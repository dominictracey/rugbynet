package net.rugby.foundation.core.server.factory.ofy;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ISponsorFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.Sponsor;

public class OfySponsorFactory extends BaseCachingFactory<ISponsor> implements ISponsorFactory {

	public OfySponsorFactory() {
		// make sure we have one so we can hack in more in prod
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			Query<Sponsor> qs = ofy.query(Sponsor.class);
			if (qs.count() == 0) {
				ISponsor s = create();
				s.setAbbr("NON");
				s.setContactName("Dominic Tracey");
				s.setName("None");
				s.setTagline(" ");
				s.setEmail("dominic.tracey@rugby.net");
				s.setUrl(" ");
				s.setHeight(40);
				s.setWidth(200);
				put(s);
				
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
		}		
	}
	
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
