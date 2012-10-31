package net.rugby.foundation.client.ui;

import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.place.Manage.step;
import net.rugby.foundation.model.shared.Stage.stageType;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface ManageView<T> extends IsWidget
{

	void setPresenter(Presenter<T> listener);

	void setClientFactory(ClientFactory clientFactory);
	void setData(T info);
	void clear();
	void showDoneButton(boolean show);
	void showPrevButton(boolean show);
	void showNextButton(boolean show);
	void showPoints(boolean show);
	void showWait();
	
	public interface Presenter<T>
	{
		void goTo(Place place);
		void onPropClicked();
		void onHookerClicked();
		void onLockClicked();
		void onFlankerClicked();
		void onNumber8Clicked();
		void onScrumhalfClicked();
		void onFlyhalfClicked();
		void onCenterClicked();
		void onWingClicked();
		void onFullbackClicked();
		void onPreviousButtonClicked();
		void onNextButtonClicked();
		void onDoneButtonClicked();
		step getStep();
		stageType getStageType();
		int getRound();
		
	}






	
}