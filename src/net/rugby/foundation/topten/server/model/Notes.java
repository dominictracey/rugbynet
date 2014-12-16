package net.rugby.foundation.topten.server.model;

import java.io.Serializable;

import javax.persistence.Id;

import com.google.appengine.api.datastore.Text;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindexed;

/**
 * This class works around the 500 character String limit in the datastore by storing the data under the covers as Text
 * @author home
 *
 */
//@Entity
//public class Notes implements Serializable, ITopTenNotes {

//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 5579142166586030276L;
//
//	@Id
//	protected Long id;
//	
//	@Unindexed
//	protected Text notesText;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public Text getNotesText() {
//		return notesText;
//	}
//
//	public void setNotesText(Text notesText) {
//		this.notesText = notesText;
//	}
//	
//	public String getNotes() {
//		return notesText.getValue();
//	}
//	
//	public void setNotes(String value) {
//		notesText = new Text(value);
//	}
//}
