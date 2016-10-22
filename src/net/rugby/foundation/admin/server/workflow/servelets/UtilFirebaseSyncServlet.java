package net.rugby.foundation.admin.server.workflow.servelets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IRoundFactory;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.inject.Inject;
import com.google.inject.Singleton;

// /cron/weekendInit
@Singleton
public class UtilFirebaseSyncServlet extends HttpServlet {

	public static class User {

	    public String date_of_birth;
	    public String full_name;
	    public String nickname;

	    public User(String date_of_birth, String full_name) {
	        this.date_of_birth = date_of_birth;
	        this.full_name = full_name;
	    }

	    public User(String date_of_birth, String full_name, String nickname) {
	    	this.date_of_birth = date_of_birth;
	        this.full_name = full_name;
	        this.nickname = nickname;
	    }

	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2415381790205617886L;
	private IConfigurationFactory ccf;
	private IRoundFactory rf;
	private ICompetitionFactory cf;

	@Inject
	public UtilFirebaseSyncServlet(IConfigurationFactory ccf, IRoundFactory rf, ICompetitionFactory cf) {
		this.ccf = ccf;
		this.rf = rf;
		this.cf = cf;
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {		
			resp.setContentType("text/html");
			
			// hook up to Firebase
	        // Note: Ensure that the [PRIVATE_KEY_FILENAME].json has read
	        // permissions set.
	        FirebaseOptions options = new FirebaseOptions.Builder()
	            .setServiceAccount(getServletContext().getResourceAsStream("/WEB-INF/firebase-rugby-poc-6a6a9b2c92ff.json"))
	            .setDatabaseUrl("https://fir-rugby-poc.firebaseio.com/")
	            .build();

	        try {
	        	List<FirebaseApp> apps = FirebaseApp.getApps();
	        	
	            FirebaseApp.getInstance();
	        }
	        catch (Exception e){
	        	//resp.getWriter().print(e.getLocalizedMessage() + "\n" + e.getStackTrace());
	        	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,("Firebase doesn't exist..."));
	        }

	        try {
	            FirebaseApp.initializeApp(options);
	        }
	        catch(Exception e){
	        	//resp.getWriter().print(e.getLocalizedMessage() + "\n" + e.getStackTrace());
	        	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,("Firebase already exists..."));
	        }

	        // As an admin, the app has access to read and write all data, regardless of Security Rules
	        DatabaseReference ref = FirebaseDatabase
	                .getInstance()
	                .getReference("foundation/rugby");
	        
	        DatabaseReference compsRef = ref.child("comps");
	        
	        Map<String, ICompetition> comps = new HashMap<String, ICompetition>();

			ICoreConfiguration cc = ccf.get();
			for (Long cid: cc.getCompsUnderway()) {
				comps.put(cc.getCompetitionMap().get(cid), cf.get(cid));
				
			}
			
			compsRef.setValue(comps);
	        
			resp.getWriter().println("Successful update of Firebase");
		} catch (Throwable e) {
			resp.getWriter().print(e.getLocalizedMessage() + "\n" + e.getStackTrace());
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "error syncing firebase: ", e);
		}


	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req,resp);
	}
}
