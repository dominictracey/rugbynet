package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IRawScore;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.RawScore;

public class OfyRawScoreFactory extends BaseCachingFactory<IRawScore> implements  IRawScoreFactory, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7135535109216170518L;

	@Override
	public IRawScore getFromPersistentDatastore(Long id) {
		if (id == null) {
			throw new RuntimeException("Call create to get a new raw score.");
		}
		
		Objectify ofy = DataStoreFactory.getOfy();
		IRawScore t = (IRawScore)ofy.get(new Key<RawScore>(RawScore.class,id));
		return t;
	}
	
	@Override
	public IRawScore putToPersistentDatastore(IRawScore rawScore) {
		if (rawScore == null) {
			return null;
		}
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put((RawScore)rawScore);
		
		return rawScore;
	}
	


	@Override
	public IRawScore create() {
		return new RawScore();
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRawScore t) {
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.delete(t);
		return true;
	}

	@Override
	public IRawScore getForPMSid(Long pmsId, Long schemaId) {
		if (pmsId == null || schemaId == null) {
			throw new RuntimeException("Call create to get a new raw score.");
		}
		
		Objectify ofy = DataStoreFactory.getOfy();
		Query<RawScore> qpmr = ofy.query(RawScore.class).filter("playerMatchStatsId", pmsId).filter("schemaId", schemaId);
		return (IRawScore)qpmr.get();
	}

	@Override
	public boolean deleteForPMSid(Long pmsId) {
		if (pmsId != null) {
			IRawScore rs = get(pmsId);
			if (rs != null) {
				DataStoreFactory.getOfy().delete(rs);
			}
			return true;
		}
		
		return false;
	}

}
