package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.shared.IPlayerMatchInfo;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 */
public interface PlayerListView<T> extends IsWidget
{


	void setPlayers(List<T> result);
	void setPresenter(Presenter<T> listener);
	void setListener(Listener<T> listener);
	void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions);
	void setGroupInfo(String name);
	void setButtonCaptions(String name1, String name2, String name3, String name4);
	void showDraftAnalysisLink(boolean show);
	void showEditGroupInfoLink(boolean show);
	
	public interface Presenter<T>
	{
		void goTo(Place place);
		void onButton1Clicked();
		void onButton2Clicked();
		void onButton3Clicked();
		void onButton4Clicked();
		boolean onItemSelected(T player);
		void onItemClicked(T player);
		void onSaveGroupInfoClicked(String info);
		void showDraftAnalysis();
	}
	
	public interface Listener<T>
	{
		boolean onItemSelected(T player);
		boolean isSelected(T player);
		void showEditPlayer(T player);
		void showEditStats(T player);
		void showEditRating(T player);
	}

	void setColumnHeaders(ArrayList<String> headers);



}