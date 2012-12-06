package net.rugby.foundation.admin.server.workflow.matchrating;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.server.UrlCacher;
import net.rugby.foundation.core.server.factory.ICountryFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.Country;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.ScrumPlayer;

import com.google.appengine.tools.pipeline.FutureValue;
import com.google.appengine.tools.pipeline.Job4;
import com.google.appengine.tools.pipeline.Job5;
import com.google.appengine.tools.pipeline.Job6;
import com.google.appengine.tools.pipeline.NoSuchObjectException;
import com.google.appengine.tools.pipeline.OrphanedObjectException;
import com.google.appengine.tools.pipeline.PipelineService;
import com.google.appengine.tools.pipeline.PipelineServiceFactory;
import com.google.appengine.tools.pipeline.PromisedValue;
import com.google.appengine.tools.pipeline.Value;

public class FetchPlayerByScrumId extends Job5<IPlayer, /*IPlayerFactory,*/ ICompetition, String, String, Long, Long> {

	private static final long serialVersionUID = 483113213168220162L;
	private IPlayerFactory pf;
	private ICountryFactory cf;

	//@Inject
	public FetchPlayerByScrumId(IPlayerFactory pf, ICountryFactory cf) {
		this.pf = pf;
		this.cf = cf;
	}

//	@SuppressWarnings("serial")
//	class DiffJob extends Job2<Integer, Integer, Integer> {
//		@Override
//		public Value<Integer> run(Integer a, Integer b) {
//			return immediate(a - b);
//		}
//	}
//
//	@SuppressWarnings("serial")
//	class MultJob extends Job2<Integer, Integer, Integer> {
//		@Override
//		public Value<Integer> run(Integer a, Integer b) {
//			return immediate(a*b);
//		}
//	}
	/**
	 * return IPlayer reference
	 * params String compName
	 * 			Long scrumId
	 * 			Long adminID
	 */		
	@Override
	public Value<IPlayer> run(/*IPlayerFactory pf,*/ ICompetition comp, String playerName, String referringURL, Long scrumPlayerId, Long adminId) {

		// first see if we have it in the database
		IPlayer player = pf.getByScrumId(scrumPlayerId);
		//player.setDisplayName("Hugo Southwell");
		
		if (player != null) {
			return immediate(player);
		} else { // didn't find, so go looking
			player = getPlayerFromScrum(pf, comp, scrumPlayerId);
			
			if (player != null) {
				return immediate(player);
			} else {
				//still didn't find, need human to get this going.
				PromisedValue<IPlayer> x = newPromise(IPlayer.class);

				Logger.getLogger("Scrum.com Player Parser").log(Level.INFO, "http://localhost:8888/Admin.html?gwt.codesvr=127.0.0.1:9997#HandleEmailPlace:promisedHandle=" + x.getHandle() + "&name=" + URLEncoder.encode(playerName)+ "&referringURL=" + URLEncoder.encode(referringURL));
				//SendEmail(x.getHandle());
				//FutureValue<IPlayer> p = futureCall(new FetchPlayerManually(pf), immediate(comp),immediate(playerName),immediate(referringURL),immediate(scrumPlayerId), immediate(adminId));
				return futureCall(new FetchPlayerManually(pf), x);
			}
//			DiffJob diffJob = new DiffJob();
//			MultJob multJob = new MultJob();
//			FutureValue<Integer> r = futureCall(diffJob, immediate(x), immediate(y));
//			FutureValue<Integer> s = futureCall(diffJob, immediate(x), immediate(z));
//			FutureValue<Integer> t = futureCall(multJob, r, s);
//			FutureValue<Integer> u = futureCall(diffJob, t, immediate(2));
//			return u;

		}
	}
	

	private IPlayer getPlayerFromScrum(IPlayerFactory pf, ICompetition comp, Long scrumPlayerId)  {

		IPlayer player = pf.getById(null);  //empty
		
		String playerURL = "http://www.espnscrum.com/scrum" + "/rugby/player/" + scrumPlayerId + ".html";
		
		Date dateRead = null;
        boolean found = false;
        
        try {
//            URL url = new URL(resultURL);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        	UrlCacher urlCache = new UrlCacher(playerURL);
        	List<String> lines = urlCache.get();
            String line;

            if (lines == null) {
            	return null;
            }
            
            Iterator<String> it = lines.iterator();
            while (it.hasNext() && !found) {
            	
            	line = it.next();
            	// first we scan to the right date
            	if (line.contains("scrumPlayerName")) {
            		player.setDisplayName(line.split("<|>")[2].trim());
            	} else if (line.contains("scrumPlayerCountry")) {
            		player.setCountry(cf.getByName(line.split("<|>")[2].trim()));
            	} else if (line.contains("Born")) {
            		line = it.next();
            		if (line != null && !line.contains(",")) {
            			line = it.next();
            		}
        			if (!line.contains(",")) {
        				break;
        			} else {
        				//String month = line.split(" |,")[0].trim();
        				String monthday = line.split(",")[0].trim();
        				String year = line.split(",")[1].trim();
        				DateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
	            		dateRead = dateFormatter.parse(monthday + ", " + year);
	            		if (dateRead != null) {
	            			player.setBirthDate(dateRead);
	            		}
            		} 
            	} else if (line.contains("All Tests")) {
           			line = it.next();
           			line = it.next();
           			//line = it.next();
            		player.setNumCaps(Integer.parseInt(line.split("<|>")[2].trim()));
            		found = true;
            	}
            }
        } catch (ParseException e) {
            Logger.getLogger("Scrum.com").log(Level.SEVERE, e.getMessage());
            return null;
        }
		
        if (found)
        	return player;
        else
        	return null;
	}
	
	private void SendEmail(String promiseHandle) {
		PipelineService service = PipelineServiceFactory.newPipelineService();
		try {
			service.submitPromisedValue(promiseHandle, pf.getByScrumId(14505L));
		} catch (NoSuchObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OrphanedObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
