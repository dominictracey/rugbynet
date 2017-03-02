package net.rugby.foundation.topten.server.staticpages;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRatingGroupFactory;
import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class CompetitionListGenerator {

	private IRatingSeriesFactory rsf;
	private ITopTenListFactory ttlf;
	private IConfigurationFactory ccf;
	private ICompetitionFactory cf;
	private IRatingGroupFactory rgf;

	@Inject
	public CompetitionListGenerator(IRatingSeriesFactory rsf, ITopTenListFactory ttlf, IConfigurationFactory ccf, ICompetitionFactory cf, IRatingGroupFactory rgf) {
		this.rsf = rsf;
		this.ttlf = ttlf;
		this.ccf = ccf;
		this.cf = cf;
		this.rgf = rgf;
	}

	public String get(Boolean comps, Boolean regions) {
		String title = comps ? "COMPETITION" : "REGION";
		String retval =	"		<div class='col-xs-12 col-sm-4'> " +
				"		<div class='showby1'> " +
				"			<div class='showby-heading'>" +
				"				<div class='showby-text col-md-9 col-sm-10'>" + title + "</div>" +
				"			</div>" +
				"				<div class=''>" +
				"					<div class='list-group'>";

		for (Long compId : ccf.get().getCompsForClient()) {

			ICompetition comp = cf.get(compId);

			if ((comps && comp.getRoundIds().size() != 0) || (regions && comp.getRoundIds().size() == 0)) {
				String compName = ccf.get().getCompetitionMap().get(compId);
				// first see if we have an in form position series for the comp
				IRatingSeries rs = rsf.get(compId, RatingMode.BY_POSITION);
				if (rs == null) {
					Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "<!-- no position series found for compId " + compId + " -->");
				} else {

					// and the latest RatingGroup
					if (rs.getRatingGroupIds() == null || rs.getRatingGroupIds().size() == 0) {
						Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "<!-- no rating groups found for position series of compId " + compId + " -->");
					} else {
						IRatingGroup rg = rgf.get(rs.getRatingGroupIds().get(0)); // first is most recent
	
						// and the in form list is preferred
						if (rg.getRatingMatrices() == null || rg.getRatingMatrices().size() == 0) {
							Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "<!-- no rating matrices found for position series of compId " + compId + " -->");
						} else {
	
							IRatingMatrix rm = null;
	
							for (IRatingMatrix rmt : rg.getRatingMatrices()) {
								if (rmt.getCriteria().equals(Criteria.IN_FORM)) {
									rm = rmt;
									break;
								}
							}						
			
							// if we don't have in form, just use the first
							if (rm == null) {
								rm = rg.getRatingMatrices().get(0);
							}
			
							if (rm != null) {
								IRatingQuery rq = rm.getRatingQueries().get(3);  // number 8s
								if (rq != null && rq.getTopTenListId() != null) {
									String url = "/s/" + ttlf.get(rq.getTopTenListId()).getGuid();
									retval += "<a href='" + url + "' class='list-group-item '>" +
											"			<img class='comps-" + comp.getAbbr().replaceAll("\\s","").toLowerCase() + "' alt='' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAACWAQMAAAAGz+OhAAAAA1BMVEX///+nxBvIAAAAAXRSTlMAQObYZgAAABpJREFUeNrtwTEBAAAAwqD1T20ND6AAAIB3Awu4AAFSPGZ8AAAAAElFTkSuQmCC'>" +
											"	   		<span class='list-group-item-heading position-text'>" + compName + "</span>" +
											"	    </a>";
								} else {
									Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "<!-- no rating query found for position series of compId " + compId + " -->");
								}
							} else {
								Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "<!-- no good rating matrix found for position series of compId " + compId + " -->");
							}
						}
					}
				}
			}
		}

		retval += "					</div>" +		
				"		</div>" +
				"	</div>" +
				"</div>";

		return retval;

	}

}
