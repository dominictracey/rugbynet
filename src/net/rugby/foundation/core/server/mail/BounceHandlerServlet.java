package net.rugby.foundation.core.server.mail;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.servlet.http.*;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IAppUser.EmailStatus;

import com.google.appengine.api.mail.BounceNotification;
import com.google.appengine.api.mail.BounceNotificationParser;
import com.google.inject.Inject;

public class BounceHandlerServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7852096859230382165L;
	private IAppUserFactory auf;

	@Inject
	public BounceHandlerServlet(IAppUserFactory auf) {
		this.auf = auf;
	}
	
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
       try {
    	   BounceNotification bounce = BounceNotificationParser.parse(req);
	       // The following data is available in a BounceNotification object
	       // bounce.getOriginal().getFrom() 
	       // bounce.getOriginal().getTo() 
	       // bounce.getOriginal().getSubject() 
	       // bounce.getOriginal().getText() 
	       // bounce.getNotification().getFrom() 
	       // bounce.getNotification().getTo() 
	       // bounce.getNotification().getSubject() 
	       // bounce.getNotification().getText() 
	       
       		// find the AppUser and mark their email status as BOUNCE
       		auf.setEmail(bounce.getOriginal().getTo());
       		Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"bounce for " + bounce.getOriginal().getTo() + " details: " + bounce.getRawMessage());
       		IAppUser user = auf.get();
       		
       		if (user != null) {
       			user.setOptOut(true);
       			user.setEmailStatus(EmailStatus.BOUNCE);
       			auf.put(user);
       		}
	       
	   } catch (MessagingException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,e.getLocalizedMessage());
	   }
    }
}