package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddPlayerEventHandler extends EventHandler {
  void onAddPlayer(AddPlayerEvent event);
}
