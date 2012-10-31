package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class AddPlayerEvent extends GwtEvent<AddPlayerEventHandler> {
  public static Type<AddPlayerEventHandler> TYPE = new Type<AddPlayerEventHandler>();
  
  @Override
  public Type<AddPlayerEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddPlayerEventHandler handler) {
    handler.onAddPlayer(this);
  }
}
