package net.rugby.foundation.topten.server;

import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlaceFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.ofy.OfyTopTenListFactory;
import net.rugby.foundation.topten.server.utilities.ISocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.SocialMediaDirector;

import com.google.inject.AbstractModule;

public class TopTenMainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(OfyTopTenListFactory.class);
		bind(IPlaceFactory.class).to(OfyPlaceFactory.class);
		bind(ISocialMediaDirector.class).to(SocialMediaDirector.class);
	}
	
}

