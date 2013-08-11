package net.rugby.foundation.core.client;

import com.google.gwt.event.dom.client.ClickHandler;

public abstract class ClickHandlerData<T> implements ClickHandler {

	private T data;
	
	public ClickHandlerData(T data) {
		this.setData(data);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
