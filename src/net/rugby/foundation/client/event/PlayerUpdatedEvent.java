package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;
import net.rugby.foundation.model.shared.Player;

public class PlayerUpdatedEvent extends GwtEvent<PlayerUpdatedEventHandler>{
  public static Type<PlayerUpdatedEventHandler> TYPE = new Type<PlayerUpdatedEventHandler>();
  private final Player updatedPlayer;
  
  public PlayerUpdatedEvent(Player updatedPlayer) {
    this.updatedPlayer = updatedPlayer;
  }
  
  public Player getUpdatedPlayer() { return updatedPlayer; }
  

  @Override
  public Type<PlayerUpdatedEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PlayerUpdatedEventHandler handler) {
    handler.onPlayerUpdated(this);
  }
}
