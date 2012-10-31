package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditPlayerEvent extends GwtEvent<EditPlayerEventHandler>{
  public static Type<EditPlayerEventHandler> TYPE = new Type<EditPlayerEventHandler>();
  private final Long id;
  
  public EditPlayerEvent(Long id) {
    this.id = id;
  }
  
  public Long getId() { return id; }
  
  @Override
  public Type<EditPlayerEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditPlayerEventHandler handler) {
    handler.onEditPlayer(this);
  }
}
