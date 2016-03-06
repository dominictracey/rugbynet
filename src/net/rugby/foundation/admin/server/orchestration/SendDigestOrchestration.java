/**
 * 
 */
package net.rugby.foundation.admin.server.orchestration;

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
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeMessage.RecipientType;

import com.google.appengine.api.taskqueue.TaskOptions;

import net.rugby.foundation.admin.server.factory.IBlurbFactory;
import net.rugby.foundation.admin.server.factory.IDigestEmailFactory;
import net.rugby.foundation.admin.server.factory.IMatchRatingEngineSchemaFactory;
import net.rugby.foundation.admin.server.factory.IQueryRatingEngineFactory;
import net.rugby.foundation.admin.server.model.IQueryRatingEngine;
import net.rugby.foundation.admin.server.util.DigestEmailer;
import net.rugby.foundation.admin.shared.IDigestEmail;
import net.rugby.foundation.admin.shared.TopTenSeedData;
import net.rugby.foundation.core.server.factory.IAppUserFactory;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerRatingFactory;
import net.rugby.foundation.core.server.factory.IRatingQueryFactory;
import net.rugby.foundation.model.shared.IAppUser;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.IRatingQuery.Status;
import net.rugby.foundation.model.shared.RatingQuery;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;

/**
 * @author home
 *
 */
@SuppressWarnings("deprecation")
public class SendDigestOrchestration extends OrchestrationCore<IAppUser> {

	private IOrchestrationConfigurationFactory ocf;
	private IAppUserFactory auf;
	private IBlurbFactory bf;
	private IDigestEmailFactory def;
	private ICompetitionFactory cf;
	private Long digestEmailId;
	
	private DigestEmailer dem = null;
	private IConfigurationFactory ccf;

	
	public SendDigestOrchestration(IOrchestrationConfigurationFactory ocf, IAppUserFactory auf, IBlurbFactory bf, IDigestEmailFactory def, ICompetitionFactory cf, IConfigurationFactory ccf) {
		this.ocf = ocf;
		this.auf = auf;
		this.bf = bf;
		this.def = def;
		this.cf = cf;
		this.ccf = ccf;
	}	

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#addParams(com.google.appengine.api.taskqueue.TaskOptions)
	 */
	@Override
	public TaskOptions addParams(TaskOptions builder) {
		return null;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#execute()
	 */
	@Override
	public void execute() {
		if (target != null) {
			IDigestEmail de = def.get(digestEmailId);
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "Sending digest email " + de.getSubject() + " to " + target.getEmailAddress());
			if (dem == null) {
				dem = new DigestEmailer(cf);
			}
			
			assert (de.getBlurbs() != null && de.getBlurbs().size() > 0);
			
			dem.configure(de, target);			
			dem.send(ccf.get());
		} else {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Attempt to invoke Send Digest Orchestration with null AppUser provided");
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#setExtraKey(java.lang.Long)
	 */
	@Override
	public void setExtraKey(Long id) {
		digestEmailId = id;

	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.admin.server.orchestration.IOrchestration#getExtraKey()
	 */
	@Override
	public Long getExtraKey() {
		return digestEmailId;
	}

}
