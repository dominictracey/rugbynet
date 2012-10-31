package net.rugby.foundation.game1.client.ui;

import java.util.List;
import java.util.Map;

import net.rugby.foundation.game1.shared.IConfiguration;
import net.rugby.foundation.game1.shared.IEntry;
import net.rugby.foundation.model.shared.IClubhouse;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * Sample implementation of {@link SmartBar}.
 */
public class SmartBarImpl extends Composite implements SmartBar {

	interface Binder extends UiBinder<Widget, SmartBarImpl> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);

	private Presenter listener;
	@UiField
	MenuBar smartBar;
	@UiField
	MenuBar entryBar;
	@UiField
	MenuBar clubhouseBar;
	@UiField
	MenuBar competitionBar;

	public SmartBarImpl() {
		initWidget(binder.createAndBindUi(this));
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar#setComps(java.util.Map)
	 */
	@Override
	public void setComps(Map<Long, String> result) {
		competitionBar.clearItems();
		for (final Long id : result.keySet()) {
			competitionBar.addItem(new MenuItem(result.get(id), new Command()
			{
			    public void execute()
			    {
			        listener.compPicked(id);
			    }
			}));
		}
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar#setClubhouses(java.util.Map)
	 */
	@Override
	public void setClubhouses(List<IClubhouse> clubhouseList, IConfiguration conf) {
		clubhouseBar.clearItems();
		for (final IClubhouse c : clubhouseList) {
			if (!conf.getClubhouseIds().contains(c.getId())) {
				clubhouseBar.addItem(new MenuItem(c.getName(), new Command()
				{
				    public void execute()
				    {
				        listener.clubhousePicked(c.getId());
				    }
				}));
			}
		}		
	}

	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar#setEntries(java.util.Map)
	 */
	@Override
	public void setEntries(List<IEntry> entries) {
		entryBar.clearItems();
		if (entries != null) {
			for (final IEntry e : entries) {
				entryBar.addItem(new MenuItem(e.getName(), new Command()
				{
				    public void execute()
				    {
				        listener.entryPicked(e);
				    }
				}));
			}			
		}
	}

		

}
