/**
 * 
 */
package net.rugby.foundation.model.shared;

import net.rugby.foundation.admin.shared.OrchestrationConfiguration;
import net.rugby.foundation.admin.shared.WorkflowConfiguration;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author home
 *
 */
public class DataStoreFactory {

	private static Objectify ofy = null;
	
	public static Objectify getOfy() {
		if (ofy == null) {
			ofy = ObjectifyService.begin();
			
			ObjectifyService.register(Player.class);
			ObjectifyService.register(Group.class);		
			ObjectifyService.register(PositionEnUs.class);
			ObjectifyService.register(Position.class);
			ObjectifyService.register(PositionGroup.class);
			ObjectifyService.register(MatchGroup.class);
			ObjectifyService.register(TeamGroup.class);
			ObjectifyService.register(AppUser.class);
			ObjectifyService.register(MyGroup.class);
			ObjectifyService.register(MatchRating.class);
			ObjectifyService.register(Content.class);
			ObjectifyService.register(Feature.class);
			ObjectifyService.register(Clubhouse.class);
			ObjectifyService.register(ClubhouseMembership.class);
			ObjectifyService.register(TeamMembership.class);
			ObjectifyService.register(Competition.class);
			ObjectifyService.register(Round.class);
			ObjectifyService.register(CompetitionTeam.class);
			ObjectifyService.register(WorkflowConfiguration.class);
			ObjectifyService.register(OrchestrationConfiguration.class);
			ObjectifyService.register(MatchResult.class);
			ObjectifyService.register(SimpleScoreMatchResult.class);
			ObjectifyService.register(CoreConfiguration.class);
		}
		
		return ofy;
	}
}
