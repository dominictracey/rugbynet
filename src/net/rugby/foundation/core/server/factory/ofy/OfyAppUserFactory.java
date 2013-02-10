/**
 * 
 */
package net.rugby.foundation.core.server.factory.ofy;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;

import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.model.shared.AppUser;
import net.rugby.foundation.model.shared.DataStoreFactory;
import net.rugby.foundation.model.shared.IAppUser;

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
	
	public OfyAppUserFactory() {

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
		else  // give an empty one
			return new AppUser();
	}
	
	private IAppUser getByEmail() {
		Objectify ofy = DataStoreFactory.getOfy();

		Query<AppUser> q = ofy.query(AppUser.class).filter("emailAddress", email );
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



}
