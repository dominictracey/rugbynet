package net.rugby.foundation.topten.model.shared;

import net.rugby.foundation.model.shared.IHasId;

public interface INoteRef extends IHasId {

	public abstract Long getId();

	public abstract void setId(Long id);

	public abstract Long getNoteId();

	public abstract void setNoteId(Long noteId);

	public abstract Long getContextId();

	public abstract void setContextId(Long contextId);

	void setUrId(int urId);

	int getUrId();

}