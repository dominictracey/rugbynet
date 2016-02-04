package net.rugby.foundation.topten.server.rest;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.core.server.BPMServletContextListener;
import net.rugby.foundation.core.server.factory.ICompetitionFactory;
import net.rugby.foundation.core.server.factory.IConfigurationFactory;
import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.model.shared.Competition;
import net.rugby.foundation.model.shared.Content;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.ICoreConfiguration;
import net.rugby.foundation.model.shared.TeamGroup;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.inject.Inject;
import com.google.inject.Injector;



@Api(
		name = "topten",
		version = "v1",
		clientIds = {Ids.WEB_CLIENT_ID, Ids.ANDROID_CLIENT_ID, Ids.IOS_CLIENT_ID},
		audiences = {Ids.ANDROID_AUDIENCE}
		)
public class TopTenV1 {
	private ICompetitionFactory cf;

	private IConfigurationFactory ccf;

	//	@Inject
	//	public TopTenV1(ICompetitionFactory cf) {
	//		this.cf = cf;
	//	}
	private static Injector injector = null;

	public TopTenV1() {
		if (injector == null) {
			injector = BPMServletContextListener.getInjectorForNonServlets();
		}

		this.cf = injector.getInstance(ICompetitionFactory.class);
		this.ccf = injector.getInstance(IConfigurationFactory.class);

	}
	
	@ApiMethod(name = "content.getcontent", httpMethod = "GET")
	public Content getcontent() {
		return new Content(90123124L,"OK");
		//		return (Competition) cf.getUnderwayComps().get(0);
	}

		@ApiMethod(name = "competition.getcomp", path="/competitions/global/", httpMethod = "GET")
		public ICompetition getcompetition() {
	
			return cf.getUnderwayComps().get(0);
		}
		
		@ApiMethod(name = "competition.getAll", path="/competitions/", httpMethod = "GET")
		public List<ICompetition> getallcompetition() {
			
			return cf.getUnderwayComps();
		}
		
		@ApiMethod(name = "configuration.get", path="/configuration/", httpMethod = "GET")
		public ICoreConfiguration getConfiguration() {
			
			return ccf.get();
		}

}
