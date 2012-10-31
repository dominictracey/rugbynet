package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditPlayerCancelledEvent extends GwtEvent<EditPlayerCancelledEventHandler>{
  public static Type<EditPlayerCancelledEventHandler> TYPE = new Type<EditPlayerCancelledEventHandler>();
  
  @Override
  public Type<EditPlayerCancelledEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditPlayerCancelledEventHandler handler) {
    handler.onEditPlayerCancelled(this);
  }
}
