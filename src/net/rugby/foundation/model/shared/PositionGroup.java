package net.rugby.foundation.model.shared;

import com.googlecode.objectify.annotation.Subclass;

@Subclass
public class PositionGroup extends Group {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PositionGroup() {
		setGroupType(GroupType.POSITION);
	}

//	public PositionGroup(Long id, GroupType type, String displayName, ArrayList<Key<Player>> m) {
//		super(id, type, displayName, m);
//		position = net.rugby.foundation.model.shared.Position.position.NONE;
//		assert type == GroupType.POSITION;  //should just use instanceof
//	}

	private Position.position position;

	public Position.position getPosition() {
		return position;
	}

	public void setPosition(Position.position position) {
		this.position = position;
	}
	
	  @Override
	  public String getGroupInfo() {
			return groupInfo;
	  }
}
