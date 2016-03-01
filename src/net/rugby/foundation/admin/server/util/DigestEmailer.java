/**
 * 
 */
package net.rugby.foundation.admin.server.util;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;

import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.IServerPlace;

/**
 * @author home
 *
 */
public class DigestEmailer {
	
    
    // addressing
    private IAppUser recip;
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

	private boolean configured = false;

	private ICompetitionFactory cf;
	private IDigestEmail de;
    
	@Inject
	public DigestEmailer(ICompetitionFactory cf) {
		this.cf = cf;
	}
	


	public boolean configure(IDigestEmail de, IAppUser recip) {
		
		if (!configured ) {
			this.de = de;
			this.recip = recip;
			configured = true;
		}
		return true;
	}
	
	public void send(ICoreConfiguration cc) {
		try {
			assert (configured);
			
			// **************************
			// *** ONLY IN PROD  *******
			// **************************
			if (cc != null && (cc.getEnvironment() == Environment.PROD || cc.getEnvironment() == Environment.LOCAL)) {
		        Message msg = new MimeMessage(session);
		        MimeMultipart mpart = new MimeMultipart();
		        MimeBodyPart bp = new MimeBodyPart();
		        
		        String content = buildMessage();
		        
		        bp.setContent(content, "text/html");
		        // add message body
		        mpart.addBodyPart(bp);
		        msg.setFrom(new InternetAddress("info@rugby.net", "The Rugby Net"));
		        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recip.getEmailAddress(), recip.getNickname()));
	
		        msg.setSubject(de.getSubject());
		        msg.setContent(mpart);
		        msg.saveChanges();
		        
		        Transport.send(msg);
		        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());
		        
			}
	
	    } catch (AddressException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 1 " + e.getLocalizedMessage());
	    } catch (MessagingException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 2 " + e.getLocalizedMessage());
	    } catch (UnsupportedEncodingException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 3 " + e.getLocalizedMessage());
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 4 " + e.getLocalizedMessage());
		}
	}

	/**
	 * 
	 * @return HTML email 
	 */
	public String buildMessage() {

		assert (configured);
		
		String email = de.getPart1();	

		String unsubLink = buildUnsubLink();
		String blurbText = buildBlurbs();
		
		
		
		email += de.getTitle() + de.getPart2() + blurbText + de.getPart6() + unsubLink + de.getPart7();
		
		configured = false;
		
		return email;
	}



	private String buildBlurbs() {
		assert (de != null && de.getBlurbs() != null);
	
		String retval = "";
		for (IBlurb b : de.getBlurbs()) {
			if (recip == null) {
				retval += de.getFormattedBlurbMap().get(b.getId());
			} else {
				// does the user care about this comp?
				ICompetition c = cf.get(b.getServerPlace().getCompId());
				if (recip.getCompList() == null || recip.getCompList().isEmpty() || recip.getCompList().contains(c.getCompType())) {
					retval += de.getFormattedBlurbMap().get(b.getId());
				}
			}
		}
		return retval;
	}

	private String buildUnsubLink() {
		try {
			String email = null;
			if (recip != null) { 
				email = recip.getEmailAddress();
			} else {
				email = "test@example.com";
			}
			return "<a href=\"http://www.rugby.net/email/unsubscribe?email=" + URLEncoder.encode(email, "UTF-8") + "\" style=\"text-decoration:underline;color:#999\" target=\"_blank\">Unsubscribe</a> from this email.";
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error building unsubscribe link" + e.getLocalizedMessage());
			return null;
		}
	}

}
