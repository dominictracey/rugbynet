package net.rugby.foundation.admin.client.ui;

import com.google.gwt.user.client.ui.Widget;

public abstract class FieldDefinition<T> {
    protected Widget w;
	public abstract Widget render(T t);

    public abstract void clear();
    public void bind(Widget w) { this.w = w; }
    public abstract T update(T t);
    public boolean isEmpty() {
      return true;
    }
    public Widget getWidget() {
    	return w;
    }
    
  }
