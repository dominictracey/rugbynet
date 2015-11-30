package net.rugby.foundation.topten.client.resources.noteTemplates;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface NoteTemplates extends ClientBundle {
	public static final NoteTemplates INSTANCE =  GWT.create(NoteTemplates.class);

	@Source("default.txt")
	public TextResource noteTemplates();
}
