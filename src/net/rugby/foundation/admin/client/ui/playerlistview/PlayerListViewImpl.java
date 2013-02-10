package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.IPlayerMatchStats;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.CheckBox;


public class PlayerListViewImpl<T extends IPlayerMatchInfo> extends Composite implements PlayerListView<T>
{
	private static PlayerListViewImplUiBinder uiBinder = GWT.create(PlayerListViewImplUiBinder.class);
	private List<ColumnDefinition<T>> columnDefinitions;
	private List<T> playerList;
	public void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

	@UiTemplate("PlayerListViewImpl.ui.xml")

	interface PlayerListViewImplUiBinder extends UiBinder<Widget, PlayerListViewImpl<?>>
	{
	}

	@UiField FlexTable playersTable;



	//	private Presenter<T> presenter;
	private Listener<T> listener;
	private ArrayList<String> headers;

	public PlayerListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		playersTable.getRowFormatter().addStyleName(0, "groupListHeader");
		playersTable.addStyleName("groupList");
		playersTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");

	}



	@UiHandler("playersTable")
	void onTableClicked(ClickEvent event) {
		if (listener != null) {
			HTMLTable.Cell cell = playersTable.getCellForEvent(event);

			if (cell != null) {
				if (cell.getRowIndex() == 0) { // we have a check box up and click the select all
					if (cell.getCellIndex() == 0) {
						if (columnDefinitions.get(0).isSelectable()) {
							// select them all
							for (int i = 1; i < playerList.size()+1; ++i) {
								((CheckBox)playersTable.getWidget(i, 0).asWidget()).setValue(true);
								listener.onItemSelected(playerList.get(i-1));
							}	   			  
						}  
					}
				} else { 					
					if (shouldFireClickEvent(cell)) {
						T info = playerList.get(cell.getRowIndex()-1);
						if (cell.getCellIndex() == 1) {
							listener.showEditPlayer(info);
						} else if (cell.getCellIndex() == 2) {
							listener.showEditStats(info);
						} else if (cell.getCellIndex() == 3) {
							listener.showEditRating(info);
						} 					
					}

//					if (shouldFireSelectEvent(cell)) { // only do it if we have a checkbox up
//						listener.onItemSelected(player);
//						if (listener != null) {
//							//important sanity check because we are clicking on the table and not the checkbox. And we can miss.
//							int x = cell.getRowIndex();
//							((CheckBox)playersTable.getWidget(x,0).asWidget()).setValue(listener.onItemSelected(player));
//						}
//					}
				}
			}
		}
	}


	@Override
	public void setPlayers(List<T> PlayerList) {
		if (PlayerList != null) {
			playersTable.removeAllRows();
			this.playerList = PlayerList;
			setHeaders();
			String style = "leaderboardRow-odd";

			//	      Date begin = new Date();
			for (int i = 1; i < PlayerList.size()+1; ++i) {
				T t = PlayerList.get(i-1);
				for (int j = 0; j < columnDefinitions.size(); ++j) {
					ColumnDefinition<T> columnDefinition = columnDefinitions.get(j);

					playersTable.setWidget(i, j, columnDefinition.render(t));
					if (j < 4) {
						// first four columns are clickable
						playersTable.getCellFormatter().setStyleName(i, j, "leaderboardCell");
					}

				}
				playersTable.getRowFormatter().setStyleName(i, style);
				if (style == "leaderboardRow-odd")
					style = "leaderboardRow-even";
				else
					style = "leaderboardRow-odd";

			}

			//	      Date end = new Date();

			//	      Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE,begin.toLocaleString() + " end: " + end.toLocaleString());
		}
	}

	private void setHeaders() {
		int i = 0;
		for (String s : headers) {
			playersTable.setHTML(0, i++, s);	
		}

		playersTable.getRowFormatter().addStyleName(0, "leaderboardRow-header");


	}

	private boolean shouldFireClickEvent(HTMLTable.Cell cell) {
		boolean shouldFireClickEvent = false;

		if (cell != null) {
			ColumnDefinition<T> columnDefinition =
					columnDefinitions.get(cell.getCellIndex());

			if (columnDefinition != null) {
				shouldFireClickEvent = columnDefinition.isClickable();
			}
		}

		return shouldFireClickEvent;
	}

//	private boolean shouldFireSelectEvent(HTMLTable.Cell cell) {
//		boolean shouldFireSelectEvent = false;
//
//		if (cell != null) {
//			ColumnDefinition<T> columnDefinition =
//					columnDefinitions.get(cell.getCellIndex());
//
//			if (columnDefinition != null) {
//				shouldFireSelectEvent = columnDefinition.isSelectable();
//			}
//		}
//
//		return shouldFireSelectEvent;
//	}


	@SuppressWarnings("unchecked")
	@Override
	public void setListener(Listener<T> listener) {
		this.listener = listener;

		PlayerListViewColumnDefinitions.setListener((Listener<IPlayerMatchInfo>) listener);

	}

	@Override
	public void setColumnHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}



	@Override
	public void showWait() {
		playersTable.removeAllRows();
		playersTable.setWidget(0,0,new HTML("Stand by...")); //new Image("/resources/images/ajax-loader.gif"));
		
	}



	@Override
	public void updatePlayerMatchStats(T newPmi) {
		// find it in the playerList
		boolean found = false;
		int index = 0;
		for (IPlayerMatchInfo pmi : playerList) {
			if (pmi.getPlayerMatchStats().getId().equals(newPmi.getPlayerMatchStats().getId())) {
				found = true;
				break;
			}
			index++;
		}
		
		if (found) {
			assert (index < playerList.size());
			for (int j = 0; j < columnDefinitions.size(); ++j) {
				ColumnDefinition<T> columnDefinition = columnDefinitions.get(j);

				playersTable.setWidget(index, j, columnDefinition.render(newPmi));


			}
		}
	}


}
