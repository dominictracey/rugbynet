/**
 * 
 */
package net.rugby.foundation.game1.server.factory;

import java.util.Set;

import net.rugby.foundation.game1.shared.ILeague;

/**
 * @author home
 *
 */
public interface ILeagueFactory {
	void setId(Long id);

	ILeague get();
	ILeague put(ILeague l);
	/**
	 * 
	 * @return every league we have
	 */
	Set<ILeague> getAll();
	
	/**
	 * call setId first
	 * @return success
	 */
	boolean delete();
}
