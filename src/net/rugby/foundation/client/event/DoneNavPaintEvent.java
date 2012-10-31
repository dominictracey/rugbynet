package net.rugby.foundation.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class DoneNavPaintEvent extends GwtEvent<DoneNavPaintEventHandler>{
  public static Type<DoneNavPaintEventHandler> TYPE = new Type<DoneNavPaintEventHandler>();
  private final Widget navPanel;
  
  public DoneNavPaintEvent(Widget navpanel) {
	  navPanel = navpanel;
  }
  
  public Widget getNavPanel() { return navPanel; }
  

  @Override
  public Type<DoneNavPaintEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DoneNavPaintEventHandler handler) {
    handler.onDoneNavPaint(this);
  }
}
