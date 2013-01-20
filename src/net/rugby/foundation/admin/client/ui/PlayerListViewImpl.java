package net.rugby.foundation.admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import net.rugby.foundation.admin.shared.IPlayerMatchInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.CheckBox;


public class PlayerListViewImpl<T> extends Composite implements PlayerListView<T>
{
	private static PlayerListViewImplUiBinder uiBinder = GWT.create(PlayerListViewImplUiBinder.class);
    private List<ColumnDefinition<T>> columnDefinitions;
    private List<T> playerList;
    private int numColumns = 0;   //currently == 6 for checkbox; 5 for no checkbox
 //   private GroupSplash groupSplash = null;
    public void setColumnDefinitions(List<ColumnDefinition<T>> columnDefinitions) {
      this.columnDefinitions = columnDefinitions;
      numColumns = columnDefinitions.size();
    }
    
	@UiTemplate("PlayerListViewImpl.ui.xml")

	interface PlayerListViewImplUiBinder extends UiBinder<Widget, PlayerListViewImpl<?>>
	{
	}
	
	@UiField Anchor editGroupInfoLink;
	@UiField Anchor draftAnalysisLink;
	@UiField Label clickHere;
	@UiField TextArea editGroupInfo;
	@UiField Button editGroupInfoSubmit;
	@UiField Button editGroupInfoCancel;
	@UiField FlexTable playersTable;
	@UiField Button button1;
	@UiField Button button2;
	@UiField Button button3;
	@UiField Button button4;
	@UiField HTML groupInfoBasic;

	
	
	private Presenter<T> presenter;
	private Listener<T> listener;
	private String groupInfo;
	private ArrayList<String> headers;
	
	public PlayerListViewImpl()
	{
		initWidget(uiBinder.createAndBindUi(this));
		playersTable.getRowFormatter().addStyleName(0, "groupListHeader");
		playersTable.addStyleName("groupList");
		playersTable.getCellFormatter().addStyleName(0, 1, "groupListNumericColumn");
		editGroupInfoLink.setVisible(false);
		draftAnalysisLink.setVisible(false);
		clickHere.setVisible(false);
		editGroupInfo.setVisible(false);
		editGroupInfoSubmit.setVisible(false);
		editGroupInfoCancel.setVisible(false);
		button1.setVisible(false);
		button2.setVisible(false);
		button3.setVisible(false);
		button4.setVisible(false);
	}
	
  @UiHandler("button1")
  void onButton1Clicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onButton1Clicked();
    }
  }

  @UiHandler("button2")
  void onButton2Clicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onButton2Clicked();
    }
  }
  
  @UiHandler("button3")
  void onButton3Clicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onButton3Clicked();
    }
  }
  
  @UiHandler("button4")
  void onButton4Clicked(ClickEvent event) {
    if (presenter != null) {
      presenter.onButton4Clicked();
    }
  }

  @UiHandler("editGroupInfoLink")
  void onEditGroupInfoLink(ClickEvent event) {
	  groupInfoBasic.setVisible(false);
	  editGroupInfo.setText(groupInfo);
	  editGroupInfo.setVisible(true);
	  editGroupInfoSubmit.setVisible(true);
	  editGroupInfoCancel.setVisible(true);
  }
  
  @UiHandler("editGroupInfoCancel")
  void onEditGroupInfoCancel(ClickEvent event)  {
	  editGroupInfo.setVisible(false);
	  editGroupInfoSubmit.setVisible(false);
	  editGroupInfoCancel.setVisible(false);	  
	  groupInfoBasic.setVisible(true);

  }
 
  @UiHandler("editGroupInfoSubmit")
  void onEditGroupInfoSubmit(ClickEvent event)  {
	  editGroupInfo.setVisible(false);
	  editGroupInfoSubmit.setVisible(false);
	  editGroupInfoCancel.setVisible(false);	  
	  groupInfoBasic.setVisible(true);
	  
	  presenter.onSaveGroupInfoClicked(editGroupInfo.getText());

  }
  
  @UiHandler("draftAnalysisLink")
  void ondraftAnalysisLink(ClickEvent event)  {
	  
	  presenter.showDraftAnalysis();
  }
  
  @UiHandler("playersTable")
  void onTableClicked(ClickEvent event) {
    if (presenter != null) {
      HTMLTable.Cell cell = playersTable.getCellForEvent(event);

      if (cell != null) {
    	  if (numColumns == 6 && cell.getRowIndex() == 0) { // we have a check box up and click the select all
        	  if (cell.getRowIndex() == 0) 	  {   
	    		  if (cell.getCellIndex() == 0) {
	    			  if (columnDefinitions.get(0).isSelectable()) {
		    			  // select them all
		    		      for (int i = 1; i < playerList.size()+1; ++i) {
		    		    	  ((CheckBox)playersTable.getWidget(i, 0).asWidget()).setValue(true);
		    		    	  presenter.onItemSelected(playerList.get(i-1));
		    		      }
	    		    	  
	    		      }
	   			  
	    		  }  
	    	  }
	      } else { 
 		  	T player = playerList.get(cell.getRowIndex()-1);
 	        if (shouldFireClickEvent(cell)) {
	          presenter.onItemClicked(player);
	        }
	
	        if (shouldFireSelectEvent(cell) && numColumns == 6) { // only do it if we have a checkbox up
	          presenter.onItemSelected(player);
	          if (listener != null) {
	        	  //important sanity check because we are clicking on the table and not the checkbox. And we can miss.
	        	int x = cell.getRowIndex();
	          	((CheckBox)playersTable.getWidget(x,0).asWidget()).setValue(listener.onItemSelected(player));
	          }
	     	}
        }
      }
    }
  }


	@Override
	public void setPresenter(Presenter<T> listener)
	{
		this.presenter = listener;
	}

	@Override
	public void setPlayers(List<T> PlayerList) {
	      playersTable.removeAllRows();
	      this.playerList = PlayerList;
	      setHeaders();
	      String style = "odd";
	      for (int i = 1; i < PlayerList.size()+1; ++i) {
		        T t = PlayerList.get(i-1);
		        for (int j = 0; j < columnDefinitions.size(); ++j) {
		          ColumnDefinition<T> columnDefinition = columnDefinitions.get(j);
		          playersTable.setWidget(i, j, columnDefinition.render(t));
		          playersTable.getRowFormatter().setStyleName(i, style);
	
		        }
		        if (style == "odd")
		        	style = "even";
		        else
		        	style = "odd";

	      }
	}
	
	private void setHeaders() {
		int i = 0;
		for (String s : headers) {
			playersTable.setHTML(0, i++, s);	
		}

		playersTable.getRowFormatter().addStyleName(0, "groupListHeader");

		
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

    private boolean shouldFireSelectEvent(HTMLTable.Cell cell) {
      boolean shouldFireSelectEvent = false;

      if (cell != null) {
        ColumnDefinition<T> columnDefinition =
          columnDefinitions.get(cell.getCellIndex());

        if (columnDefinition != null) {
          shouldFireSelectEvent = columnDefinition.isSelectable();
        }
      }

      return shouldFireSelectEvent;
    }

	public String getGroupInfo() {
		return groupInfoBasic.getHTML();
	}

	public void setGroupInfo(String groupInfo) {
		this.groupInfo = groupInfo;
		this.groupInfoBasic.setHTML(groupInfo);
	}

	@Override
	public void setButtonCaptions(String name1, String name2, String name3, String name4) {
		if (!name1.isEmpty()) {
			button1.setText(name1);
			button1.setVisible(true);
		}
		else {
			button1.setVisible(false);
		}

		if (!name2.isEmpty()) {
			button2.setText(name2);
			button2.setVisible(true);
		}
		else {
			button2.setVisible(false);
		}		
		
		if (!name3.isEmpty()) {
			button3.setText(name3);
			button3.setVisible(true);
		}
		else {
			button3.setVisible(false);
		}		
		
		if (!name4.isEmpty()) {
			button4.setText(name4);
			button4.setVisible(true);
		}
		else {
			button4.setVisible(false);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setListener(Listener<T> listener) {
		this.listener = listener;

		PlayerListViewColumnDefinitions.setListener((Listener<IPlayerMatchInfo>) listener);

		
	}
	
	@Override
	public void showEditGroupInfoLink(boolean show)
	{
		editGroupInfoLink.setVisible(show);
	}

	@Override
	public void showDraftAnalysisLink(boolean show) {
		//DOM.setElementAttribute(draftAnalysisLink.getElement(), "href", "\"javascript:;\"");
		draftAnalysisLink.setVisible(show);
		clickHere.setVisible(show);
		
	}

	@Override
	public void setColumnHeaders(ArrayList<String> headers) {
		this.headers = headers;
	}


}
