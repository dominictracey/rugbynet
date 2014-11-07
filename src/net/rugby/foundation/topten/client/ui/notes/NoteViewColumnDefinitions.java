package net.rugby.foundation.topten.client.ui.notes;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.topten.client.place.SeriesPlace;
import net.rugby.foundation.topten.client.ui.ColumnDefinition;
import net.rugby.foundation.model.shared.ICompetition;
import net.rugby.foundation.model.shared.IServerPlace;
import net.rugby.foundation.topten.client.ui.notes.NoteView.NoteViewPresenter;
import net.rugby.foundation.topten.model.shared.INote;

public class NoteViewColumnDefinitions<T extends INote> {

	private List<ColumnDefinition<T>> columnDefinitions =
			new ArrayList<ColumnDefinition<T>>();

	private NoteViewPresenter<T> listener;

	public NoteViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			columnDefinitions.add(new ColumnDefinition<T>() {
				//id
				public Widget render(final T c) {
					Widget w = listener.getClientFactory().render(c, null, false);
					
					return w;
				}     

				public boolean isClickable() {
					return false;
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});
			

//			columnDefinitions.add(new ColumnDefinition<T>() {
//				// Edit
//				public Widget render(final T c) {
//
//					Anchor a =  new Anchor("Link");
//					a.addClickHandler(new ClickHandler() {
//
//						@Override
//						public void onClick(ClickEvent event) {
//							listener.getClientFactory().getRpcService().getPlace(c.getLink(), new AsyncCallback<IServerPlace>() {
//
//								@Override
//								public void onFailure(Throwable caught) {
//									Window.alert(caught.getLocalizedMessage());
//								}
//
//								@Override
//								public void onSuccess(IServerPlace place) {
//									SeriesPlace p = new SeriesPlace();
//									p.setCompId(place.getCompId());
//									p.setGroupId(place.getGroupId());
//									p.setMatrixId(place.getMatrixId());
//									p.setItemId(place.getItemId());
//									p.setQueryId(place.getQueryId());
//									p.setSeriesId(place.getSeriesId());
//									
//									// what we want to track about the note-sourced click stream:
//									//		1) the template that caught the user's eye
//									//		2) the list type the user left
//									//		3) the list type the user went to
//									//		4) the player name being followed
//									recordAnalyticsEvent("note", "template", c.getTemplateSelector(), 1);
//									recordAnalyticsEvent("note", "link", c.getLink(), 1);
//									recordAnalyticsEvent("note", "type", c.getType().toString(), 1);
//									recordAnalyticsEvent("note", "player", listener.getClientFactory().getPlayerName(c.getPlayer1Id()), 1);
//									listener.gotoPlace(p);
//
//								}
//
//							});
//						}
//
//					});
//					return a;
//				}
//
//				public boolean isClickable() {
//					return false;  // it's a button
//				}
//
//				@Override
//				public Column<T, ?> getColumn() {
//					// TODO Auto-generated method stub
//					return null;
//				}
//			});
		} 	

	}

	public List<ColumnDefinition<T>> getColumnDefinitions() {
		return columnDefinitions;
	}

	public void setListener(NoteViewPresenter<T> listener2) {
		this.listener =  listener2;
	}
	
	
	public static native void recordAnalyticsEvent(String cat, String action, String label, int val) /*-{

		$wnd.ganew('send', 'event', cat, action, label, val);
	}-*/;
}
