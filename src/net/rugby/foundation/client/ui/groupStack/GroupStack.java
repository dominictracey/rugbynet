package net.rugby.foundation.client.ui.groupStack;

import java.util.ArrayList;
import net.rugby.foundation.client.ClientFactory;
import net.rugby.foundation.client.place.Browse;
import net.rugby.foundation.client.ui.GroupBrowser;
import net.rugby.foundation.model.shared.Group;
import net.rugby.foundation.model.shared.Position;
import net.rugby.foundation.model.shared.Group.GroupType;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

public class GroupStack<T extends Group> {
	private CellList<T> cellList;
	private Label header;
	private ArrayList<T> arrayList = null;
	private GroupBrowser parent;
	private ClientFactory clientFactory;
	private boolean initialized = false;
	
	public CellList<T> getCellList() {
		return cellList;
	}

	public void setCellList(CellList<T> cellList) {
		this.cellList = cellList;
	}

	public Label getHeader() {
		return header;
	}

	public void setHeader(Label header) {
		this.header = header;
	}

	public GroupStack(GroupBrowser parent, ClientFactory clientFactory) {
		
		this.parent = parent;
		this.clientFactory = clientFactory;

	}
	
	/**
	* A custom {@link Cell} used to render a {@link Position}.
	*/
	private class GroupCell extends AbstractCell<T> {

		@Override
		public void render(Context context, T value, SafeHtmlBuilder sb) {
			if (value != null) {
				if (value.getDisplayName() != null)
					sb.appendEscaped(value.getDisplayName());
			}
		}
	}
	
	public void Init(final GroupType type, ArrayList<T> arrayList, final Long compID)
	{

  	    /*
  	     * Define a key provider for a Position. We use the unique ID as the key,
  	     * which allows to maintain selection even if the name changes.
  	     */
  	    ProvidesKey<T> keyProvider = new ProvidesKey<T>() {
  	      public Object getKey(T item) {
  	        // Always do a null check.
  	        return (item == null) ? null : item.getId();
  	      }
  	    };
  	    
  	    // Add a selection model to handle user selection.
  	    final SelectionModel<T> selectionModel = new SingleSelectionModel<T>();
  	    
  	    cellList = new CellList<T>(new GroupCell(), keyProvider);
  	    cellList.setSelectionModel(selectionModel);
  	    
  	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
  	    public void onSelectionChange(SelectionChangeEvent event) {
  	        T selected = ((SingleSelectionModel<T>)selectionModel).getSelectedObject();
  	        if (selected != null) {
  	        	clientFactory.getPlaceController().goTo(new Browse(compID, type, selected.getId(), 0L));
  	        }
  	      }
  	    });

  	    // get groups of proper type to populate stack

  	    cellList.setRowCount(arrayList.size());
  	    cellList.setRowData(arrayList);		
  	    cellList.setVisible(false);
  	    parent.getStackPanel().add(cellList, "", 2);		

	}
	  
	public void clear()
	{
		T selected = ((SingleSelectionModel<T>)cellList.getSelectionModel()).getSelectedObject();
		if (selected != null)
		{
			cellList.getSelectionModel().setSelected(selected, false);
		}
	}
	
	public void addCellList() {
  	  cellList.setVisible(true);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

}
