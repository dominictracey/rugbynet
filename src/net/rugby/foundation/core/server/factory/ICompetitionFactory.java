package net.rugby.foundation.core.server.factory;

import java.util.List;

import net.rugby.foundation.model.shared.ICompetition;

public interface ICompetitionFactory {

	void setId(Long id);
	
	ICompetition getCompetition();
	ICompetition put(ICompetition r);

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
	void build(Long compId);

	ICompetition repair(ICompetition comp);
	
//	/**
//	 * @return the timestamp of when this competition was last updated. Useful for server-side caching
//	 */
//	Date getLastUpdate(Long compId);

	boolean delete(Long compId);
	
	ICompetition getGlobalComp();
}
