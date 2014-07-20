package net.rugby.foundation.core.server.factory.test;

import java.io.Serializable;
import java.util.Random;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRawScoreFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IRawScore;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.RawScore;

public class TestRawScoreFactory extends BaseCachingFactory<IRawScore> implements  IRawScoreFactory {
	private IMatchRatingEngineSchemaFactory sf;

	/**
	 * 
	 */

	@Inject
	public TestRawScoreFactory(IMatchRatingEngineSchemaFactory sf)
	{
		this.sf = sf;
		
	}
	Random r = new Random();
	
	@Override
	public IRawScore getFromPersistentDatastore(Long id) {
		if (id == null) {
			throw new RuntimeException("Call create to get a new raw score.");
		}
		IRawScore t = new RawScore();
		t.setId(id);
		t.setSchemaId(sf.getDefault().getId());
		t.setPlayerId(9002000L + r.nextInt(22)); //9002011L
		t.setPlayerMatchStatsId(t.getPlayerId() + 10000);
		return t;
	}
	
	@Override
	public IRawScore putToPersistentDatastore(IRawScore rawScore) {
		if (rawScore == null) {
			return null;
		}
		Objectify ofy = DataStoreFactory.getOfy();
		ofy.put(rawScore);
		
		return rawScore;
	}
	


	@Override
	public IRawScore create() {
		return new RawScore();
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRawScore t) {
		return true;
	}

	@Override
	public IRawScore getForPMSid(Long pmsId, Long schemaId) {
		if (pmsId == null || schemaId == null) {
			throw new RuntimeException("Call create to get a new raw score.");
		}

		return get(r.nextLong());
	}

	@Override
	public boolean deleteForPMSid(Long pmsId) {
		return true;
	}

}
