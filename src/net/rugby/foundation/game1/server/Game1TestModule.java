package net.rugby.foundation.game1.server;

import net.rugby.foundation.game1.server.BPM.CoreRuleFactory;
import net.rugby.foundation.game1.server.BPM.ICoreRuleFactory;
import net.rugby.foundation.game1.server.factory.IClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.IConfigurationFactory;
import net.rugby.foundation.game1.server.factory.IEntryFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardFactory;
import net.rugby.foundation.game1.server.factory.ILeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.ILeagueFactory;
import net.rugby.foundation.game1.server.factory.IMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.IMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.IRoundEntryFactory;
import net.rugby.foundation.game1.server.factory.test.TestClubhouseLeagueMapFactory;
import net.rugby.foundation.game1.server.factory.test.TestConfigurationFactory;
import net.rugby.foundation.game1.server.factory.test.TestEntryFactory;
import net.rugby.foundation.game1.server.factory.test.TestLeaderboardFactory;
import net.rugby.foundation.game1.server.factory.test.TestLeaderboardRowFactory;
import net.rugby.foundation.game1.server.factory.test.TestLeagueFactory;
import net.rugby.foundation.game1.server.factory.test.TestMatchEntryFactory;
import net.rugby.foundation.game1.server.factory.test.TestMatchStatsFactory;
import net.rugby.foundation.game1.server.factory.test.TestRoundEntryFactory;

import com.google.inject.AbstractModule;

public class Game1TestModule extends AbstractModule {
	@Override
	 protected void configure() {
		bind(Game1ServiceImpl.class);
		bind(IEntryFactory.class).to(TestEntryFactory.class);
		bind(IRoundEntryFactory.class).to(TestRoundEntryFactory.class);
		bind(IMatchEntryFactory.class).to(TestMatchEntryFactory.class);
		bind(ILeaderboardFactory.class).to(TestLeaderboardFactory.class);
		bind(ILeaderboardRowFactory.class).to(TestLeaderboardRowFactory.class);
		bind(ICoreRuleFactory.class).to(CoreRuleFactory.class);
		bind(IConfigurationFactory.class).to(TestConfigurationFactory.class);
		bind(ILeagueFactory.class).to(TestLeagueFactory.class);
		bind(IClubhouseLeagueMapFactory.class).to(TestClubhouseLeagueMapFactory.class);
		bind(IMatchStatsFactory.class).to(TestMatchStatsFactory.class);
	}
}

