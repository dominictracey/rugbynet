/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import net.rugby.foundation.admin.shared.IOrchestrationActions;
import net.rugby.foundation.game1.shared.IClubhouseLeagueMap;
import net.rugby.foundation.game1.shared.ILeague;
import net.rugby.foundation.game1.shared.IMatchEntry;
import net.rugby.foundation.game1.shared.IRoundEntry;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IClubhouse;
import net.rugby.foundation.model.shared.ICompetition;

/**
 * @author home
 *
 */
public class Game1OrchestrationActions {

	public static enum EntryActions implements IOrchestrationActions<IAppUser> { 

		ENTRY_SEND_ROUND_END_EMAIL ("Email round end"),
		ENTRY_SEND_REMINDER_EMAIL ("Email reminder");

		private String value;
		private final static String actionKey = "Action";

		private EntryActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}

	public static enum CompActions implements IOrchestrationActions<ICompetition> { 
		GAME_COMPLETE ("Game1 Instance Complete");

		private String value;
		private final static String actionKey = "Action";

		private CompActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}

	public static enum ClubhouseLeagueMapActions implements IOrchestrationActions<IClubhouseLeagueMap> { 
		CREATE_LEADERBOARD ("Create Leaderboard"), FIX ("Fix CLM");

		private String value;
		private final static String actionKey = "Action";

		private ClubhouseLeagueMapActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}
	//	public static enum RoundActions implements IOrchestrationActions<IRound> { 
	//		
	//		
	//		private String value;
	//		private final static String actionKey = "Action";
	//	
	//		private RoundActions(String value)
	//		{
	//			this.setValue(value);
	//		}
	//	
	//		public static final String getKey() {
	//			return actionKey;
	//		}
	//	
	//		/* (non-Javadoc)
	//		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
	//		 */
	//		@Override
	//		public String getValue() {
	//			return value;
	//		}
	//	
	//		private void setValue(String value) {
	//			this.value = value;
	//		}
	//	}

	public static enum ClubhouseActions implements IOrchestrationActions<IClubhouse> { 
		FIX ("Fix Clubhouse");

		private String value;
		private final static String actionKey = "Action";

		private ClubhouseActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}

	public static enum LeagueActions implements IOrchestrationActions<ILeague> { 
		FIX ("Fix League");

		private String value;
		private final static String actionKey = "Action";

		private LeagueActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}

	public static enum MatchEntryActions implements IOrchestrationActions<IMatchEntry> { 
		DELETE ("Delete MatchEntry");

		private String value;
		private final static String actionKey = "Action";

		private MatchEntryActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}

	public static enum RoundEntryActions implements IOrchestrationActions<IRoundEntry> { 
		DELETEMATCHENTRY ("Delete MatchEntry");

		private String value;
		private final static String actionKey = "Action";

		private RoundEntryActions(String value)
		{
			this.setValue(value);
		}

		public static final String getKey() {
			return actionKey;
		}

		/* (non-Javadoc)
		 * @see net.rugby.foundation.admin.server.factory.IOrchestrationActions#getValue()
		 */
		@Override
		public String getValue() {
			return value;
		}

		private void setValue(String value) {
			this.value = value;
		}
	}	
}
