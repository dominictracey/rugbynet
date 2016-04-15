package net.rugby.foundation.admin.client.ui.playerpopup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.CheckBox;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.client.ClientFactory.GetCountryListCallback;
import net.rugby.foundation.admin.client.ClientFactory.GetPositionListCallback;
import net.rugby.foundation.admin.client.ui.FieldDefinition;
import net.rugby.foundation.model.shared.ICountry;
import net.rugby.foundation.model.shared.IPlayer;
import net.rugby.foundation.model.shared.Position.position;

public class PlayerPopupViewFieldDefinitions<T> {
    private static List<FieldDefinition<IPlayer>> fieldDefinitions =
      new ArrayList<FieldDefinition<IPlayer>>();

//	@UiField Label id;
//	@UiField TextBox scrumId;	
//	@UiField TextBox displayName;
//	@UiField DatePicker birthDate;
//	@UiField TextBox height;
//	@UiField TextBox weight;
//	@UiField TextBox imageUri;
//	//@UiField Long countryId;
//	@UiField ListBox country;
//	@UiField ListBox position;
//	@UiField TextBox numCaps;
//	@UiField TextBox givenName;
//	@UiField TextBox surName;
//	@UiField TextBox shortName;
	
    public PlayerPopupViewFieldDefinitions(final ClientFactory clientFactory) {
    	if (fieldDefinitions.isEmpty()) {

    		
    		fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//id
	        private Label w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (Label) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c != null && c.getId() != null) {
	        		w.setText(c.getId().toString());
	        	}
	        	
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				// can't edit id
				return p;
			}
			
	      });

    		fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//scrumId
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c != null && c.getScrumId() != null)
	        		w.setText(c.getScrumId().toString());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				Long l = null;
				try {
					l = Long.valueOf(w.getText());
				} catch (NumberFormatException e) {
					// it's no good
				}
				if (l != null) {
					p.setScrumId(l);
				}
				return p;
			}
			
	      });
    		
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//displayName
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getDisplayName());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setDisplayName(w.getText());

				return p;
			}
			
	      });
    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//birthDate
	        private DatePicker w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (DatePicker) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c.getBirthDate() != null) {
	        		w.setValue(c.getBirthDate(), true);
	        		w.setCurrentMonth(c.getBirthDate());
	        	}
	        	return w;
	        }

			@Override
			public void clear() {
				w.setValue(new Date(), true);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setBirthDate(w.getLastDate());

				return p;
			}
			
	      });
    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//height
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c.getHeight() != null) {
	        		w.setText(c.getHeight().toString());
	        	}
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				Float l = null;
				try {
					l = Float.valueOf(w.getText());
				} catch (NumberFormatException e) {
					// it's no good
				}
				if (l != null) {
					p.setHeight(l);
				}

				return p;
			}
			
	      });
    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//weight
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c.getWeight() != null) {
	        		w.setText(c.getWeight().toString());
	        	}
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				Float l = null;
				try {
					l = Float.valueOf(w.getText());
				} catch (NumberFormatException e) {
					// it's no good
				}
				if (l != null) {
					p.setWeight(l);
				}

				return p;
			}
			
	      });   	
    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//imageUri
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getImageUri());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setImageUri(w.getText());

				return p;
			}
			
	      });
    	

    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//country
	        private ListBox w;
	        // need this bit of gorminess as the first player is passed in before the country list is completely loaded.
	        private Long selectedId = -1L;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (ListBox) in;
	        	clientFactory.getCountryListAsync(new GetCountryListCallback() {

					@Override
					public void onCountryListFetched(List<ICountry> countries) {
						int count = 0;
						for (ICountry c : countries) {
							w.addItem(c.getName(), c.getId().toString());
							// check to see if the one we are adding is the one we want selected (as set in render())
							if (selectedId.equals(c.getId())) {
								w.setItemSelected(count, true);
							}
							count++;
						}
						

					}

	        	});
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (w.getItemCount() > 0) {
		        	int i = 0;
		        	String index = w.getItemText(i);
		        	if (c.getCountry() != null) {
			        	while (!c.getCountry().getName().equals(index) && i < w.getItemCount()) {
			        		++i;
			        		index = w.getItemText(i);
			        	}
			        	
			        	w.setItemSelected(i, true);
		        	}
	        	} else {
	        		selectedId = c.getCountryId();
	        	}
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				//w.setItemSelected(0, false);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				int val = w.getSelectedIndex();
				
				if (val != -1) {
					p.setCountryId(Long.valueOf(w.getValue(val)));
				}
				
				return p;
			}
			
	      });
    	
       	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//position
	        private ListBox w;
	        // need this bit of gorminess as the first player is passed in before the country list is completely loaded.
	        private Integer selectedId = -1;
	        private List<position> list = null;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (ListBox) in;
	        	clientFactory.getPositionListAsync(new GetPositionListCallback() {

					@Override
					public void onPositionListFetched(List<position> positions) {
						list = positions;
						int count = 0;
						for (position c : positions) {
							w.addItem(c.getName(), Integer.valueOf(c.ordinal()).toString());
							// check to see if the one we are adding is the one we want selected (as set in render())
							if (selectedId.equals(Integer.valueOf(c.ordinal()))) {
								w.setItemSelected(count, true);
							}
							count++;
						}
						

					}

	        	});
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (w.getItemCount() > 0) {
		        	int i = 0;
		        	String index = w.getItemText(i);
		        	if (c.getPosition() != null) {
			        	while (!c.getPosition().getName().equals(index) && i < w.getItemCount()) {
			        		++i;
			        		index = w.getItemText(i);
			        	}
			        	
			        	w.setItemSelected(i, true);
		        	}
	        	} else {
	        		if (c != null && c.getPosition() != null) {
	        			selectedId = c.getPosition().ordinal();
	        		}
	        	}
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				//w.setItemSelected(0, false);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				int val = w.getSelectedIndex();
				
				if (val != -1) {
					p.setPosition(list.get(Integer.valueOf(w.getValue(val))));
				}
				
				return p;
			}
			
	      });


    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//numCaps
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	if (c != null && c.getNumCaps() != null) {
	        		w.setText(c.getNumCaps().toString());
	        	}
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				Integer l = null;
				try {
					l = Integer.valueOf(w.getText());
				} catch (NumberFormatException e) {
					// it's no good
				}
				if (l != null) {
					p.setNumCaps(l);
				}

				return p;
			}
			
	      });
    	
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//givenName
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getGivenName());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setGivenName(w.getText());

				return p;
			}
			
	      });
    

    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//surName
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getSurName());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setSurName(w.getText());

				return p;
			}
			
	      });
    	

    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//shortName
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getShortName());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setShortName(w.getText());

				return p;
			}
			
	      });
    	
		fieldDefinitions.add(new FieldDefinition<IPlayer>() {
		//scrumLink
        private Anchor w;
        
        @Override
        public void bind (Widget in)
        {
        	w = (Anchor) in;
        }
        
        @Override
		public Widget render(final IPlayer c) {
        	w.setText("Link to player on scrum.com");
        	if (c != null && c.getScrumId() != null) {
        		w.setHref("http://www.espnscrum.com/scrum/rugby/player/" + c.getScrumId().toString() + ".html");
        	}
        	w.setTarget("_blank");
        	
        	
        	return w;
        }

		@Override
		public void clear() {
			w.setText(null);
		}
		
		@Override
		public IPlayer update(IPlayer p) {
			// can't edit id
			return p;
		}
		
      });
		fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//twitter
	        private TextBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (TextBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setText(c.getTwitterHandle());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setText(null);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setTwitterHandle(w.getText());

				return p;
			}
			
	      });
    	}
    	fieldDefinitions.add(new FieldDefinition<IPlayer>() {
    		//twitterNotAvailable
	        private CheckBox w;
	        
	        @Override
	        public void bind (Widget in)
	        {
	        	w = (CheckBox) in;
	        }
	        
	        @Override
			public Widget render(IPlayer c) {
	        	w.setValue(c.getTwitterNotAvailable());
	        		
	        	return w;
	        }

			@Override
			public void clear() {
				w.setValue(false);
			}
			
			@Override
			public IPlayer update(IPlayer p) {
				p.setTwitterNotAvailable(w.getValue());

				return p;
			}
			
	      });
    	
    fieldDefinitions.add(new FieldDefinition<IPlayer>() {
		//twitterGoogle
        private Anchor w;
        
        @Override
        public void bind (Widget in)
        {
        	w = (Anchor) in;
        }
        
        @Override
		public Widget render(IPlayer c) {
        	w.setText("Google");
    		String target;
    		target = "https://www.google.com/search?sourceid=chrome-psyapi2&ion=1&espv=2&ie=UTF-8&q="+ URL.encode(c.getDisplayName() + " rugby twitter ");
    		w.setHref(target);
    		w.setTarget("_blank");
        	return w;
        }

		@Override
		public void clear() {
			w.setText(null);
		}
		
		@Override
		public IPlayer update(IPlayer p) {

			return p;
		}
		
      });
	}
    

    public List<FieldDefinition<IPlayer>> getFieldDefinitions() {
      return fieldDefinitions;
    }
  }
