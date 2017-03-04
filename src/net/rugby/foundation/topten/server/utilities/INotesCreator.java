package net.rugby.foundation.topten.server.utilities;

import java.util.List;

import net.rugby.foundation.model.shared.IRatingQuery;
import net.rugby.foundation.model.shared.RatingMode;
import net.rugby.foundation.topten.model.shared.INote;

/**
 * Creates a list of bullets on interesting things about a <class>ITopTenList</class>
 * @author home
 *
 */
public interface INotesCreator {

	List<INote> createNotes(IRatingQuery rq, RatingMode mode);
}
