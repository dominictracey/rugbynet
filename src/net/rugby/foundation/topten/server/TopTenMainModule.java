package net.rugby.foundation.topten.server;

import net.rugby.foundation.core.server.factory.IPlaceFactory;
import net.rugby.foundation.core.server.factory.ofy.OfyPlaceFactory;
import net.rugby.foundation.topten.server.factory.INoteFactory;
import net.rugby.foundation.topten.server.factory.INoteRefFactory;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.factory.ofy.OfyNoteFactory;
import net.rugby.foundation.topten.server.factory.ofy.OfyNoteRefFactory;
import net.rugby.foundation.topten.server.factory.ofy.OfyTopTenListFactory;
import net.rugby.foundation.topten.server.utilities.INotesCreator;
import net.rugby.foundation.topten.server.utilities.ISocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.MatchNotesCreator;
import net.rugby.foundation.topten.server.utilities.SocialMediaDirector;
import net.rugby.foundation.topten.server.utilities.notes.TacklesNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TopTenNotesRenderer;
import net.rugby.foundation.topten.server.utilities.notes.TriesScoredNotesCreator;
import net.rugby.foundation.topten.server.utilities.notes.TwitterNotesRenderer;

import com.google.inject.AbstractModule;

public class TopTenMainModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(ITopTenListFactory.class).to(OfyTopTenListFactory.class);
		bind(IPlaceFactory.class).to(OfyPlaceFactory.class);
		bind(ISocialMediaDirector.class).to(SocialMediaDirector.class);
		bind(INoteFactory.class).to(OfyNoteFactory.class);
		bind(INoteRefFactory.class).to(OfyNoteRefFactory.class);
		bind(INotesCreator.class).to(MatchNotesCreator.class);
		bind(TopTenNotesRenderer.class);
		bind(TwitterNotesRenderer.class);
		bind(TopTenNotesCreator.class);
		bind(TriesScoredNotesCreator.class);
		bind(TacklesNotesCreator.class);
	}
	
}

