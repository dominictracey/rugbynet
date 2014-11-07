/**
 * 
 */
package net.rugby.foundation.topten.client.ui.notes;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.topten.client.ClientFactory;
import net.rugby.foundation.topten.client.ui.ColumnDefinition;
import net.rugby.foundation.topten.model.shared.INote;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author home
 *
 */
public class NoteViewImpl<T extends INote> extends Composite implements NoteView<T> {



	interface NoteViewImplUiBinder extends UiBinder<Widget, NoteViewImpl<?>> {
	}
	
	private static NoteViewImplUiBinder uiBinder = GWT
			.create(NoteViewImplUiBinder.class);

	@UiField FlexTable noteTable;

	private NoteViewColumnDefinitions<T> columnDefinitions;
	private List<T> noteList;
	private NoteViewPresenter<T> listener;


	private ClientFactory clientFactory;
	
	public NoteViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		noteTable.getRowFormatter().addStyleName(0, "groupListHeader");
		noteTable.addStyleName("groupList");
		noteTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");

	}

	@Override
	public void setColumnDefinitions(NoteViewColumnDefinitions<T> defs) {
		this.columnDefinitions = defs;
		defs.setListener(listener);
	}


	@Override
	public void setNotes(List<T> NoteList) {
		if (NoteList != null) {
			noteTable.removeAllRows();
			this.noteList = NoteList;
			String style = "leaderboardRow-odd";

			for (int i = 1; i < NoteList.size()+1; ++i) {
				T t = NoteList.get(i-1);
				for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
					ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);

					noteTable.setWidget(i, j, columnDefinition.render(t));
					noteTable.getRowFormatter().setStyleName(i, style);

				}
				if (style == "leaderboardRow-odd")
					style = "leaderboardRow-even";
				else
					style = "leaderboardRow-odd";

			}
		}
	}

	@Override
	public void showWait() {
		noteTable.removeAllRows();
		noteTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));	
	}
	
	@Override
	public void setPresenter(NoteViewPresenter<T> p) {
		listener = p;
		
		if (columnDefinitions != null) {
			columnDefinitions.setListener(p);
		}
	}

	@Override
	public NoteViewPresenter<T> getPresenter() {
		return listener;
	}
	
	@Override
	public void showError(T Note, int index, String message) {
		Window.alert(message);
		// do something cool with a row formatter!
	}

//	@Override
//	public void updateNoteRow(T Note) {
//		// ignore i
//		// find the right row
//		boolean found = false;
//		int index = -1;
//		for (int k = 0; k < seriesConfigurationList.size(); ++k) {
//			if (seriesConfigurationList.get(k).getId().equals(Note.getId())) {
//				found = true;
//				index = k;
//				break;
//			}
//		}
//		if (found == true) {
//			for (int j = 0; j < columnDefinitions.getColumnDefinitions().size(); ++j) {
//				ColumnDefinition<T> columnDefinition = columnDefinitions.getColumnDefinitions().get(j);
//				noteTable.setWidget(index+1, j, columnDefinition.render(Note));
//			}
//		} else {
//			// must be a new one, append and redraw
//			seriesConfigurationList.add(Note);
//			showList(seriesConfigurationList);
//		}
//	}
//
	@Override
	public void setClientFactory(ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}



}

