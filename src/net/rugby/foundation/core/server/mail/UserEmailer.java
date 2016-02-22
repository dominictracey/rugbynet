/**
 * 
 */
package net.rugby.foundation.core.server.mail;

import java.io.FileInputStream;
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

import org.apache.commons.io.IOUtils;

import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IServerPlace;

/**
 * @author home
 *
 */
public class UserEmailer {
	
    private Properties props = new Properties();
    private Session session = Session.getDefaultInstance(props, null);
    private String subject;
    
    private String message;   
    // message parts
    private String part1;
    private String title;
    private String part2;
    private String linkText;
    private String linkTarget;
    private String part3;
    private String body;
    private String part4;
    private String unsubLink;
    private String part5;
    
    // addressing
    private IAppUser recip;
    
    
    public void send() {
	    try {
	        Message msg = new MimeMessage(session);
	        MimeMultipart mpart = new MimeMultipart();
	        MimeBodyPart bp = new MimeBodyPart();
	        bp.setContent(message, "text/html");
	        // add message body
	        mpart.addBodyPart(bp);
	        msg.setFrom(new InternetAddress("info@rugby.net", "The Rugby Net"));
	        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recip.getEmailAddress(), recip.getNickname()));

	        msg.setSubject(subject);
	        msg.setContent(mpart);
	        msg.saveChanges();
	        
	        Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING,"Sent mail to " + msg.getRecipients(RecipientType.TO)[0].toString());
	        
	        Transport.send(msg);
	
	    } catch (AddressException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 1 " + e.getLocalizedMessage());
	    	e.printStackTrace();
	    } catch (MessagingException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 2 " + e.getLocalizedMessage());
	    	e.printStackTrace();
	    } catch (UnsupportedEncodingException e) {
	    	Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 3 " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"Error sending mail 4 " + e.getLocalizedMessage());

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

	public IAppUser getRecip() {
		return recip;
	}

	public void setRecip(IAppUser recip) {
		this.recip = recip;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUnsubLink() {
		return unsubLink;
	}

	public void setUnsubLink(String unsubLink) {
		this.unsubLink = unsubLink;
	}

	public boolean configure(String subject, String title, String linkText, String linkTarget, String body, String unsubLink, IAppUser recip) {
		this.subject = subject;
		this.title = title;
		this.linkText = linkText;
		this.linkTarget = linkTarget;
		this.body = body;
		this.unsubLink = unsubLink;
		this.recip = recip;
		
		if (subject == null || title == null || body == null || recip == null) {
			return false;
		}
		
		if (part1 == null || part1.isEmpty()) {
			readTemplate();			
		}
		
		//<a href="https://www.quora.com/qemail/track_click?uid=YSa5mta4MCY&amp;al_imp=eyJoYXNoIjogIjEyMTU5ODgwMTI1MDk4NDQ0MHwxfDF8MTczMzM3OTIiLCAidHlwZSI6IDMzfQ%3D%3D&amp;aoid=FUDbhyV9SNW&amp;request_id=121598801250984440&amp;aoty=2&amp;et=2&amp;al_pri=QuestionLinkClickthrough&amp;id=7xqFbZflS0GOjsXi3PU06A%3D%3D&amp;ct=1455309485888073&amp;src=1&amp;ty_data=jug84oeWyCQ&amp;ty=1&amp;click_pos=1&amp;st=1455309485888073&amp;source=1&amp;stories=1_jug84oeWyCQ%7C1_8wsevV83TI2%7C1_4f1KUuoJewK%7C1_19pHS646uSB%7C1_3DyQeuRbWQx%7C1_Rczv06eNZ7P%7C1_m1CTxdbDpNK%7C1_i6e4IV9NbU%7C1_MdKXyuABTg4%7C1_YgSmzP5Uapi&amp;v=0&amp;aty=4"
		// style="text-decoration:none;display:block;font-size:19px;color:#333;letter-spacing:-0.5px;line-height:1.25;font-weight:bold;color:#155fad" target="_blank"><span>
		// Click here to activate your account 
		//</span></a>
		String link = "<a href=\"" + linkTarget + "\" style=\"text-decoration:none;display:block;font-size:19px;color:#333;letter-spacing:-0.5px;line-height:1.25;font-weight:bold;color:#155fad\" target=\"_blank\"><span>" + linkText + "</span></a>";
		message = part1 + title + part2 + link + part3 + body + part4;
		if (!unsubLink.isEmpty()) {
			message += unsubLink;
		}
		message += part5;
		
		return true;
	}

	private void readTemplate() {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream("mail/accountEmail.html");

			String everything = IOUtils.toString(inputStream);

			String[] chunks = everything.split("(<!-- split -->)");
			part1 = chunks[0];
			part2 = chunks[1];
			part3 = chunks[2];
			part4 = chunks[3];
			part5 = chunks[4];

			inputStream.close();
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		} 
		
	}

	public String getLinkText() {
		return linkText;
	}

	public void setLinkText(String linkText) {
		this.linkText = linkText;
	}

	public String getLinkTarget() {
		return linkTarget;
	}

	public void setLinkTarget(String linkTarget) {
		this.linkTarget = linkTarget;
	}
}
