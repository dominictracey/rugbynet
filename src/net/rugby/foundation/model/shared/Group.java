package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
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
  private String displayName;
  private List<IPlayer> members;
  private GroupType groupType;
  protected String groupInfo;
  


//if you change here, also change mapping in Browse.java :(
  public enum GroupType { ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE, MY }
  
  public Group() {
    //new Group(null, GroupType.ADHOC, "", new ArrayList<Key<Player>>() );
	  members = new ArrayList<IPlayer>();
  }

//  public Group(Long id, GroupType type, String displayName, ArrayList<Key<Player>> m) {
//	assert m != null;
//    this.id = id;
//    this.groupType = type;
//    this.displayName = displayName;
//    this.setMembers(m);
//  }
  
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#getId()
 */
@Override
public Long getId() { return id; }
  /* (non-Javadoc)
 * @see net.rugby.foundation.model.shared.IGroup#setId(java.lang.Long)
 */
@Override
public void setId(Long id) { this.id = id; }
  
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
//
//  public void addMember(Key<Player> m)
//  {
//	  members.add(m);
//  }
  
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
}
