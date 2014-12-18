/**
 * 
 */
package net.rugby.foundation.admin.server;

import java.io.UnsupportedEncodingException;
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

/**
 * @author home
 *
 */
public class AdminEmailer {
	
    private Properties props = new Properties();
    private Session session = Session.getDefaultInstance(props, null);
    private String subject;
    private String message;
    
    public void send() {
	    try {
	        Message msg = new MimeMessage(session);
	        MimeMultipart mpart = new MimeMultipart();
	        MimeBodyPart bp = new MimeBodyPart();
	        bp.setContent("<html><body>" + message + "</body></html>", "text/html");
	        // add message body
	        mpart.addBodyPart(bp);
//	        msg.setFrom(new InternetAddress("info@rugby.net", "The Rugby Net"));
//	        msg.addRecipient(Message.RecipientType.TO,
//	                         new InternetAddress("info@rugby.net", "The Rugby Net"));
			msg.setFrom(new InternetAddress("dominic.tracey@gmail.com", "rugby.net "));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress("dominic.tracey@gmail.com", "Dominic Tracey"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress("tom.lovering@gmail.com", "Tom Lovering"));
			msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress("caleb.tracey@gmail.com", "Caleb Tracey"));
	        msg.addRecipient(Message.RecipientType.TO,
	        		new InternetAddress("info@rugby.net", "The Rugby Net"));
	        msg.setSubject(subject);
	        msg.setContent(mpart);
	        msg.saveChanges();
	        
	        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO,"Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());
	        
	        Transport.send(msg);
	
	    } catch (AddressException e) {
	    	e.printStackTrace();
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail");

			//e.printStackTrace();
		}
    }

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
