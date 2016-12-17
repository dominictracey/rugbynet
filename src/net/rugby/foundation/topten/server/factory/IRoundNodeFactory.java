package net.rugby.foundation.topten.server.factory;

import java.util.List;

import net.rugby.foundation.core.server.factory.ICachingFactory;
import net.rugby.foundation.topten.server.rest.RoundNode;

public interface IRoundNodeFactory extends ICachingFactory<RoundNode> {

	List<RoundNode> get(Long compId, int positionOrdinal);

	RoundNode create(Long compId, int urOrd, String label, int positionOrdinal);

	void dropListFromCache(Long hostCompId, int i);

}
