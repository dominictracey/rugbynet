/**
 * 
 */
package net.rugby.foundation.game1.server.BPM;

import net.rugby.foundation.admin.server.orchestration.IOrchestrationTargets;

/**
 * @author home
 *
 */
public class Game1OrchestrationTargets {

	public static enum Targets implements IOrchestrationTargets { 
		
		MATCH ("Match"), 
		COMP ("Comp"),
		ROUND ("Round"),
		ENTRY ("Entry"), 
		LEAGUE ("League"), 
		CLUBHOUSE ("Clubhouse"),
		CLM ("ClubhouseLeaguMap"), 
		MATCHENTRY ("MatchEntry"), 
		ROUNDENTRY ("RoundEntry");
		
		private String value;
		private final static String targetKey = "Target";
	
		private Targets(String value)
		{
			this.setValue(value);
		}
	
		public static final String getKey() {
			return targetKey;
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
