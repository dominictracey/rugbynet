package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface EditPlayerEventHandler extends EventHandler {
  void onEditPlayer(EditPlayerEvent event);
}
