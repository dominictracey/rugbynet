package net.rugby.foundation.core.server.factory;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.model.shared.ICompetition;

public interface ICompetitionFactory extends ICachingFactory<ICompetition>{

	/**
	 * @return all comps with underway=true
	 */
	List<ICompetition> getUnderwayComps();

	/**
	 * @return all comps, regardless of status
	 */
	List<ICompetition> getAllComps();

	/**
	 * @param compId
	 */
	void invalidate(Long compId);

	ICompetition repair(ICompetition comp);
	
	ICompetition getGlobalComp();
	
	List<Long> getClientComps();

	Boolean addRound(Long compId, int uri, String name);	
	
	List<ICompetition> getVirtualComps();

	Map<Long, String> getAllCompIds();

	List<Long> getUnderwayCompIds();

	Map<Long, Float> getAllCompWeights();




}
