package net.rugby.foundation.admin.server.model;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;



import net.rugby.foundation.admin.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IPlayerMatchStats;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.ITeamMatchStats;

public interface IQueryRatingEngine {
	void addTeamStats(List<ITeamMatchStats> teamStats);
	void addPlayerStats(List<IPlayerMatchStats> playerStats);
	List<IPlayerRating> generate(IRatingEngineSchema schema);
	List<IRatingEngineSchema> getSupportedSchemas();
	StatisticalSummary getStatisticalSummary(); 
	String toString();
}
