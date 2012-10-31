/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

/**
 * @author home
 *
 */
public class AdminOrchestrationTargets {

	public static enum Targets implements IOrchestrationTargets { 
		
		MATCH ("Match"), 
		COMP ("Comp"),
		ROUND ("Round"),
		APPUSER ("App User");
		
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
