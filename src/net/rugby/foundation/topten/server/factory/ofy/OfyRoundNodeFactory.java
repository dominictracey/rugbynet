package net.rugby.foundation.topten.server.factory.ofy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.topten.server.factory.BaseRoundNodeFactory;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;
import net.rugby.foundation.topten.server.rest.RoundNode;
import net.rugby.foundation.topten.server.rest.RoundNode.PlayerMatch;

import com.google.inject.Inject;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

public class OfyRoundNodeFactory extends BaseRoundNodeFactory implements IRoundNodeFactory {
	@Inject
	public OfyRoundNodeFactory(IRatingSeriesFactory rsf,
			IPlayerRatingFactory prf, IPlayerMatchStatsFactory pmsf,
	//		IPlayerMatchFactory pmf, 
			IMatchGroupFactory mgf, IRoundFactory rf,
			ITeamGroupFactory tf, IPlayerFactory pf, IUniversalRoundFactory urf, IRatingGroupFactory rgf) {
		super(rsf, prf, pmsf, 
				//pmf, 
				mgf, rf, tf, pf, urf, rgf);
	}

	@Override
	public RoundNode create() {
		try {
			return new RoundNode();
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
			return null;
		}
	}

	@Override
	protected RoundNode getFromPersistentDatastore(Long id) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			
			RoundNode rn = ofy.get(RoundNode.class, id);
			
			populateMap(rn);
			
			return rn;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," get ", ex);
			return null;
		}
	}

	private void populateMap(RoundNode rn) {
		// populate map
		for (PlayerMatch pm : rn.get_playerMatches()) {
			//PlayerMatch pm = pmf.get(pid);
			IPlayer p = pf.get(pm.playerId);
			
			// need to deal with the eventuality of having multiple matches per round per player (RWC)
			if (!rn.playerMatches.containsKey(p.getDisplayName())) {
				rn.playerMatches.put(p.getDisplayName(), new ArrayList<PlayerMatch>());
			}
			rn.playerMatches.get(p.getDisplayName()).add(pm);
		}
	}

	@Override
	protected RoundNode putToPersistentDatastore(RoundNode rn) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			for (List<PlayerMatch> pml: rn.playerMatches.values()) {
				for (PlayerMatch pm : pml) {
					if (!rn.get_playerMatches().contains(pm))
						rn.get_playerMatches().add(pm);
				}
			}
			ofy.put(rn);
			return rn;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," put ", ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(RoundNode rn) {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
//			for (Long pmid : rn.getPlayerMatchIds()) {
//				pmf.delete(pmf.get(pmid));
//			}
			ofy.delete(rn);
			return true;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," delete ", ex);
			return false;
		}
	}

	@Override
	protected List<RoundNode> getListFromPersistentDatastore(Long compId, int positionOrdinal) {
		try {
			List<RoundNode> retval = new ArrayList<RoundNode>();

			// first get ones from the database
			Objectify ofy = DataStoreFactory.getOfy();
			Query<RoundNode> qrn = ofy.query(RoundNode.class).filter("compId",compId).filter("positionOrdinal", positionOrdinal);

			for (RoundNode rn : qrn.list()) {
				populateMap(rn);
				retval.add(rn);
			}
			
			Collections.sort(retval, new Comparator<RoundNode>() {
				@Override
			    public int compare(RoundNode e1, RoundNode e2) {
			        if(e1.round < e2.round){
			            return 1;
			        } else {
			            return -1;
			        }
			    }
			});		
			
			return retval;

		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
			return new ArrayList<RoundNode>();
		}
	}



}
