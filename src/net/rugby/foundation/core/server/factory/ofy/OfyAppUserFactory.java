/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.model.shared.AppUser;
import net.rugby.foundation.model.shared.CoreConfiguration.Environment;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IAppUser.EmailStatus;

/**
 * @author home
 *
 */
public class OfyAppUserFactory implements IAppUserFactory, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2901553975138064910L;
	private Long id;
	private String email;
	private String nickName;
	private IConfigurationFactory ccf;
	
	@Inject
	public OfyAppUserFactory(IConfigurationFactory ccf) {
		this.ccf = ccf;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
		this.email = null;
		this.nickName = null;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#setId(java.lang.Long)
	 */
	@Override
	public void setEmail(String email) {
		this.email = email;
		this.id = null;
		this.nickName = null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#get()
	 */
	@Override
	public IAppUser get() {
		if (id != null)
			return getById();
		else if (email != null)
			return getByEmail();
		else if (nickName != null)
			return getByNickname();
		else  {// give an empty one
			IAppUser u = new AppUser();
			u.setCreated(new Date());
			u.setEmailStatus(EmailStatus.NEW);
			return u;
		}
	}
	
	private IAppUser getByEmail() {
		Objectify ofy = DataStoreFactory.getOfy();

		Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", email.toLowerCase() );
		if (q.count() == 0) {
			return null; //empty
		}
		
		IAppUser u = q.get();

		return u;
	}

	private IAppUser getByNickname() {
		Objectify ofy = DataStoreFactory.getOfy();

		Query<AppUser> q = ofy.query(AppUser.class).filter("nickname", nickName);
		if (q.count() == 0) {
			return null; //empty
		}
		
		IAppUser u = q.get();

		return u;
	}
	
	private IAppUser getById() {
		Objectify ofy = DataStoreFactory.getOfy();

		AppUser u = ofy.get(new Key<AppUser>(AppUser.class,id));

		return u;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#put(net.rugby.foundation.model.shared.IAppUser)
	 */
	@Override
	public IAppUser put(IAppUser appUser) {
		// normalize the email
		String email = appUser.getEmailAddress().toLowerCase();
		appUser.setEmailAddress(email);
		Objectify ofy = DataStoreFactory.getOfy();

		appUser.setLastUpdated(new Date());
		ofy.put((AppUser)appUser);
		return appUser;
	}

	@Override
	public IAppUser put(IAppUser appUser, boolean loginOnly) {
		// normalize the email
		String email = appUser.getEmailAddress().toLowerCase();
		appUser.setEmailAddress(email);
		Objectify ofy = DataStoreFactory.getOfy();

		if (loginOnly)
			appUser.setLastLogin(new Date());
		else 
			appUser.setLastUpdated(new Date());
		
		ofy.put((AppUser)appUser);
		return appUser;
	}
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.core.server.factory.IAppUserFactory#setNickName(java.lang.String)
	 */
	@Override
	public void setNickName(String nickName) {
		this.email = null;
		this.id = null;
		this.nickName = nickName;	}

	@Override
	public List<IAppUser> getDigestEmailRecips() {
		try {
			Objectify ofy = DataStoreFactory.getOfy();
			
			Environment env = ccf.get().getEnvironment();
			
			Query<AppUser> qg = null;
			if (env != Environment.DEV && env != Environment.BETA) {
				qg = ofy.query(AppUser.class).filter("optOut ==", false);
			} else {
				qg = ofy.query(AppUser.class).filter("optOut ==", false).filter("isTestUser ==", true);
			}
			
			List<IAppUser> list = new ArrayList<IAppUser>();
			Iterator<AppUser> it = qg.list().iterator();
			while (it.hasNext()) {
				IAppUser g = (IAppUser)it.next();				
				list.add(g);
			}
			return list;
		} catch (Throwable ex) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,"getDigestEmailRecips", ex);
			return null;
		}
	}



}
