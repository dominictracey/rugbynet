package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface PlayerUpdatedEventHandler extends EventHandler{
  void onPlayerUpdated(PlayerUpdatedEvent event);
}
