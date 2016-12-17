package net.rugby.foundation.topten.server.factory.test;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.IMatchGroupFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.core.server.factory.IPlayerMatchStatsFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.topten.server.factory.BaseRoundNodeFactory;
import net.rugby.foundation.topten.server.factory.IRoundNodeFactory;
import net.rugby.foundation.topten.server.rest.RoundNode;

import com.google.inject.Inject;

public class TestRoundNodeFactory extends BaseRoundNodeFactory implements IRoundNodeFactory {

	@Inject
	public TestRoundNodeFactory(IRatingSeriesFactory rsf,
			IPlayerRatingFactory prf, IPlayerMatchStatsFactory pmsf,
			//IPlayerMatchFactory pmf, 
			IMatchGroupFactory mgf, IRoundFactory rf,
			ITeamGroupFactory tf, IPlayerFactory pf, IUniversalRoundFactory urf) {
		super(rsf, prf, pmsf, //pmf,
				mgf, rf, tf, pf, urf);
		// TODO Auto-generated constructor stub
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
			return null;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
			return null;
		}
	}

	@Override
	protected RoundNode putToPersistentDatastore(RoundNode rn) {
		try {
			return rn;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(RoundNode rn) {
		try {

			return true;
		} catch (Exception ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE," create ", ex);
			return false;
		}
	}

	@Override
	protected List<RoundNode> getListFromPersistentDatastore(Long compId,
			int positionOrdinal) {
		// TODO Auto-generated method stub
		return null;
	}



}
