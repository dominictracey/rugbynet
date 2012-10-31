/**
 * 
 */
package net.rugby.foundation.game1.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Transient;

import com.googlecode.objectify.annotation.Entity;

/**
 * @author home
 *
 */
@Entity
public class League implements ILeague, Serializable {
	

	private static final long serialVersionUID = 1L;
	
	@Id
	private Long id = null;	
	private List<Long> entryIds;
	
	@Transient
	private Map<Long,IEntry> entryMap;
	
	public League() {
		entryIds = new ArrayList<Long>();
		entryMap = new HashMap<Long,IEntry>();
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeague#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeague#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeague#getEntryIds()
	 */
	@Override
	public final List<Long> getEntryIds() {
		return entryIds;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeague#getEntryMap()
	 */
	@Override
	public final Map<Long,IEntry> getEntryMap() {
		return entryMap;
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.shared.ILeague#addEntryId(java.lang.Long)
	 */
	@Override
	public void addEntry(IEntry entry) {
		if (!entryIds.contains(entry.getId())) {
			entryIds.add(entry.getId());
			entryMap.put(entry.getId(), entry);
		}
	}
	
	
}
