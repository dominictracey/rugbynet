package net.rugby.foundation.admin.server.model;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingSeries;

public interface IRatingSeriesManager {

	// This creates the first RatingGroups and RatingMatrices and populates them with RatingQueries
	public abstract List<IRatingGroup> initialize(IRatingSeries rs);

}