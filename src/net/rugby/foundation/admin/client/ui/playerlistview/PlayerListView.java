package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 */
public interface PlayerListView<T> extends IsWidget
{

	void setListener(Listener<T> listener);
	void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions);
	
	public interface Listener<T>
	{
		boolean onItemSelected(T player);
		boolean isSelected(T player);
		void showEditPlayer(T player);
		void showEditStats(T player);
		void showEditRating(T player);
		void showEditTeamStats(T player);

	}

	void setColumnHeaders(ArrayList<String> headers);
	void showWait();

	void updatePlayerMatchStats(T newPmi);
	void setPlayers(List<T> PlayerList, IMatchGroup match);
	void clear();


}