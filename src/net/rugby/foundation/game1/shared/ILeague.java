/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.util.List;
import java.util.Map;

/**
 * @author home
 *
 */
public interface ILeague {

	public abstract Long getId();

	public abstract void setId(Long id);

	/**
	 * 
	 * @return an unmutable list of EntryIds. Use addEntryId to add members.
	 */
	public abstract List<Long> getEntryIds();


	/**
	 * 
	 * @return an unmutable HashMap (Key: EntryId, Value: IEntry)
	 */
	public abstract Map<Long, IEntry> getEntryMap();


	/**
	 *  Add an entry to this League
	 */
	public abstract void addEntry(IEntry entry);
}