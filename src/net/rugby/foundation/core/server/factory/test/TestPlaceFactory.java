package net.rugby.foundation.core.server.factory.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.core.server.factory.BasePlaceFactory;
import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.model.shared.ServerPlace;
import net.rugby.foundation.model.shared.IServerPlace.PlaceType;

public class TestPlaceFactory extends BasePlaceFactory implements IPlaceFactory {

	private Long count = 33900L;

	@Override
	public boolean delete(IServerPlace t) {
		return true;
	}

	@Override
	public IServerPlace create() {
		IServerPlace place = new ServerPlace();
		place.setId(count++);
		place.setGuid(generate(place.getId()));
		return place;
	}

	protected IServerPlace build(Long id, PlaceType type, Long compId, Long featureId, Long seriesId, Long groupId, Long matrixId, Long queryId, Long listId, Long itemId) {
		IServerPlace p = new ServerPlace(type, compId, featureId, seriesId, groupId, matrixId, queryId, listId, itemId); 
		p.setGuid(generate(id));
		return p;
	}
	
	// use: http://jsfiddle.net/ivanakimov/8z6d8o94/
	@Override
	protected IServerPlace getForGuidFromPersistentDatastore(String guid) {
		if (guid.equals(generate(33000L))) {
			// default series for comp
			return build(33000L, PlaceType.SERIES, 1L, null, null, null, null, null, null, null);
		} else if (guid.equals(generate(33001L))) {
			// BY_POSITION series for comp 1
			return build(33001L, PlaceType.SERIES, 1L, null, 75000L, null, null, null, null, null);
		} else if (guid.equals(generate(33002L))) {
			// Round 1
			return build(33002L, PlaceType.SERIES, 1L, null, 75000L, 76000L, null, null, null, null);
		} else if (guid.equals(generate(33003L))) {
			// BEST_YEAR criteria
			return build(33003L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, null, null, null);
		} else if (guid.equals(generate(33004L))) {
			// the RQ - do we need to specify this?
			return build(33004L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, null, null);
		} else if (guid.equals(generate(33005L))) {
			// the TTL - see above
			return build(33005L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, 1000L, null);
		} else if (guid.equals(generate(33006L))) {
			// TTI - particular player
			return build(33006L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, 1000L, 1000007L);
		} else if (guid.equals(generate(33100L))) {
			// latest feature for comp
			return build(33100L, PlaceType.FEATURE, 1L, null, null, null, null, null, null, null);
		}
		return null;
	}

	@Override
	protected IServerPlace getForNameFromPersistentDatastore(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IServerPlace getFromPersistentDatastore(Long id) {
		for (Long l = 33000L; l < 33009L; l++) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, l + "=>" + generate(l));
		}
		if (id == 33000L) { // PeaJ
			// default series for comp
			return build(33000L, PlaceType.SERIES, 1L, null, null, null, null, null, null, null);
		} else if (id == 33001L) { // 8WqP
			// BY_POSITION series for comp 1
			return build(33001L, PlaceType.SERIES, 1L, null, 75000L, null, null, null, null, null);
		} else if (id == 33002L) { // Qrjj
			// Round 1
			return build(33002L, PlaceType.SERIES, 1L, null, 75000L, 76000L, null, null, null, null);
		} else if (id == 33003L) { // 9WZL
			// BEST_YEAR criteria
			return build(33003L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, null, null, null);
		} else if (id == 33004L) { // vp8X
			// the RQ - do we need to specify this?
			return build(33004L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, null, null);
		} else if (id == 33005L) { // pN2v
			// the TTL - see above
			return build(33005L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, 1000L, null);
		} else if (id == 33006L) { // 0L7V
			// TTI - particular player
			return build(33006L, PlaceType.SERIES, 1L, null, 75000L, 76000L, 77100L, 7710000L, 1000L, 1000007L);
		} else if (id == 33100L) {
			// latest feature for comp
			return build(33100L, PlaceType.FEATURE, 1L, null, null, null, null, null, null, null);
		}
		return null;
	}

	@Override
	protected IServerPlace putToPersistentDatastore(IServerPlace t) {
		// TODO Auto-generated method stub
		return t;
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IServerPlace t) {
		// TODO Auto-generated method stub
		return false;
	}

}
