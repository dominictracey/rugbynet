package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface EditPlayerCancelledEventHandler extends EventHandler {
  void onEditPlayerCancelled(EditPlayerCancelledEvent event);
}
