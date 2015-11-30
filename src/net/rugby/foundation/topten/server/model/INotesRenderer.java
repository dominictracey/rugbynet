package net.rugby.foundation.topten.server.model;

import java.util.List;

import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.ITopTenList;

public interface INotesRenderer {
	String render(List<INote> notes, ITopTenList context, boolean includeDetails);
	String render(INote note, ITopTenList context, boolean includeDetails);
}
