package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingMatrix;

public interface IRatingMatrixFactory extends ICachingFactory<IRatingMatrix> {

	List<IRatingMatrix> getForRatingGroup(Long ratingGroupId);
}
