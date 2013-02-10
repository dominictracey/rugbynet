package net.rugby.foundation.admin.client.ui.playerlistview;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.playerlistview.PlayerListView.Listener;
import net.rugby.foundation.model.shared.IPlayerMatchInfo;
import net.rugby.foundation.model.shared.PlayerRating;

public class PlayerListViewColumnDefinitions<T> {

	private static List<ColumnDefinition<IPlayerMatchInfo>> columnDefinitions =
      new ArrayList<ColumnDefinition<IPlayerMatchInfo>>();

	private static PlayerListView.Listener<IPlayerMatchInfo> listener;
	
    public PlayerListViewColumnDefinitions() {
    	if (columnDefinitions.isEmpty()) {
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
	        public Widget render(IPlayerMatchInfo c) {
	          CheckBox checkBox = new CheckBox();
	          if (listener != null)
	        	  checkBox.setValue(listener.isSelected(c));
	          return checkBox;
	        }
	
	        public boolean isSelectable() {
	          return true;
	        }
	      });
	
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {

	        public Widget render(IPlayerMatchInfo c) {
	        	String name = c.getPlayerMatchStats().getName();
	          return new HTML(name);
	        }
	     
	        public boolean isClickable() {
	          return true;
	        }
	      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
	    	  public Widget render(IPlayerMatchInfo c) {
		        	String name = c.getPlayerMatchStats().getId().toString();
		          return new HTML(name);
		        }
		
		        public boolean isClickable() {
		          return true;
		        }
		  });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
	    	  
	    	  public Widget render(IPlayerMatchInfo c) {
		        	String name = "--";
		        	if ((PlayerRating)c.getMatchRating() != null && ((PlayerRating)c.getMatchRating()).getRating() != null) {
		        		name = ((PlayerRating)c.getMatchRating()).getRating().toString();
		        	}
		        	return new HTML(name);
		        }     
		
		        public boolean isClickable() {
		          return true;
		        }
		      });
	      

	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String pos = c.getPlayerMatchStats().getPosition().name();
		          return new HTML(pos);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTries().toString()  + "/" + c.getPlayerMatchStats().getTryAssists().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getPoints().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getKicks().toString() + "/" + c.getPlayerMatchStats().getPasses().toString() + "/" +  c.getPlayerMatchStats().getRuns().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getMetersRun().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getCleanBreaks().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getDefendersBeaten().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getOffloads().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTurnovers().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getTacklesMade().toString() +"/" + c.getPlayerMatchStats().getTacklesMissed().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getLineoutsWonOnThrow().toString() +"/" + c.getPlayerMatchStats().getLineoutsStolenOnOppThrow().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getPenaltiesConceded().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = c.getPlayerMatchStats().getYellowCards().toString() +"/" + c.getPlayerMatchStats().getRedCards().toString();
		          return new HTML(str);
		        }
		      });
	      
	      columnDefinitions.add(new ColumnDefinition<IPlayerMatchInfo>() {
		        public Widget render(IPlayerMatchInfo c) {
		        	String str = "--";
		        	if (c.getPlayerMatchStats().getTimePlayed() != null) {
		        		str = c.getPlayerMatchStats().getTimePlayed().toString();
		        	}
		          return new HTML(str);
		        }
		      });
  	} 	
    }

    public List<ColumnDefinition<IPlayerMatchInfo>> getColumnDefinitions() {
      return columnDefinitions;
    }

	public static PlayerListView.Listener<IPlayerMatchInfo> getListener() {
		return listener;
	}

	public static void setListener(Listener<IPlayerMatchInfo> listener2) {
		listener = listener2;
	}
	
	public static ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("<img src='resources/cb.jpg'>");
		headers.add( "Name");
		headers.add( "Stats");
		headers.add( "Rating");		
		headers.add( "Pos");
		headers.add( "T/A");
		headers.add( "Pts");
		headers.add( "K/P/R");
		headers.add( "MR");
		headers.add( "CB");
		headers.add( "DB");
		headers.add( "OL");
		headers.add( "TO");
		headers.add( "Tack");
		headers.add( "LO");
		headers.add( "Pen");
		headers.add( "T/R");
		headers.add( "Time");
		
		return headers;
	}
  }
