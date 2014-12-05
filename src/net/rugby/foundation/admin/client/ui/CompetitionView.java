package net.rugby.foundation.admin.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICompetition.CompetitionType;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IMatchResult;
import net.rugby.foundation.model.shared.IPlayerRating;
import net.rugby.foundation.model.shared.IRound;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * View base interface.
 * Extends IsWidget so a view impl can easily provide its container widget.
 */
public interface CompetitionView extends IsWidget {

	enum Step { TEAMS, MATCHES, ROUNDS, COMP };
	
	void setPresenter(Presenter listener);
	void showTeams(Map<String, ITeamGroup> teams);
	void showRounds(List<IRound>  result);
	void showMatches(Map<String, IMatchGroup> matches);
	void showCompetition(ICompetition result);
	void showStatus(String message);
	
	public interface Presenter {
		void fetchCompetitionClicked(List<IRound> roundMap, CompetitionType compType);
		void fetchTeamsClicked(String text, CompetitionType compType);
		void fetchMatchesClicked(Map<String,ITeamGroup> teams, CompetitionType compType);
		void fetchRoundsClicked(Map<String, IMatchGroup> matches, CompetitionType compType);

		void saveCompetitionClicked(ICompetition comp, Map<String,ITeamGroup> teams);

		/**
		 * @param editRound 
		 * @param compId
		 * @param roundId
		 */
		void roundClicked(EditRound editRound, Long parseLong, Long parseLong2);

		/**
		 * @param editTeam
		 * @param parseLong
		 */
		void editTeamInit(EditTeam editTeam, long teamId, long compId);
		/**
		 * 
		 */
		void createAdminClicked();
		/**
		 * 
		 */
		void sanityCheckClicked();
		/**
		 * @param compId - they want to edit the comp
		 */
		void compClicked(EditComp editComp, long compId);
		/**
		 * @param editMatch
		 * @param editMatchStats 
		 * @param matchId
		 */
		void editMatchInit(EditMatch editMatch, PlayerListView<IPlayerRating> editMatchStats, long matchId, long roundId, long compId);
		
		void virtualCompClicked();

	}

	/**
	 * @param base
	 * @param result
	 */
	void addComps(List<ICompetition> result);
	/**
	 * @param compId
	 * @param result
	 */
	void addRounds(ICompetition comp, List<IRound> result);
	/**
	 * @param compId
	 * @param result
	 */
	void addTeams(Long compId, List<ITeamGroup> result);

	/**
	 * @param compId
	 * @param roundId
	 * @param matchId
	 * @param result
	 */
	void addResults(Long compId, IRound round, IMatchGroup match,
			List<IMatchResult> result);
	/**
	 * @param compId
	 * @param roundId
	 * @param result
	 */
	void addRound(Long compId, Long roundId, List<IMatchGroup> result);
	void setClientFactory(ClientFactory clientFactory);
	PlayerListView<IPlayerRating> getPlayerListView();
	boolean isAllSetup();
	void setInitialized(boolean b);
	void showWait(boolean show);
	void setSchemaList(List<ScrumMatchRatingEngineSchema> result);

	SmartBar getSmartBar();

}
