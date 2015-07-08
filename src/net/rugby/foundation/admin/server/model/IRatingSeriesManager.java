package net.rugby.foundation.admin.server.model;

import org.joda.time.DateTime;

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
//	public abstract String process(IRatingSeries rs);

	Boolean readyForNewGroup(ISeriesConfiguration config);

	public abstract IRatingGroup doRatingGroup(IRatingSeries series, UniversalRound time);

}