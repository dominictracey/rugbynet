package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import net.rugby.foundation.core.server.factory.BaseRatingSeriesFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.model.shared.RatingSeries;

public class TestRatingSeriesFactory extends BaseRatingSeriesFactory implements IRatingSeriesFactory {

//	private ICompetitionFactory cf;
	private Long counter = 75100L;
//	private IRatingGroupFactory rgf;
//	@Inject
//	public TestRatingSeriesFactory(ICompetitionFactory cf, IRatingGroupFactory rgf) {
//		this.cf = cf;
//		this.rgf = rgf;
//	}
	
	@Override
	public IRatingSeries create() {
		IRatingSeries retval = new RatingSeries();
		retval.setCreated(DateTime.now().toDate());
		retval.setStart(DateTime.now().toDate());
		
		return retval;
	}

	/// ids are 75000 and up
	@Override
	protected IRatingSeries getFromPersistentDatastore(Long id) {
		IRatingSeries retval = create();
		retval.setId(id);
		if (id == 75000L) {
			// Position lists for Comp 1
			retval.getActiveCriteria().add(Criteria.IN_FORM);
			retval.getActiveCriteria().add(Criteria.BEST_YEAR);
			retval.setStart(DateTime.now().minusMonths(2).toDate());
			retval.setEnd(DateTime.now().plusMonths(2).toDate());
			retval.setLive(true);
			retval.setMode(RatingMode.BY_POSITION);
			retval.getCompIds().add(1L);
			//retval.getComps().add(cf.get(1L));
			retval.getRatingGroupIds().add(76000L);
			retval.getRatingGroupIds().add(76001L);  
//			retval.getRatingGroups().add(rgf.get(76000L));
//			retval.getRatingGroups().add(rgf.get(76001L));
			retval.setDisplayName("Best in The Rugby Championship By Position");
			build(retval);
			return retval;
		}
		
		return null;
	}

	@Override
	protected IRatingSeries putToPersistentDatastore(IRatingSeries t) {
		if (t.getId() == null) {
			t.setId(counter++);
		}
		t.setUpdated(DateTime.now().toDate());
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRatingSeries t) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public IRatingSeries get(Long compId, RatingMode mode) {
		return get(75000L);
	}

	@Override
	public List<RatingMode> getModesForCompFromPersistentDatastore(Long compId) {
		List<RatingMode> list = new ArrayList<RatingMode>();
		list.add(RatingMode.BY_POSITION);
		list.add(RatingMode.BY_LAST_MATCH);
		return null;
	}


}
