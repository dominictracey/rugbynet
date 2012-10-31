/**
 * 
 */
package net.rugby.foundation.game1.server;

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
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.server.PlayersServiceImpl;

/**
 * @author home
 *
 */
public class Game1Emailer {
	
    private Properties props = new Properties();
    private Session session = Session.getDefaultInstance(props, null);
    private String subject;
    private String message;
    private IAppUser user;
    
    public void send() {
	    try {
	        Message msg = new MimeMessage(session);
	        msg.setFrom(new InternetAddress("dominic.tracey@gmail.com", "rugby.net "));
	        msg.addRecipient(Message.RecipientType.TO,
	                         new InternetAddress(user.getEmailAddress(), user.getNickname()));
	        msg.setSubject(subject);
	        msg.setText(message);
	        Transport.send(msg);
	        Logger.getLogger(PlayersServiceImpl.class.getName()).log(Level.INFO,"Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());
	
	    } catch (AddressException e) {
	    	e.printStackTrace();
	    } catch (MessagingException e) {
	    	e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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

	public IAppUser getUser() {
		return user;
	}

	public void setUser(IAppUser user) {
		this.user = user;
	}
}
