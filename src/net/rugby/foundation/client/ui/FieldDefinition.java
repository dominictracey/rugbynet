package net.rugby.foundation.client.ui;

import com.google.gwt.user.client.ui.Widget;

public abstract class FieldDefinition<T> {
    public abstract Widget render(T t);

    public abstract void clear();
    public abstract void bind(Widget w);
    public abstract T update(T t);
    public boolean isEmpty() {
      return true;
    }
  }
