package net.rugby.foundation.core.server.factory;

import java.util.List;

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
	
	List<ICompetition> getClientComps();

}
