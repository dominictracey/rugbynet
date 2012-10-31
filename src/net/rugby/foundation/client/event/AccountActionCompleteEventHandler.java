package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AccountActionCompleteEventHandler extends EventHandler{
  void onDoneAccountAction(AccountActionCompleteEvent event);
}
