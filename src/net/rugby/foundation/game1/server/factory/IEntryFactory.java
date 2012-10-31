package net.rugby.foundation.game1.server.factory;

import java.util.ArrayList;

import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRound;

public interface IEntryFactory {

	void setId(Long id);
	
	IEntry getEntry();
	IEntry put(IEntry e);
	Boolean delete();
	
	/**
	 * @param userId
	 * @param compId
	 */
	void setUserIdAndCompId(Long userId, Long compId);

	/**
	 * Call before calling getEntries()
	 * @param round set to null if you want all the entries in the comp, or to an IRound if you want just the entries with RoundEntries for the given round.
	 * @param comp
	 */
	void setRoundAndComp(IRound round, ICompetition comp);
	
	/**
	 * @return list of entries for user and comp set with either  setUserIdAndCompId() or setRoundAndComp()
	 */
	ArrayList<IEntry> getEntries();

	/**
	 * Used to set up a call to getEntry() that gets an Entry by name (to check for duplicate names)
	 * @param name
	 * @param comp
	 * @param userId
	 */
	void setNameAndComp(String name, ICompetition comp, Long userId);
}
