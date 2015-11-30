package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.Date;

public class UniversalRound implements IHasId, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6830183365643825087L;
	public int ordinal;
	public String abbr;
	public String shortDesc;
	public String longDesc;
	public Date start;
	
	public UniversalRound() {
		
	}
	
	public UniversalRound(int ordinal, String abbr, String shortDesc,
			String longDesc, Date start) {
		super();
		this.ordinal = ordinal;
		this.abbr = abbr;
		this.shortDesc = shortDesc;
		this.longDesc = longDesc;
		this.start = start;
	}
	
	// Implement IHasId just so we can use memcache
	@Override
	public void setId(Long id) {
		throw new RuntimeException("Don't use the id");	
	}
	@Override
	public Long getId() {
		throw new RuntimeException("Don't use the id");
	}
}
