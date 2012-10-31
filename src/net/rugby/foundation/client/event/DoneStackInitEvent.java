package net.rugby.foundation.client.event;

import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.event.shared.GwtEvent;

public class DoneStackInitEvent extends GwtEvent<DoneStackInitEventHandler>{
  public static Type<DoneStackInitEventHandler> TYPE = new Type<DoneStackInitEventHandler>();
  private final GroupType groupType;
  
  public DoneStackInitEvent(GroupType groupType) {
	  this.groupType = groupType;
  }

  @Override
  public Type<DoneStackInitEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DoneStackInitEventHandler handler) {
    handler.onDoneStackInit(this);
  }

	public GroupType getGroupType() {
		return groupType;
	}
}
