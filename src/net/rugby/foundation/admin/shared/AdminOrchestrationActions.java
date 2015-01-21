/**
 * 
 */
package net.rugby.foundation.admin.shared;

import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IMatchGroup;
import net.rugby.foundation.model.shared.IRatingQuery;

/**
 * @author home
 *
 */
public class AdminOrchestrationActions {

	public enum MatchActions implements IOrchestrationActions<IMatchGroup> { 

		LOCK ("Lock"), 
		FETCH ("Fetch"),
		MATCH_STALE_NEEDS_ATTENTION ("Stale, needs attention"), 
		MATCH_STALE_MARK_UNREPORTED ("Stale, mark unreported"), 
		UNLOCK ("Unlock"), FETCHSTATS ("Fetch Stats");

		private String value;
		private final static String actionKey = "Action";

		private MatchActions(String value)
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

	public enum CompActions implements IOrchestrationActions<ICompetition> { 

		UPDATENEXTANDPREV ("Round complete"), 
		COMP_COMPLETE ("CompComplete");

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

	public enum RatingActions implements IOrchestrationActions<IRatingQuery> { 

		GENERATE ("Generate Ratings"),
		CLEANUP ("Clean Up"), 
		RERUN ("Rerun");

		private String value;
		private final static String actionKey = "Action";

		private RatingActions(String value)
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


	public enum SeriesActions implements IOrchestrationActions<ISeriesConfiguration> { 

		PROCESS ("Process Series"),
		CLEANUP ("Clean Up");

		private String value;
		private final static String actionKey = "Action";

		private SeriesActions(String value)
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
