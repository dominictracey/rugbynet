package net.rugby.foundation.client.event;

import net.rugby.foundation.client.place.Home.actions;

import com.google.gwt.event.shared.GwtEvent;

public class AccountActionCompleteEvent extends GwtEvent<AccountActionCompleteEventHandler>{
  public static Type<AccountActionCompleteEventHandler> TYPE = new Type<AccountActionCompleteEventHandler>();

  public actions action;
  
  public AccountActionCompleteEvent(actions action) {
	super();
	this.action = action;
}

public AccountActionCompleteEvent() {
	  
  }

@Override
protected void dispatch(AccountActionCompleteEventHandler handler) {
	handler.onDoneAccountAction(this);
	
}

@Override
public com.google.gwt.event.shared.GwtEvent.Type<AccountActionCompleteEventHandler> getAssociatedType() {
	return TYPE;
}
}
