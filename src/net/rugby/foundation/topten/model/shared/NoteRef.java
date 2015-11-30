package net.rugby.foundation.topten.model.shared;

import java.io.Serializable;

import javax.persistence.Id;

import com.googlecode.objectify.annotation.Entity;

import net.rugby.foundation.model.shared.IHasId;

@Entity
public class NoteRef implements Serializable, IHasId, INoteRef {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5788773701921682481L;

	@Id
	Long id;
	
	protected Long noteId;
	protected Long contextId;
	protected int urId;
	
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#getNoteId()
	 */
	@Override
	public Long getNoteId() {
		return noteId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#setNoteId(java.lang.Long)
	 */
	@Override
	public void setNoteId(Long noteId) {
		this.noteId = noteId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#getContextId()
	 */
	@Override
	public Long getContextId() {
		return contextId;
	}
	/* (non-Javadoc)
	 * @see net.rugby.foundation.topten.model.shared.INoteRef#setContextId(java.lang.Long)
	 */
	@Override
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	@Override
	public int getUrId() {
		return urId;
	}
	@Override
	public void setUrId(int urId) {
		this.urId = urId;
	}
	
}
