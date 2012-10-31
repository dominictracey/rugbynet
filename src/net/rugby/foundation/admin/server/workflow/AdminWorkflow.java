/**
 * 
 */
package net.rugby.foundation.admin.server.workflow;

/**
 * @author home
 *
 */
public class AdminWorkflow {

	public static String SecondaryKey = "SecondaryKey";
	//public static enum TargetType { IMatchGroup, ICompetition; }
	public final static String TargetType = "TargetType";
	public final static String Key = "Key";
	
	public static enum Targets implements IWorkflowTargets { 
		
		MATCH ("Match"), 
		COMP ("Comp");
//		ROUND ("Round"),
//		APPUSER ("App User");
		
		private String value;
		
	
		private Targets(String value)
		{
			this.setValue(value);
		}
	
		public static final String getKey() {
			return TargetType;
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
