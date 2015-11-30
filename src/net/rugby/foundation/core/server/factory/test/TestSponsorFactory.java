package net.rugby.foundation.core.server.factory.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.ISponsorFactory;
import net.rugby.foundation.model.shared.ISponsor;
import net.rugby.foundation.model.shared.Sponsor;

public class TestSponsorFactory extends BaseCachingFactory<ISponsor> implements ISponsorFactory {

	@Override
	public ISponsor create() {
		try {
			return  new Sponsor();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected ISponsor getFromPersistentDatastore(Long id) {
		try {
			if (id != null) {
				ISponsor p = new Sponsor();
				if (id == 90000L) {
					p.setAbbr("NON");
					p.setName("None");
					p.setTagline("");
				}
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
