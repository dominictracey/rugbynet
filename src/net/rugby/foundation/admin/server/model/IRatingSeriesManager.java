package net.rugby.foundation.admin.server.model;

import net.rugby.foundation.admin.shared.ISeriesConfiguration;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.UniversalRound;

public interface IRatingSeriesManager {

	/**
	 * 
	 * @param seriesConfig
	 * @return RatingSeries associated with the provided SeriesConfiguration
	 */
	public abstract IRatingSeries initialize(ISeriesConfiguration seriesConfig);
	
	/**
	 * 
	 * @param rs
	 * @return pipeline handle of created workflow
	 */

	/**
	 * @return a properly configured RatingGroup placed in the RatingSeries specified by the SeriesConfiguration for the given time
	 */
	public abstract IRatingGroup getRatingGroup(ISeriesConfiguration sc, UniversalRound time);

}