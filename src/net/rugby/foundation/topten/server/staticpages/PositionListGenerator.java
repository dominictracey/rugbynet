package net.rugby.foundation.topten.server.staticpages;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IRatingSeriesFactory;
import net.rugby.foundation.model.shared.Criteria;
import net.rugby.foundation.model.shared.IRatingGroup;
import net.rugby.foundation.model.shared.IRatingMatrix;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingSeries;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

public class PositionListGenerator {
	
	private IRatingSeriesFactory rsf;
	private ITopTenListFactory ttlf;

	@Inject
	public PositionListGenerator(IRatingSeriesFactory rsf, ITopTenListFactory ttlf) {
		this.rsf = rsf;
		this.ttlf = ttlf;
	}
	
	public String getForComp(Long compId) {
		String retval =	"	<div class='row'> " +
"		<div class='col-xs-12 col-sm-4'> " +
"		<div class='showby1'> " +
"			<div class='showby-heading'>" +
"				<div class='showby-text col-md-9 col-sm-10'>POSITION</div>" +
"			</div>" +
"				<div class=''>" +
"					<div class='list-group'>";
		
		// first see if we have an in form position series for the comp
		IRatingSeries rs = rsf.get(compId, RatingMode.BY_POSITION);
		if (rs == null) {
			return "<!-- no position series found for compId " + compId + " -->";
		}
		
		// and the latest RatingGroup
		if (rs.getRatingGroups() == null || rs.getRatingGroups().size() == 0) {
			return "<!-- no rating groups found for position series of compId " + compId + " -->";
		}
		IRatingGroup rg = rs.getRatingGroups().get(rs.getRatingGroups().size()-1);
		
		// and the in form list is preferred
		if (rg.getRatingMatrices() == null || rg.getRatingMatrices().size() == 0) {
			return "<!-- no rating matrices found for position series of compId " + compId + " -->";
		}
		
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
		
		for (IRatingQuery rq : rm.getRatingQueries()) {
			String url = "/s/" + ttlf.get(rq.getTopTenListId()).getGuid();
			retval += "<a href='" + url + "' class='list-group-item '>" +
"			<img class='position position-" + rq.getLabel().replaceAll("\\s","").toLowerCase() + "' alt='' src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAJYAAACWAQMAAAAGz+OhAAAAA1BMVEX///+nxBvIAAAAAXRSTlMAQObYZgAAABpJREFUeNrtwTEBAAAAwqD1T20ND6AAAIB3Awu4AAFSPGZ8AAAAAElFTkSuQmCC'>" +
"	   		<span class='list-group-item-heading position-text'>" + rq.getLabel() + "</span>" +
"	    </a>";
		}
		
		retval += "					</div>" +		
"		</div>" +
"	</div>" +
"</div>";
		
		return retval;

	}

}
