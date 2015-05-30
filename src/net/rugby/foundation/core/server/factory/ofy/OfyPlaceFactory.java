package net.rugby.foundation.core.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BasePlaceFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ScrumPlayerMatchStats;
import net.rugby.foundation.model.shared.ServerPlace;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class OfyPlaceFactory extends BasePlaceFactory implements IPlaceFactory {

	@Inject
	public OfyPlaceFactory(ITopTenListFactory ttlf, IRatingQueryFactory rqf, IConfigurationFactory ccf, IRatingSeriesFactory rsf, IRatingMatrixFactory rmf, IRatingGroupFactory rgf) {
		super(ttlf, rqf, ccf, rsf, rmf, rgf);

	}

	@Override
	public boolean delete(IServerPlace t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public IServerPlace create() {
		try {
			IServerPlace place = new ServerPlace();
			put(place);  // get id
			place.setGuid(generate(place.getId()));
			put(place); // update cache
			return place;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}

	}


	@Override
	protected IServerPlace getForNameFromPersistentDatastore(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IServerPlace putToPersistentDatastore(IServerPlace player) {
		try {
			if (player == null) {
				Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Don't try to put with null. Call create() instead!");
				return null;
			}
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.put(player);
			return player;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	protected IServerPlace getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IServerPlace p = ofy.get(new Key<ServerPlace>(ServerPlace.class,id));
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
	protected boolean deleteFromPersistentDatastore(IServerPlace t) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(t);
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
		return true;
	}
	
	@Override
	public List<IServerPlace> getForCompId(Long id) {
		try {
			List<IServerPlace> list = new ArrayList<IServerPlace>();
			Objectify ofy = DataStoreFactory.getOfy();
			
			Query<ServerPlace> qsp = ofy.query(ServerPlace.class).filter("compId",id);
			
			for (ServerPlace sp : qsp.list()) {
				list.add(sp);
			}
			return list;
		} catch (Exception e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "generate" + e.getMessage(), e);
			return null;
		}	
	}

}
