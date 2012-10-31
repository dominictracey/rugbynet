package net.rugby.foundation.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.client.ui.PlayerListView.Listener;
import net.rugby.foundation.model.shared.PlayerRowData;

public class PlayerListViewPoolColumnDefinitions<T> {

	private static List<ColumnDefinition<PlayerRowData>> columnDefinitions =
      new ArrayList<ColumnDefinition<PlayerRowData>>();

	private static PlayerListView.Listener<PlayerRowData> listener;
	
    public PlayerListViewPoolColumnDefinitions() {
    	if (columnDefinitions.isEmpty()) {
	
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
	        public Widget render(PlayerRowData c) {
	        	Anchor a =  new Anchor(c.getDisplayName());
	        	a.addClickHandler( new ClickHandler() {
	        		@Override
	        		public void onClick(ClickEvent event) {
	      	          ; // let table handle it?
	        		}	
	        	});

	        	return a;
	        }
	
	        public boolean isClickable() {
	          return true;
	        }
	      });
	      
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String rating = "--";
		        	if (c.getPoolRating() != null)
		        		rating = c.getPoolRating().toString();
		          return new HTML( rating );
		        }
		      });

	      
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String pos = c.getPosition().name();
		          return new HTML(pos);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String pos = c.getTeamAbbr();
		          return new HTML(pos);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String pool = c.getPool();
		          return new HTML(pool);
		        }
		      });
  	} 	
    }

    public List<ColumnDefinition<PlayerRowData>> getColumnDefinitions() {
      return columnDefinitions;
    }

	public static PlayerListView.Listener<PlayerRowData> getListener() {
		return listener;
	}

	public static void setListener(Listener<PlayerRowData> listener2) {
		listener = listener2;
	}
	
	public static ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();

		headers.add( "Name");
		headers.add( "Rating");
		headers.add( "Position");
		headers.add( "Team");
		headers.add( "Pool");
		
		return headers;
	}
  }
