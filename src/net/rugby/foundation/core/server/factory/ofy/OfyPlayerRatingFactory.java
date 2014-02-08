package net.rugby.foundation.core.server.factory.ofy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Query;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.PlayerRating;

public class OfyPlayerRatingFactory extends BaseCachingFactory<IPlayerRating> implements IPlayerRatingFactory {
	
	private IPlayerFactory pf;
	private IPlayerMatchStatsFactory pmsf;

	@Inject
	public OfyPlayerRatingFactory(IPlayerFactory pf, IPlayerMatchStatsFactory pmsf) {
		this.pf = pf;
		this.pmsf = pmsf;
	}
	
	@Override
	public IPlayerRating create() {
		try {
			IPlayerRating pr = new PlayerRating();
			return pr;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IPlayerRating getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			if (id != null) {
				IPlayerRating rq = ofy.get(new Key<PlayerRating>(PlayerRating.class,id));
				if (rq != null && rq.getPlayerId() != null) {
					rq.setPlayer(pf.get(rq.getPlayerId()));
				}
				for (Long pmsid : rq.getMatchStatIds()) {
					rq.addMatchStats(pmsf.getById(pmsid));
				}
				return rq;
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
	protected IPlayerRating putToPersistentDatastore(IPlayerRating rq) {
		try {
			if (rq != null) {
				Objectify ofy = DataStoreFactory.getOfy();
				ofy.put(rq);
				return rq;
			} else {
				return null;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IPlayerRating rq) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			ofy.delete(rq);
			
			return true;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return false;
		}
	}

	@Override
	public List<IPlayerRating> query(IRatingQuery query) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			// confirm list is in ascending order
			Query<PlayerRating> prq = ofy.query(PlayerRating.class).filter("queryId", query.getId());
			List<IPlayerRating> list = new ArrayList<IPlayerRating>();
			list.addAll(prq.list());
			for (IPlayerRating r : list) {
				if (r.getPlayerId() != null) {
					r.setPlayer(pf.get(r.getPlayerId()));
				}
				for (Long pmsid : r.getMatchStatIds()) {
					r.addMatchStats(pmsf.getById(pmsid));
				}
			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public boolean deleteForQuery(IRatingQuery rq) {
		try {
			if (rq != null) {
				Objectify ofy = DataStoreFactory.getOfy();
	
				Query<PlayerRating> qpmr = ofy.query(PlayerRating.class).filter("queryId", rq.getId());
				ofy.delete(qpmr);

				
			} else {
				return false; // null match
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Problem in delete: " + ex.getLocalizedMessage());
			return false;
		}
		return true;	
		
	}

	

}
