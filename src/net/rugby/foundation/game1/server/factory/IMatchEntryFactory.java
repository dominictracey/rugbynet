/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import java.util.Set;

import net.rugby.foundation.game1.shared.IMatchEntry;

/**
 * @author home
 *
 */
public interface IMatchEntryFactory {
	void setId(Long id);
	
	IMatchEntry getMatchEntry();
	IMatchEntry put(IMatchEntry e);
	Boolean delete();

	/**
	 * 
	 */
	Set<IMatchEntry> getAll();
	
	Set<IMatchEntry> getForMatch(Long matchId);
}
