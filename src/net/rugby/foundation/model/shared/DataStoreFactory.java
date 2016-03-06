/**
 * 
 */
package net.rugby.foundation.model.shared;

import net.rugby.foundation.admin.shared.AdminTask;
import net.rugby.foundation.admin.shared.Blurb;
import net.rugby.foundation.admin.shared.DigestEmail;
import net.rugby.foundation.admin.shared.EditPlayerAdminTask;
import net.rugby.foundation.admin.shared.EditPlayerMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.EditTeamMatchStatsAdminTask;
import net.rugby.foundation.admin.shared.OrchestrationConfiguration;
import net.rugby.foundation.admin.shared.WorkflowConfiguration;
import net.rugby.foundation.admin.shared.seriesconfig.BaseSeriesConfiguration;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;
import net.rugby.foundation.model.shared.fantasy.MyGroup;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.model.shared.NoteRef;
import net.rugby.foundation.topten.model.shared.TopTenList;
import net.rugby.foundation.topten.model.shared.TopTenItem;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author home
 *
 */
public class DataStoreFactory {

	private static Objectify ofy = null;
	private static Objectify transaction = null;
	
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
//			ObjectifyService.register(Feature.class);
			ObjectifyService.register(Clubhouse.class);
			ObjectifyService.register(ClubhouseMembership.class);
			ObjectifyService.register(TeamMembership.class);
			ObjectifyService.register(Competition.class);
			ObjectifyService.register(Round.class);
			//ObjectifyService.register(CompetitionTeam.class);
			ObjectifyService.register(WorkflowConfiguration.class);
			ObjectifyService.register(OrchestrationConfiguration.class);
			ObjectifyService.register(MatchResult.class);
			ObjectifyService.register(SimpleScoreMatchResult.class);
			ObjectifyService.register(CoreConfiguration.class);
			ObjectifyService.register(Country.class);
			ObjectifyService.register(ScrumPlayer.class);
			ObjectifyService.register(ScrumPlayerMatchStats.class);
			ObjectifyService.register(ScrumPlayerMatchStatTimeLog.class);
			ObjectifyService.register(ScrumTeamMatchStats.class);
			ObjectifyService.register(PlayerRating.class);
			
			// these should be split off into another DAO manager thingie
			ObjectifyService.register(AdminTask.class);
			ObjectifyService.register(EditPlayerAdminTask.class);
			ObjectifyService.register(EditPlayerMatchStatsAdminTask.class);
			ObjectifyService.register(EditTeamMatchStatsAdminTask.class);
			ObjectifyService.register(ScrumMatchRatingEngineSchema.class);
			ObjectifyService.register(ScrumMatchRatingEngineSchema20130713.class);
			ObjectifyService.register(BaseSeriesConfiguration.class);
			ObjectifyService.register(Blurb.class);
			ObjectifyService.register(DigestEmail.class);
			
			//topten
			ObjectifyService.register(TopTenList.class);
			ObjectifyService.register(TopTenItem.class);
			ObjectifyService.register(Standing.class);
			ObjectifyService.register(RatingQuery.class);
			ObjectifyService.register(Note.class);
			ObjectifyService.register(NoteRef.class);
			
			ObjectifyService.register(RatingComponent.class);
			ObjectifyService.register(RawScore.class);
			ObjectifyService.register(RatingSeries.class);
			ObjectifyService.register(RatingMatrix.class);
			ObjectifyService.register(RatingGroup.class);
			
			ObjectifyService.register(ServerPlace.class);
			ObjectifyService.register(Sponsor.class);
			
			//ObjectifyService.register(Feature.class);
		}
		
		return ofy;
	}

	@SuppressWarnings("unused")
	public static Objectify startTransaction() {
		// make sure we are registered
		Objectify ofy = getOfy();
		transaction =  ObjectifyService.beginTransaction();
		return transaction;
	}
	
	public static Objectify getTransaction() {
		return transaction;
	}
}
