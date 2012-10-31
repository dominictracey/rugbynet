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
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.Player.movement;

public class PlayerListViewColumnDefinitionsNoSelect<T> {

	private static List<ColumnDefinition<PlayerRowData>> columnDefinitions =
      new ArrayList<ColumnDefinition<PlayerRowData>>();

	private static PlayerListView.Listener<PlayerRowData> listener;
	
    public PlayerListViewColumnDefinitionsNoSelect() {
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
	          //return new HTML(c.getDisplayName());
	        }
	
	        public boolean isClickable() {
	          return true;
	        }
	      });
	      
	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String image = "resources/arrow_Up_Green.gif";
		        	if (c.getMovement() == Player.movement.DOWN)
		        		image = "resources/arrow_Down_Red.gif";
		        	else if (c.getMovement() == movement.UNCHANGED)
		        		image = "resources/unchanged_Blue.gif";
		        	String rating = "--";
		        	if (c.getOverallRating() != null)
		        		rating = c.getOverallRating().toString();
		          return new HTML( rating + "<img src=" + image + ">");
		        }
		      });

	      columnDefinitions.add(new ColumnDefinition<PlayerRowData>() {
		        public Widget render(PlayerRowData c) {
		        	String rating = "--";
		        	if (c.getLastRating() != null)
		        		rating = c.getLastRating().toString();
		          return new HTML(rating);
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
		headers.add( "Last");
		headers.add( "Position");
		headers.add( "Team");
		
		return headers;
	}
  }
