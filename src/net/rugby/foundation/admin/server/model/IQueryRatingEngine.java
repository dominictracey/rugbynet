package net.rugby.foundation.admin.server.model;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;



import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;

public interface IQueryRatingEngine {
	boolean setQuery(IRatingQuery q);
	List<IRatingEngineSchema> getSupportedSchemas();
	StatisticalSummary getStatisticalSummary(); 
	String toString();
	List<IPlayerRating> generate(IRatingEngineSchema schema,
			boolean scaleStandings, boolean scaleCompetition,
			boolean scaleMatchAge, boolean sendReport);
	String getMetrics();
}
