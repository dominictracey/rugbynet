package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseVenueFactory;
import net.rugby.foundation.core.server.factory.IVenueFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IVenue;
import net.rugby.foundation.model.shared.Venue;

public class OfyVenueFactory extends BaseVenueFactory implements Serializable, IVenueFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -432668421907824342L;

	@Override
	public IVenue create() {
		try{
			IVenue v = new Venue();
			return v;
		}catch(Throwable e){
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public IVenue putToPersistentDatastore(IVenue t) {
		try{
			if(t == null){
				return new Venue();
			}
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(t);
			
			dropFromCache(getCacheId(t.getId()).toString());
			
			return t;
		}catch(Throwable ex){
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}
	
	@Override
	protected IVenue getFromPersistentDatastore(Long id) {
		try{
			Objectify ofy = DataStoreFactory.getOfy();
			if(id != null){
				IVenue v = ofy.get(new Key<Venue>(Venue.class, id));
				return v;
			}
			else{
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Your ID was null. Try with create(), instead.");
				return null;
			}
		}catch(Throwable ex){
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IVenue getFromPersistentDatastoreByVenueName(String venueName) {
		try{
			Objectify ofy = DataStoreFactory.getOfy();
			if(venueName != null){
				Query<Venue> qv = ofy.query(Venue.class).filter("venueName", venueName);
				return qv.get();
			}
			else{
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Your venueName was null. Try with create(), instead.");
				return null;
			}
		}catch(Throwable ex){
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}	

	@Override
	protected boolean deleteFromPersistentDatastore(IVenue t) {
		try{
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			
			return true;
		}catch(Throwable ex){
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

}
