package net.rugby.foundation.core.server.factory.test;

import java.util.Date;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IUniversalRoundFactory;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.RatingGroup;


public class TestRatingGroupFactory extends BaseCachingFactory<IRatingGroup> implements IRatingGroupFactory {

	private IRatingMatrixFactory rmf;
	private IRatingSeriesFactory rsf;
	private Long counter = 77100L;
	private IUniversalRoundFactory urf;
	
	@Inject
	public TestRatingGroupFactory(IRatingMatrixFactory rmf, IRatingSeriesFactory rsf, IUniversalRoundFactory urf)
	{
		this.rmf = rmf;
		this.rsf = rsf;
		this.urf = urf;
	}
	
	@Override
	public IRatingGroup create() {
		IRatingGroup rg = new RatingGroup();
		return rg;
	}

	@Override
	protected IRatingGroup getFromPersistentDatastore(Long id) {
		IRatingGroup rg = create();
		rg.setId(id);
		rg.setUniversalRound(urf.get(rg.getUniversalRoundOrdinal()));
		rg.setLabel(rg.getUniversalRound().abbr);
		if (id == 76000L) {   // comp 1, round 1
			rg.setRatingSeriesId(75000L);
			rg.getRatingMatrixIds().add(77000L);  // best of year
			rg.getRatingMatrixIds().add(77001L);  // in form
			rg.getRatingMatrices().add(rmf.get(77000L));
			rg.getRatingMatrices().add(rmf.get(77001L));
			rg.setLabel("Round 1");
			//rg.getRatingMatrices().add(rmf.get(77000L));
			return rg;
		} else if (id == 76001L) {  // comp 1, round 2 (latest is default)
			rg.setRatingSeriesId(75000L);
			rg.getRatingMatrixIds().add(77100L);  // best of year (default)
			rg.getRatingMatrices().add(rmf.get(77100L));  // populate default
			rg.getRatingMatrixIds().add(77101L);  // inform
			rg.getRatingMatrices().add(rmf.get(77101L));
			rg.setLabel("Round 2");
			return rg;
		}
		return null;
	}

	@Override
	protected IRatingGroup putToPersistentDatastore(IRatingGroup t) {
		if (t.getId() == null) {
			t.setId(counter++);
		}
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRatingGroup t) {
		return true;
	}

}
