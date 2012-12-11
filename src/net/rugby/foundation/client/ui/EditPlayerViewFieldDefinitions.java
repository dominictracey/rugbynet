package net.rugby.foundation.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.IGroup;
import net.rugby.foundation.model.shared.ITeamGroup;
import net.rugby.foundation.model.shared.Player;
import net.rugby.foundation.model.shared.Group.GroupType;
import net.rugby.foundation.model.shared.Position.position;
import net.rugby.foundation.model.shared.TeamGroup;

public class EditPlayerViewFieldDefinitions<T> {
	private Widget displayName;
	
	private HashMap<Integer,TeamGroup> teamMap = new HashMap<Integer,TeamGroup>();
	private ListBox teamList;

	
	 		// Create a handler for the sendButton and nameField
	private class NameHandler implements KeyPressHandler {


		@Override
		public void onKeyPress(KeyPressEvent event) {
			String curr = ((TextBox)displayName).getText();
			((TextBox)displayName).setText(curr + event.getCharCode());	
		}
		
	}
	
    private static List<FieldDefinition<Player>> fieldDefinitions =
      new ArrayList<FieldDefinition<Player>>();

    public EditPlayerViewFieldDefinitions(final ClientFactory cf) {
    	


    	if (fieldDefinitions.isEmpty()) {
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//first name
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	
	        	w = (TextBox)in;
	        	NameHandler h = new NameHandler();
	        	w.addKeyPressHandler(h);
	        }
	        
	        @Override
			public Widget render(Player c) {
	        	w.setText(c.getGivenName());
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public Player update(Player p) {
				p.setGivenName(w.getText());
				return p;
			}
			
	      });
    		
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//last name
    	        private TextBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (TextBox)in;
    	        	NameHandler h = new NameHandler();
    	        	w.addKeyPressHandler(h);
    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	w.setText(c.getSurName());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setSurName( w.getText());
    				return p;
    			}
    			
    	      });
        	
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//display name
    	        
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	displayName = (TextBox)in;
    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	((TextBox)displayName).setText(c.getDisplayName());
    	          return displayName;
    	        }

    			@Override
    			public void clear() {
    				((TextBox)displayName).setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setDisplayName( ((TextBox)displayName).getText());
    				return p;
    			}
    			
    	      });       	
        	
//            fieldDefinitions.get(3).bind(position);  
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//position
    	        private ListBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (ListBox)in;
    	        	
    	        	//populate with positions if needed
    	        	if (((ListBox)w).getItemCount() == 0) {
	    	        	for (position p : position.values()) {
	    	        		w.addItem(p.name());
	    	        	}
    	        	}

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	int index = 0;
    	        	for (position p : position.values()) {
    	        		if (p == c.getPosition())
    	        		{
    	        			((ListBox)w).setItemSelected(index, true);
    	        			break;
    	        		}
    	        		index++;
    	        	}
    	        	
    	        	
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				((ListBox)w).setItemSelected(0,true);  //select NONE
    			}
    			
    			@Override
    			public Player update(Player p) {
    	        	for (position pos : position.values()) {
    	        		if (pos.name().equalsIgnoreCase(w.getItemText(w.getSelectedIndex())))
    	        				p.setPosition( pos);
    	        	}
    				return p;
    			}
    			
    	      });       	
//            fieldDefinitions.get(4).bind(numCaps);      
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//num caps
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	
	        	w = (TextBox)in;

	        }
	        
	        @Override
			public Widget render(Player c) {
	        	if (c.getNumberCaps() != null)
	        		w.setText(Integer.toString(c.getNumberCaps()));
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public Player update(Player p) {
				if (!w.getText().isEmpty())
					p.setNumberCaps(Integer.decode(w.getText()));
				return p;
			}
			
	      });
    		
//            fieldDefinitions.get(5).bind(dateOfBirth);  
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//DOB
	        private DateBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	
	        	w = (DateBox)in;

	        }
	        
	        @Override
			public Widget render(Player c) {
	        	w.setValue(c.getDateOfBirth());
	          return w;
	        }

			@Override
			public void clear() {
				w.setValue(null);
			}
			
			@Override
			public Player update(Player p) {
				p.setDateOfBirth(w.getValue());
				return p;
			}
			
	      });
//            fieldDefinitions.get(6).bind(club);        
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//club
    	        private TextBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (TextBox)in;

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	w.setText(c.getClub());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setClub( w.getText());
    				return p;
    			}
    			
    	      });    
//            fieldDefinitions.get(7).bind(height);        
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//height
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	
	        	w = (TextBox)in;

	        }
	        
	        @Override
			public Widget render(Player c) {
	        	if (c.getHeight() != null)
	        	w.setText(Integer.toString(c.getHeight()));
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public Player update(Player p) {
				if (!w.getText().isEmpty())
					p.setHeight(Integer.decode(w.getText()));
				return p;
			}
			
	      });
//            fieldDefinitions.get(8).bind(weight);    
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//weight
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	
	        	w = (TextBox)in;

	        }
	        
	        @Override
			public Widget render(Player c) {
	        	if (c.getWeight() != null)
	        		w.setText(Integer.toString(c.getWeight()));
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public Player update(Player p) {
				if (!w.getText().isEmpty())
					p.setWeight(Integer.decode(w.getText()));
				return p;
			}
			
	      });
       		fieldDefinitions.add(new FieldDefinition<Player>() {
        		//active
    	        private CheckBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (CheckBox)in;

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	if (c.getWeight() != null)
    	        		w.setValue(c.isActive());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setValue(true);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setActive(w.getValue());
    				return p;
    			}
    			
    	      });
    		fieldDefinitions.add(new FieldDefinition<Player>() {
    		//origRating
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {	        	
	        	w = (TextBox)in;
	        }
	        
	        @Override
			public Widget render(Player c) {
	        	if (c.getOrigRating() != null)
	        		w.setText(Long.toString(c.getOrigRating()));
	          return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public Player update(Player p) {
				if (!w.getText().isEmpty())
					p.setOrigRating(Long.decode(w.getText()));
				return p;
			}
			
	      });
//            fieldDefinitions.get(9).bind(bioCredit);        
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//bioCredit
    	        private TextBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (TextBox)in;

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	w.setText(c.getBioCredit());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setBioCredit( w.getText());
    				return p;
    			}
    			
    	      });   
//            fieldDefinitions.get(10).bind(bioUrl);   
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//bioUrl
    	        private TextBox w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (TextBox)in;

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	w.setText(c.getBioURL());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setBioURL( w.getText());
    				return p;
    			}
    			
    	      });   
//            fieldDefinitions.get(11).bind(bioSnippet);      
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//bioSnoppit
    	        private TextArea w;
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	w = (TextArea)in;

    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	w.setText(c.getBioSnippet());
    	          return w;
    	        }

    			@Override
    			public void clear() {
    				w.setText(null);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				p.setBioSnippet( w.getText());
    				return p;
    			}
    			
    	      });   
//            fieldDefinitions.get(12).bind(team);   
        	fieldDefinitions.add(new FieldDefinition<Player>() {
        		//team
    	        
    	        @Override
    	        public void bind (Widget in)
    	        {
    	        	
    	        	teamList = (ListBox)in;
    	        	
    	        	if (teamList.getItemCount() == 0) {
    	        		
	    	    		cf.getRpcService().getGroupsByGroupType(GroupType.TEAM, new AsyncCallback<ArrayList<Group>>() {
	    	  		      public void onSuccess(ArrayList<Group> result) {
	
	    	  		    	  Iterator<Group> iter = result.iterator();
	    	  		    	  int index = 0;
	    	  		    	  while (iter.hasNext()) 	  {
	    	  		    		  IGroup g = iter.next();
	    	  		    		  if (g instanceof TeamGroup) {
	    	  			    		  teamList.addItem(((ITeamGroup)g).getAbbr());
	    	  			    		  teamMap.put(index, (TeamGroup)g);
	    	  			    		  index++;
	    	  		    		  }
	    	  		    	  }
	    	  	    	  
	      	        	}


	    	  			public void onFailure(Throwable caught) {
	    	  		    	  Window.alert("Error populating team dropdown list");
	    	  		      }
	    	  		});   	     

  	  		      }
    	        }
    	        
    	        @Override
    			public Widget render(Player c) {
    	        	//TODO sweet linear search fuckwad
    	        	Long teamID = c.getTeamID();
    	        	for (int index : teamMap.keySet())
    	        	{
    	        		if (teamMap.get(index).getId() == teamID) {
    	        			teamList.setSelectedIndex(index);
    	        			break;
    	        		}
    	        	}
    	        	
    	        	
    	          return teamList;
    	        }

    			@Override
    			public void clear() {
    				teamList.setSelectedIndex(0);
    			}
    			
    			@Override
    			public Player update(Player p) {
    				int index = teamList.getSelectedIndex();
    				if (teamMap.containsKey(index)) {			
    					
	    				p.setTeamID(teamMap.get(index).getId());
//	    				p.setTeamAbbr((teamMap.get(index).getAbbr()));
//	    	    		p.setTeamName((teamMap.get(index).getDisplayName()));
//	    	    		p.setPool(teamMap.get(index).getPool());
    				}
    	        	
    				return p;
    			}
    			
    	      }); 
        	
    	}
    }

    public List<FieldDefinition<Player>> getFieldDefinitions() {
      return fieldDefinitions;
    }
  }
