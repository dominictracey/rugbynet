package net.rugby.foundation.model.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;

@SuppressWarnings({ "serial", "unused" })
@Entity
public class Group implements Serializable {
  @Id
  private Long id;
  private String displayName;
  //private ArrayList<Key<Player>> members;
  private GroupType groupType;
  protected String groupInfo;
  


//if you change here, also change mapping in Browse.java :(
  public enum GroupType { ADHOC, TEAM, POSITION, MATCH, FEATURED, NONE, MY }
  
  public Group() {
    //new Group(null, GroupType.ADHOC, "", new ArrayList<Key<Player>>() );
	  //members = new ArrayList<Key<Player>>();
  }

//  public Group(Long id, GroupType type, String displayName, ArrayList<Key<Player>> m) {
//	assert m != null;
//    this.id = id;
//    this.groupType = type;
//    this.displayName = displayName;
//    this.setMembers(m);
//  }
  
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  
  public GroupType getGroupType() {	return groupType; }
  public void setGroupType(GroupType groupType) {	this.groupType = groupType; }

  public String getDisplayName() { return displayName; }
  public void setDisplayName(String displayName) { this.displayName = displayName; }

//  public ArrayList<Key<Player>> getMembers() {	return members;	}
//  public void setMembers(ArrayList<Key<Player>> members) {	this.members.addAll(members);	} 
//
//  public void addMember(Key<Player> m)
//  {
//	  members.add(m);
//  }
  
  public String getGroupInfo() {
	return groupInfo;
  }

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
	}
}
