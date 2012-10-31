package net.rugby.foundation.game1.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.game1.shared.IMatchStats;
import net.rugby.foundation.model.shared.IClubhouse;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface MatchStatsView extends IsWidget {

	void setPresenter(Presenter listener);

	public interface Presenter {
		void getMatchStats(Long matchId, AsyncCallback<List<IMatchStats>> cb);
	}

	/**
	 * @param stats - this is a list to be compiled to support sharding
	 */
	 void setData(List<IMatchStats> stats);

}
