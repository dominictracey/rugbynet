package net.rugby.foundation.topten.client.ui;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Widget;

public abstract class ColumnDefinition<T> {
    public abstract Widget render(T t);

    public boolean isClickable() {
      return false;
    }

    public boolean isSelectable() {
      return false;
    }
    
    public void clear() {
    	
    }

	public abstract Column<T, ?> getColumn(); 
  }
