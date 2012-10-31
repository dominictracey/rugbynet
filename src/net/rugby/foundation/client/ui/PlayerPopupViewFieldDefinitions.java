package net.rugby.foundation.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.model.shared.MatchPopupData;
import net.rugby.foundation.model.shared.PlayerPopupData;
import net.rugby.foundation.model.shared.PositionEnUs;

public class PlayerPopupViewFieldDefinitions<T> {
    private static List<FieldDefinition<PlayerPopupData>> fieldDefinitions =
      new ArrayList<FieldDefinition<PlayerPopupData>>();

    public PlayerPopupViewFieldDefinitions() {
    	if (fieldDefinitions.isEmpty()) {

    		
    		fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
    		//picture
	        private HTML w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	//TODO type check it
	        	w = (HTML)in;
	        }
	        
	        @Override
			public Widget render(PlayerPopupData c) {
	        	if(c.isThumbnail())
	        		w.setHTML("<img src=/resources/" + c.getRowData().getTeamID() + "/" + c.getId() + ".jpg");
	        	else  //the flag
	        		w.setHTML("<img src=/resources/" + c.getRowData().getTeamID() + "/" + "flag_popup.jpg");
	        		
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public PlayerPopupData update(PlayerPopupData p) {
				// what does this mean -- not editable
				return p;
			}
			
	      });
    		
        	fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
        		//details
    	        private HTML w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	//TODO type check it
    	        	w = (HTML)in;
    	        }
    	        
    	        @Override
    			public Widget render(PlayerPopupData c) {
    	        	PositionEnUs pos = new PositionEnUs(c.getRowData().getPosition());
    	        	String posName = pos.getPrimaryName();

    	        	w.setHTML("<table><tr><td>" + posName +
    	        				"</td></tr><tr><td>" + c.getTeamName() +
    	        				"</td></tr></table>"
    	        			);
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public PlayerPopupData update(PlayerPopupData p) {
    				//p.setSurName( w.getText());
    				return p;
    			}
    			
    	      });

           	
           	fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
        		//Rating table
    	        private HTML w;
    	        private Long overR;
    	        private Long lastR;
    	        private Long origR;
    	        private  String arrowDown = "<img src=resources/arrow_Down_Red.gif>";
    	        private  String arrowUp = "<img src=resources/arrow_Up_Green.gif>";
    	        private  String arrowUnchanged = "<img src=resources/unchanged_Blue.gif>";
   	        

    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	//TODO type check it
    	        	w = (HTML)in;
    	        }
    	        
    	        @Override
    			public Widget render(PlayerPopupData c) {

    	        	overR = c.getRowData().getOverallRating();
    	        	lastR = c.getRowData().getLastRating();
    	        	origR = c.getRowData().getOrigRating();
    	        	
    	        	String overImg = arrowUnchanged;
    	        	if (overR != null && lastR != null) {
	    	        	if (overR > lastR) {
	    	        		overImg = arrowUp;
	    	        	} else if (overR < lastR) {
	    	        		overImg = arrowDown;
	    	        	}
    	        	}
    	        	
    	        	String lastImg = arrowUnchanged;
    	        	if (origR != null && lastR != null) {
	    	        	if (lastR > origR) {
	    	        		lastImg = arrowUp;
	    	        	} else if (lastR < origR) {
	    	        		lastImg = arrowDown;
	    	        	}
    	        	}
    	        	
    	        	String overallRating = ( overR != null) ? overR.toString() : "--";
    	        	String lastRating = (lastR != null) ? lastR.toString() : "--";
    	        	String origRating = (origR != null) ? origR.toString() : "--";
    	        	w.setHTML("<table><tr class=\"popupHeader\">" +
    	        				"<td>Rating</td><td>Last Rating</td><td>Original Rating</td></tr>" +
    	        				"<tr class=\"popupOddRow\">" + "<td>"+overallRating+ "&nbsp;"+ overImg +"</td><td>"+lastRating+ "&nbsp;"+ lastImg+ 
    	        				"</td><td>" + origRating+ "</td></tr></table>" );
    	          return w; 
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public PlayerPopupData update(PlayerPopupData p) {
    				//p.setSurName( w.getText());
    				return p;
    			}
    			
    	      });
           	
          	fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
        		//Match rating table
    	        private HTML w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	//TODO type check it
    	        	w = (HTML)in;
    	        }
    	        
    	        @Override
    			public Widget render(PlayerPopupData c) {
    	        	PositionEnUs pos = new PositionEnUs(c.getRowData().getPosition());
    	        	//TODO grouping map
    	        	String table = "<table><tr class=\"popupHeader\">" +
    	        				"<td>Match</td><td>Rating</td>";
    	        	
    	        	//TODO alternate odd even styles
    	        	if (c.getMatchData() != null) {
    	        		boolean even = true;
    	        		for (MatchPopupData mpd : c.getMatchData()) {
    	        			if (!even) {
    	        				table += "<tr class=\"popupOddRow\">";
    	        				even = true;
    	        			} else {
    	        				table += "<tr class=\"popupEvenRow\">";
    	        				even = false;
    	        			}
    	        			
    	        			table +=  "<td>" + mpd.getMatchDescription()+ "</td><td>"
	        			 	+ mpd.getOverallRating()+ "</td></tr>";
    	        		}
    	        		table += "</table>";
    	        	}
    	        	
    	        	table += "</table>" ;
    	        	
    	        	w.setHTML(table);
    	        	return w; 
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public PlayerPopupData update(PlayerPopupData p) {
    				//p.setSurName( w.getText());
    				return p;
    			}
    			
    	      });
          	
         	fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
        		//playerSheet link
    	        private HTML w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	//TODO type check it
    	        	w = (HTML)in;
    	        }
    	        
    	        @Override
    			public Widget render(PlayerPopupData c) {

//    	        	String table = "<table><tr class=\"popupFooter\">" +
//    	        				"<td><a href=\"/rugbyFoundation.html#ViewPlayerSheetPlace:" + c.getId() + ">Complete Player Sheet: >></a></td></tr>";
//    	        
//    	        	
//    	        	table += "</table>" ;
    	        	
    	        	w.setHTML("");
    	        	return w; 
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public PlayerPopupData update(PlayerPopupData p) {
    				//p.setSurName( w.getText());
    				return p;
    			}
    			
    	      });
         	
    		fieldDefinitions.add(new FieldDefinition<PlayerPopupData>() {
    		//dialog box
	        private DialogBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	//TODO type check it
	        	w = (DialogBox)in;
	        }
	        
	        @Override
			public Widget render(PlayerPopupData c) {
	        	w.setText(c.getRowData().getDisplayName());
	        		
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public PlayerPopupData update(PlayerPopupData p) {
				// what does this mean -- not editable
				return p;
			}
			
	      });
    		
    	}
    }

    public List<FieldDefinition<PlayerPopupData>> getFieldDefinitions() {
      return fieldDefinitions;
    }
  }
