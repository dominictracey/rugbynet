package net.rugby.foundation.topten.client.ui;

public interface RatingPopupView<T> {

	public abstract void setRating(T rating);

	void setContent(String body);

}