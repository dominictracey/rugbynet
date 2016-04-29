package net.rugby.foundation.core.server.promote;

import java.util.List;

import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.topten.model.shared.ITopTenItem;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface IPromoter {

	List<IPlayer> promoteList(Long ttlId);

	String promoteItem(ITopTenItem tti, ITopTenList ttl);
}
