package net.rugby.foundation.engine.server;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withUrl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.model.shared.PlayerRating;
import net.rugby.foundation.model.shared.PlayerRating.RatingComponent;

import org.joda.time.DateTime;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Query;

/**
 * Flushes out the PlayerRating table details to reduce datastore overhead
 *  
 * @author Dominic Tracey
 *
 * </p>
 */

@Singleton
public class CleanupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private IPlayerRatingFactory prf;
	private IConfigurationFactory ccf;

	public enum Actions {TOUCH, SHRINK}
	@Inject
	public CleanupServlet(IPlayerRatingFactory prf, IConfigurationFactory ccf) {
		this.ccf = ccf;
		this.prf = prf;
	}


	public static final long LIMIT_MILLIS = 1000 * 25; // provide a little leeway

	
	/** URL Format
	/*		/cleanup/
	 * 
	 **/ 
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			long startTime = System.currentTimeMillis();
			int count = 0;
			
		    Objectify ofy = ObjectifyService.begin();
		    Query<PlayerRating> query = ofy.query(PlayerRating.class);

		    String cursorStr = req.getParameter("cursor");
		    if (cursorStr != null)
		        query.startCursor(Cursor.fromWebSafeString(cursorStr));

		    String action = req.getParameter("action");
		    String clazz = req.getParameter("clazz");
		    
		    assert (clazz.contains("PlayerRating"));
		    
		    QueryResultIterator<PlayerRating> iterator = query.iterator();
		    while (iterator.hasNext()) {
		        PlayerRating pr = iterator.next();

		        // process PlayerRating
		        if (action.equals(Actions.TOUCH.name())) {
		        	ofy.put(pr);
		        	
		        } else if (action.equals(Actions.SHRINK.name())) {
		        	
		        	pr.setDetails(null);
					for (RatingComponent rc : pr.getRatingComponents()) {
						rc.setRatingDetails(null);
						rc.setStatsDetails(null);
					}
					ofy.put(pr);
		        }
		        
		        
						
		        if (System.currentTimeMillis() - startTime > LIMIT_MILLIS) {
		        	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Cleanup servlet reached limit after processing " + count + " records in " + action + " mode. Servlet will continue for " + query.count() + " remaining instances."); 
		            Cursor cursor = iterator.getCursor();
		            Queue queue = QueueFactory.getDefaultQueue();
		            
		            queue.add(withUrl(ccf.get().getEngineUrl() + "/cleanUp").param("cursor", cursor.toWebSafeString()).param("action", action).param("clazz", clazz));
		            break;
		        }
		        
		        count++;
		    }
		} catch (Throwable caught) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,caught.getLocalizedMessage(),caught);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}



}
