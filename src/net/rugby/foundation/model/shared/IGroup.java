package net.rugby.foundation.model.shared;

import java.util.Iterator;
import java.util.List;

import net.rugby.foundation.model.shared.Group.GroupType;

public interface IGroup extends IHasId {

	public abstract GroupType getGroupType();

	public abstract void setGroupType(GroupType groupType);

	public abstract String getDisplayName();

	public abstract void setDisplayName(String displayName);

	//public abstract Iterator<IPlayer> getMembers();

	public abstract void setMembers(List<IPlayer> members);

	//
	//  public void addMember(Key<Player> m)
	//  {
	//	  members.add(m);
	//  }

	public abstract String getGroupInfo();

	public abstract void setGroupInfo(String groupInfo);
	
	public abstract void add(IPlayer p);
	
	public abstract String getSnakeCaseDisplayName();
	
}