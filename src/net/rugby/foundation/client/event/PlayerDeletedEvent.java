package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PlayerDeletedEvent extends GwtEvent<PlayerDeletedEventHandler>{
  public static Type<PlayerDeletedEventHandler> TYPE = new Type<PlayerDeletedEventHandler>();
  
  @Override
  public Type<PlayerDeletedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PlayerDeletedEventHandler handler) {
    handler.onPlayerDeleted(this);
  }
}
