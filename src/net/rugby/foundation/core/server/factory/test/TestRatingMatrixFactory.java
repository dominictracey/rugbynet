package net.rugby.foundation.core.server.factory.test;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.server.factory.BaseRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingMatrixFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingMatrix;

import org.joda.time.DateTime;

import com.google.inject.Inject;

public class TestRatingMatrixFactory extends BaseRatingMatrixFactory implements IRatingMatrixFactory {

	private Long counter = 76100L;

	
	@Inject 
	public TestRatingMatrixFactory(IRatingSeriesFactory rsf, IRatingQueryFactory rqf) {
		super(rsf, rqf);
	}
	
	@Override
	public IRatingMatrix create() {
		IRatingMatrix rm = new RatingMatrix();
		rm.setGenerated(DateTime.now().toDate());
		return rm;
	}

	@Override
	protected IRatingMatrix getFromPersistentDatastore(Long id) {
		IRatingMatrix rm = new RatingMatrix();
		rm.setId(id);
		if (id.equals(77100L)) {  // DEFAULT IS LATEST AND ROUND?
			rm.setRatingGroupId(76001L);
			//rm.setRatingGroup(rgf.get(76001L));
//			rm.getRoundIds().add(7L);
//			rm.getRoundIds().add(6L);
			rm.setCriteria(Criteria.ROUND);
			rm.setGenerated(DateTime.now().toDate());
			rm.getRatingQueryIds().add(7710001L);
			rm.getRatingQueries().add(rqf.get(7710001L)); // first in list is default
			rm.getRatingQueryIds().add(7710002L);
			rm.getRatingQueries().add(rqf.get(7710002L));
			rm.getRatingQueryIds().add(7710003L);
			rm.getRatingQueries().add(rqf.get(7710003L));
			rm.getRatingQueryIds().add(7710004L);
			rm.getRatingQueries().add(rqf.get(7710004L));
			rm.getRatingQueryIds().add(7710005L);
			rm.getRatingQueries().add(rqf.get(7710005L));
			rm.getRatingQueryIds().add(7710006L);
			rm.getRatingQueries().add(rqf.get(7710006L));
			rm.getRatingQueryIds().add(7710007L);
			rm.getRatingQueries().add(rqf.get(7710007L));
			rm.getRatingQueryIds().add(7710008L);
			rm.getRatingQueries().add(rqf.get(7710008L));
			rm.getRatingQueryIds().add(7710009L);
			rm.getRatingQueries().add(rqf.get(7710009L));
			rm.getRatingQueryIds().add(7710010L);
			rm.getRatingQueries().add(rqf.get(7710010L));
		} else if (id.equals(77101L)) {
			rm.setRatingGroupId(76001L);
			//rm.setRatingGroup(rgf.get(76001L));
//			rm.getRoundIds().add(7L);
//			rm.getRoundIds().add(6L);
			rm.setCriteria(Criteria.IN_FORM);
			rm.setGenerated(DateTime.now().toDate());
			rm.getRatingQueryIds().add(7710101L);
			rm.getRatingQueryIds().add(7710102L);
			rm.getRatingQueryIds().add(7710103L);
			rm.getRatingQueryIds().add(7710104L);
			rm.getRatingQueryIds().add(7710105L);
			rm.getRatingQueryIds().add(7710106L);
			rm.getRatingQueryIds().add(7710107L);
			rm.getRatingQueryIds().add(7710108L);
			rm.getRatingQueryIds().add(7710109L);
			rm.getRatingQueryIds().add(7710110L);
			rm.getRatingQueries().add(rqf.get(7710101L));
			rm.getRatingQueries().add(rqf.get(7710102L));
			rm.getRatingQueries().add(rqf.get(7710103L));
			rm.getRatingQueries().add(rqf.get(7710104L));
			rm.getRatingQueries().add(rqf.get(7710105L));
			rm.getRatingQueries().add(rqf.get(7710106L));
			rm.getRatingQueries().add(rqf.get(7710107L));
			rm.getRatingQueries().add(rqf.get(7710108L));
			rm.getRatingQueries().add(rqf.get(7710109L));
			rm.getRatingQueries().add(rqf.get(7710110L));	
			
		} else if (id.equals(77000L)) {
			rm.setRatingGroupId(76000L);
			//rm.setRatingGroup(rgf.get(76000L));
//			rm.getRoundIds().add(6L);
			rm.setCriteria(Criteria.ROUND);
			rm.setGenerated(DateTime.now().toDate());
			rm.getRatingQueryIds().add(7700001L);
			rm.getRatingQueryIds().add(7700002L);
			rm.getRatingQueryIds().add(7700003L);
			rm.getRatingQueryIds().add(7700004L);
			rm.getRatingQueryIds().add(7700005L);
			rm.getRatingQueryIds().add(7700006L);
			rm.getRatingQueryIds().add(7700007L);
			rm.getRatingQueryIds().add(7700008L);
			rm.getRatingQueryIds().add(7700009L);
			rm.getRatingQueryIds().add(7700010L);
			rm.getRatingQueries().add(rqf.get(7700001L));
			rm.getRatingQueries().add(rqf.get(7700002L));
			rm.getRatingQueries().add(rqf.get(7700003L));
			rm.getRatingQueries().add(rqf.get(7700004L));
			rm.getRatingQueries().add(rqf.get(7700005L));
			rm.getRatingQueries().add(rqf.get(7700006L));
			rm.getRatingQueries().add(rqf.get(7700007L));
			rm.getRatingQueries().add(rqf.get(7700008L));
			rm.getRatingQueries().add(rqf.get(7700009L));
			rm.getRatingQueries().add(rqf.get(7700010L));
		} else if (id.equals(77001L)) {
			rm.setRatingGroupId(76000L);
			//rm.setRatingGroup(rgf.get(76000L));
//			rm.getRoundIds().add(6L);
			rm.setCriteria(Criteria.IN_FORM);
			rm.setGenerated(DateTime.now().toDate());
			rm.getRatingQueryIds().add(7700101L);
			rm.getRatingQueryIds().add(7700102L);
			rm.getRatingQueryIds().add(7700103L);
			rm.getRatingQueryIds().add(7700104L);
			rm.getRatingQueryIds().add(7700105L);
			rm.getRatingQueryIds().add(7700106L);
			rm.getRatingQueryIds().add(7700107L);
			rm.getRatingQueryIds().add(7700108L);
			rm.getRatingQueryIds().add(7700109L);
			rm.getRatingQueryIds().add(7700110L);
			rm.getRatingQueries().add(rqf.get(7700101L));
			rm.getRatingQueries().add(rqf.get(7700102L));
			rm.getRatingQueries().add(rqf.get(7700103L));
			rm.getRatingQueries().add(rqf.get(7700104L));
			rm.getRatingQueries().add(rqf.get(7700105L));
			rm.getRatingQueries().add(rqf.get(7700106L));
			rm.getRatingQueries().add(rqf.get(7700107L));
			rm.getRatingQueries().add(rqf.get(7700108L));
			rm.getRatingQueries().add(rqf.get(7700109L));
			rm.getRatingQueries().add(rqf.get(7700110L));
		}
		
		for (IRatingQuery rq: rm.getRatingQueries()) {
			rq.setRatingMatrix(rm);
			rq.setRatingMatrixId(rm.getId());
		}
		
		return rm;
	}

	@Override
	protected IRatingMatrix putToPersistentDatastore(IRatingMatrix t) {
		if (t.getId() == null) {
			t.setId(counter++);
		}
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IRatingMatrix t) {
		return true;
	}

	@Override
	public List<IRatingMatrix> getForRatingGroup(Long ratingGroupId) {
		List<IRatingMatrix> rms = new ArrayList<IRatingMatrix>();
		if (ratingGroupId == 76001L) {
			rms.add(get(77100L));
			rms.add(get(77101L));
		} else if (ratingGroupId == 76000L) {
			rms.add(get(77000L));
			rms.add(get(77001L));
		}
		return rms;
	}



}
