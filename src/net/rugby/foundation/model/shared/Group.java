package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings({ "serial", "unused" })
@Entity
public class Group implements Serializable, IGroup {
  @Id
  private Long id;
  protected String displayName;
  private List<IPlayer> members;
  private GroupType groupType;
  protected String groupInfo;
  protected Date createdDate;
  protected Date lastModifiedDate;


//if you change here, also change mapping in Browse.java :(
  public enum GroupType { ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE, MY }
  
  public Group() {
	  members = new ArrayList<IPlayer>();
  }
 
  
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#getGroupType()
 */
@Override
public GroupType getGroupType() {	return groupType; }
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#setGroupType(net.rugby.foundation.model.shared.Group.GroupType)
 */
@Override
public void setGroupType(GroupType groupType) {	this.groupType = groupType; }

  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#getDisplayName()
 */
@Override
public String getDisplayName() { return displayName; }
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#setDisplayName(java.lang.String)
 */
@Override
public void setDisplayName(String displayName) { this.displayName = displayName; }

  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#getMembers()
 */
@Override
public Iterator<IPlayer> getMembers() {	return members.iterator();	}
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#setMembers(java.util.List)
 */
@Override
public void setMembers(List<IPlayer> members) {	this.members = members;	} 

  
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#getGroupInfo()
 */
@Override
public String getGroupInfo() {
	return groupInfo;
  }

	/* (non-Javadoc)
	 * @see net.rugby.foundation.model.shared.IGroup#setGroupInfo(java.lang.String)
	 */
	@Override
	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}

	@Override
	public void add(IPlayer p) {
		members.add(p);
		
	}


	@Override
	public void setId(Long id) {
		this.id = id;
	}


	@Override
	public Long getId() {
		return id;
	}

}
