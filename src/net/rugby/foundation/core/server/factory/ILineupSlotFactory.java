package net.rugby.foundation.core.server.factory;

import net.rugby.foundation.model.shared.ILineupSlot;
import net.rugby.foundation.model.shared.Position.position;

public interface ILineupSlotFactory extends ICachingFactory<ILineupSlot> {

	position getPosFromSlot(int slot);

}
