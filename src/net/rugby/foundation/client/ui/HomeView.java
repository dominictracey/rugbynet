package net.rugby.foundation.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 */
public interface HomeView<T> extends IsWidget
{

	void setPresenter(Presenter<T> listener);
	void setUser(T loggedInUser);
	void clear();
	void showDraftButton(boolean show);
	void showRandomButton(boolean show);
	void showRoundButton(boolean show);
	void showSignInButton(boolean show);
	void showSignUpButton(boolean show);
	void showNoLogin(boolean show);
	void showNoDraft(boolean show);
	void showNoRoundSelection(boolean show);
	void showComplete(boolean show);
	void showCreateLeague(boolean show);
	
	public interface Presenter<T>
	{
		void goTo(Place place);
		void onDraftButtonClicked();
		void onRandomButtonClicked();
		void onSignUpButtonClicked();
		void onSignInButtonClicked();
		void onUniversalSportsClicked();
		void onRoundButtonClicked();
		void onLoginComplete();
		void onCreateLeagueClicked();
		void onCreateLeagueFinished();
		void onReuseButtonClicked();
	}

	void setNoDraftContent(String content);
	void setNoRoundContent(String content);
	void setCompleteContent(String content);
	void setNoLoginContent(String content);
	void showReuse(boolean reuse);

	
}