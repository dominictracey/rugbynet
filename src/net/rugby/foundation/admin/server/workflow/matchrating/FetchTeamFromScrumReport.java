package net.rugby.foundation.admin.server.workflow.matchrating;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.tools.pipeline.Job2;
import com.google.appengine.tools.pipeline.Value;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.admin.server.workflow.matchrating.GenerateMatchRatings.Home_or_Visitor;
import net.rugby.foundation.core.server.factory.ITeamGroupFactory;
import net.rugby.foundation.model.shared.ITeamGroup;

public class FetchTeamFromScrumReport extends Job2<ITeamGroup, Home_or_Visitor, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3101992931956737933L;
	private ITeamGroupFactory tf;

	public FetchTeamFromScrumReport(ITeamGroupFactory tf) {
		this.tf = tf;
	}

	@Override
	public Value<ITeamGroup> run(Home_or_Visitor home_or_visitor, String url) {
		boolean found = false;
		ITeamGroup team = null;

		UrlCacher urlCache = new UrlCacher(url);
		List<String> lines = urlCache.get();
		String line;
		String homeName = null;
		String visitName = null;

		if (lines == null) {
			return null;
		}

		Iterator<String> it = lines.iterator();
		while (it.hasNext() && !found) {

			line = it.next();
			// seems to have this tag around the 
			if (line.contains("liveSubNavText1")) {
				line = it.next();
				// New Zealand <span class="liveSubNavText2">(14)</span> 20 - 6 <span class="liveSubNavText2">(6)</span> Australia <span class="liveSubNavText2">(FT)</span>
				if (line.split("<").length > 0) {
					homeName = line.split("<")[0].trim();
				} else {
					homeName = null;
					found = false;
					break;
				}

				if (line.split("</span>").length > 1) {
					String endChunk = line.split("</span>")[2];
					if (endChunk.split("<").length > 1) {
						visitName = endChunk.split("<")[0].trim();
					}
				}
				
				if (homeName != null && visitName != null) {
					found = true;
				}
			}
		}

		if (home_or_visitor == Home_or_Visitor.HOME) {
			team = tf.getTeamByName(homeName);
		} else {
			team = tf.getTeamByName(visitName);           	
		}


		if (found)
			return immediate(team);
		else
			return null;
	}

}
