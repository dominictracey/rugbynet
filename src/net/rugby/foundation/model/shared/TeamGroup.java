package net.rugby.foundation.model.shared;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class TeamGroup extends Group implements ITeamGroup {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pool;
	private String abbr;
	private String shortName;
	private String scrumName;
	private String espnName;
	private String twitter;
	private String twitterChannel;
	private String color;
	private Long foreignId;
	@Override
	public Long getForeignId() {
		return foreignId;
	}
	@Override
	public void setForeignId(Long foreignId) {
		this.foreignId = foreignId;
	}

	public TeamGroup() {
		setGroupType(GroupType.TEAM);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		
		if (o != null) {
			if (o instanceof TeamGroup) {
				TeamGroup t = (TeamGroup)o;
				boolean sameDisplayNames = false;
				if ((t.getDisplayName() == null && getDisplayName() != null) ||
					(t.getDisplayName() != null && getDisplayName() == null)) {
					return false;
				}
				
				// big song and dance to avoid NullPointerExceptions on the equals call
				if ((t.getDisplayName() == null && getDisplayName() == null))  {
					sameDisplayNames = true;
				} else {
					sameDisplayNames = t.getDisplayName().equals(getDisplayName());
				}
			
				boolean sameShortNames = false;
				if ((t.getShortName() == null && getShortName() != null) ||
						(t.getShortName() != null && getShortName() == null)) {
						return false;
					}
					
					if ((t.getShortName() == null && getShortName() == null))  {
						sameShortNames = true;
					} else {
						sameShortNames = t.getShortName().equals(getShortName());
					}
				return (sameDisplayNames && sameShortNames); 
			}
		}
		
		return false;
	}
	
	
	  /* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#getGroupInfo()
	 */
	@Override
	  public String getGroupInfo() {
		return groupInfo;
	  }

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#getPool()
	 */
	@Override
	public String getPool() {
		return pool;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#setPool(java.lang.String)
	 */
	@Override
	public void setPool(String pool) {
		this.pool = pool;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#getAbbr()
	 */
	@Override
	public String getAbbr() {
		if (getId() == 0L) 
			return "TBD";
		return abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#setAbbr(java.lang.String)
	 */
	@Override
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#getShortName()
	 */
	@Override
	public String getShortName() {
		if (getId() == null)
			return shortName;
		if (getId() == 0L) 
			return "TBD";
		
		return shortName;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#setShortName(java.lang.String)
	 */
	@Override
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	@Override	
	public String getTwitter() {
		return twitter;
	}
	@Override
	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	@Override
	public String getDisplayName() {
		
		// ok to have a null ID, we are in the process of creation
		if (getId() != null && getId() == 0L) 
			return "TBD";
		return super.getDisplayName();
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#getColor()
	 */
	@Override
	public String getColor() {

		return color;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.ITeamGroup#setColor(java.lang.String)
	 */
	@Override
	public void setColor(String color) {
		this.color = color;
		
	}

	@Override
	public String getTwitterChannel() {
		return twitterChannel;
	}

	@Override
	public String setTwitterChannel(String twitterChannel) {
		this.twitterChannel = twitterChannel;
		return this.twitterChannel;
	}

	@Override
	public String getScrumName() {
		if (scrumName != null && !scrumName.isEmpty())
			return scrumName;
		else 
			return displayName;
	}
	@Override
	public void setScrumName(String scrumName) {
		this.scrumName = scrumName;
	}
	@Override
	public String getEspnName() {
		if (espnName != null && !espnName.isEmpty())
			return espnName;
		else 
			return displayName;
	}
	@Override
	public void setEspnName(String espnName) {
		this.espnName = espnName;
	}

}
