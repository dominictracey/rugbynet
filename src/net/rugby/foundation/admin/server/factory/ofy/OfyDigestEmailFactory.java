package net.rugby.foundation.admin.server.factory.ofy;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.shared.DigestEmail;
import net.rugby.foundation.admin.shared.IBlurb;
import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.core.server.factory.BaseCachingFactory;
import net.rugby.foundation.model.shared.DataStoreFactory;

public class OfyDigestEmailFactory extends BaseCachingFactory<IDigestEmail> implements IDigestEmailFactory {
	
	private Objectify ofy;
	private IBlurbFactory bf;

	@Inject
	public OfyDigestEmailFactory(IBlurbFactory bf) {
		this.ofy = DataStoreFactory.getOfy();
		this.bf = bf;
	}
	
	@Override
	public IDigestEmail create() {
		try {
			IDigestEmail digestEmail = new DigestEmail();

			return digestEmail;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "create" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected IDigestEmail getFromPersistentDatastore(Long id) {
		try {
			DigestEmail digestEmail = ofy.get(new Key<DigestEmail>(DigestEmail.class, id));
			if (digestEmail != null) {
				populatedigestEmail(digestEmail);
			}
			return digestEmail;

		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "get" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	public void populatedigestEmail(IDigestEmail digestEmail) {
		if (digestEmail.getBlurbs() == null) {
			digestEmail.setBlurbs(new ArrayList<IBlurb>());
		}
		
		if (digestEmail.getFormattedBlurbMap() == null) {
			digestEmail.setFormattedBlurbMap(new HashMap<Long,String>());
		}
		
		for (Long bid : digestEmail.getBlurbIds()) {
			IBlurb blurb = bf.get(bid);
			digestEmail.getBlurbs().add(blurb);
			buildFormattedBlurb(blurb,digestEmail);
		}
		
		readTemplate(digestEmail);
		addMessage(digestEmail);
	}


	private void readTemplate(IDigestEmail de) {
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream("mail/accountEmail.html");

			String everything = IOUtils.toString(inputStream);

			String[] chunks = everything.split("(<!-- split -->)|(<!-- element -->)");
			assert (chunks.length == 7);
			de.setPart1(chunks[0]);
			de.setPart2(chunks[1]);
			de.setPart3(chunks[2]); //element start
			de.setPart4(chunks[3]); // element mid
			de.setPart5(chunks[4]); // element end
			de.setPart6(chunks[5]);
			de.setPart7(chunks[6]);
			
			
			inputStream.close();
			
			for (IBlurb b: de.getBlurbs()) {
				buildFormattedBlurb(b, de);
			}
			
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, ex.getLocalizedMessage(), ex);
		} 
		
	}

	private void addMessage(IDigestEmail de) {
		de.setPart2(de.getPart2() + de.getPart3() + de.getMessage() + de.getPart4());		
	}
	
	private void buildFormattedBlurb(IBlurb b, IDigestEmail de) {
		String fb = de.getPart3();
		String url = "http://www.rugby.net/s/" + b.getServerPlace().getGuid();
		String link = "<a href=\"" + url+ "\" style=\"text-decoration:none;display:block;font-size:19px;color:#333;letter-spacing:-0.5px;line-height:1.25;font-weight:bold;color:#155fad\" target=\"_blank\"><span>" + b.getLinkText() + "</span></a>";
		String readMore = "<a href=\"" + url+ "\"style=\"border-collapse:collapse;display:block;font-family:Georgia,Times,'Times New Roman',serif;font-size:15px;line-height:1.4;color:#155fad\" target=\"_blank\"><span> See the full list...</span></a>";

		fb += link + de.getPart4() + b.getBodyText() + readMore + de.getPart5();
		
		de.getFormattedBlurbMap().put(b.getId(),fb);
	}
	
	@Override
	protected IDigestEmail putToPersistentDatastore(IDigestEmail digestEmail) {
		try {

			if (digestEmail != null) {

				ofy.put(digestEmail);
				populatedigestEmail(digestEmail);
			}
			return digestEmail;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "put" + ex.getMessage(), ex);
			return null;
		}
	}

	@Override
	protected boolean deleteFromPersistentDatastore(IDigestEmail digestEmail) {
		try {

			if (digestEmail != null) {
				ofy.delete(digestEmail);
				return true;
			} else {
				return false;
			}
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "delete" + ex.getMessage(), ex);
			return false;
		}
	}



}
