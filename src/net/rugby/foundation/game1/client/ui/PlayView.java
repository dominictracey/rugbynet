package net.rugby.foundation.game1.client.ui;

import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface PlayView extends IsWidget {
  
	void setEntry(IEntry entry, ICompetition comp);

	void setPresenter(Presenter listener);

	public interface Presenter {

		/**
		 * @param entry
		 */
		void saveEntry(IEntry entry);

		/**
		 * @param entry
		 * @param currentRound
		 * @param m
		 * @param homeTeam
		 * @param parseInt
		 */
		void tiebreakerHomeScoreSet(IEntry entry, IRound currentRound,
				IMatchGroup m, ITeamGroup homeTeam, int parseInt);

		/**
		 * @param entry
		 * @param currentRound
		 * @param m
		 * @param visitingTeam
		 * @param parseInt
		 */
		void tiebreakerVisitScoreSet(IEntry entry, IRound currentRound,
				IMatchGroup m, ITeamGroup visitingTeam, int parseInt);

		/**
		 * @param e
		 */
		void entryClicked(IEntry e);

		/**
		 * 
		 */
		void newEntryClicked();
	}

	/**
	 * Call when user has logged out
	 */
	void clear();
}
