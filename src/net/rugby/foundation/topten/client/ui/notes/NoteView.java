package net.rugby.foundation.topten.client.ui.notes;

import java.util.List;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.model.shared.INote;

public interface NoteView<T extends INote> extends IsWidget {
	public interface NoteViewPresenter<T> {
		ClientFactory getClientFactory();	
		Boolean deleteNote(Long noteId);
		void addNote(T note);
		void editNote(T note);
		void gotoPlace(Place p);
	} 

	public abstract void setPresenter(NoteViewPresenter<T> p);

	public abstract void showError(T task, int index, String message);

	//void setColumnHeaders(ArrayList<String> headers);

	void showWait();

	void setColumnDefinitions(NoteViewColumnDefinitions<T> defs);

	//public abstract void updateNoteRow(T sc);

	NoteViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

	public abstract void setNotes(List<T> notes);


}