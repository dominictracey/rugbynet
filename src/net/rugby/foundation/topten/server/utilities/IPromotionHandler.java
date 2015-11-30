package net.rugby.foundation.topten.server.utilities;

import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface IPromotionHandler {
String process(ITopTenList ttl);

String process(ITopTenList ttl, String channel);

String process(ITopTenList ttl, String channel, boolean showTeam);

}
