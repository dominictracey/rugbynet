package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;

import net.rugby.foundation.core.server.factory.BaseVenueFactory;
import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.core.server.factory.IVenueFactory;
import net.rugby.foundation.model.shared.IVenue;

public class TestVenueFactory extends BaseVenueFactory implements Serializable, IVenueFactory{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8936621707000385375L;

	@Override
	public IVenue create() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IVenue getFromPersistentDatastoreByVenueName(String venueName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IVenue getFromPersistentDatastore(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IVenue putToPersistentDatastore(IVenue t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IVenue t) {
		// TODO Auto-generated method stub
		return false;
	}

}
