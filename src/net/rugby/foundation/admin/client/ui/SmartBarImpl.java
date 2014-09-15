package net.rugby.foundation.admin.client.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.rugby.foundation.admin.client.place.AdminCompPlace;
import net.rugby.foundation.admin.client.place.AdminCompPlace.Filter;
import net.rugby.foundation.admin.client.place.AdminOrchPlace;
import net.rugby.foundation.admin.client.place.AdminTaskPlace;
import net.rugby.foundation.admin.client.place.PortalPlace;
import net.rugby.foundation.admin.client.place.SeriesPlace;
import net.rugby.foundation.admin.client.ui.EditContent.EditContentPresenter;
import net.rugby.foundation.model.shared.IContent;
import net.rugby.foundation.model.shared.IRatingEngineSchema;
import net.rugby.foundation.model.shared.ScrumMatchRatingEngineSchema;

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
	@UiField MenuBar smartBar;
	@UiField MenuItem compMenuNew;
	@UiField MenuItem compMenuAll;
	@UiField MenuItem compMenuUnderway;
	@UiField MenuBar compBar;
	@UiField MenuItem orchMenuShow;
	@UiField MenuBar orchBar;
	@UiField MenuItem taskMenuShow;
	@UiField MenuBar taskBar;
	@UiField MenuBar portalBar;
	@UiField MenuItem portalMenuShow;
	@UiField MenuItem seriesMenuShow;
	@UiField MenuBar schemaBar;
	@UiField MenuItem schemaMenuNew;
	@UiField MenuBar schemaList;
	@UiField MenuItem adminMenuFlushAllPipelineJobs;
	@UiField MenuItem adminMenuCreateContent;
	@UiField MenuItem adminMenuCleanup;
	@UiField MenuBar contentList;

	private SchemaPresenter schemaListener;
	private EditContentPresenter contentListener;
	
	public SmartBarImpl() {
		initWidget(binder.createAndBindUi(this));

	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setPresenter(final Presenter listener) {
		this.listener = listener;
		taskMenuShow.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new AdminTaskPlace("filter=ALL"));
			}

		});
		compMenuAll.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new AdminCompPlace(Filter.ALL));
			}
		});
		compMenuUnderway.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new AdminCompPlace(Filter.UNDERWAY));
			}
		});
		portalMenuShow.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new PortalPlace(""));
			}
		});
		seriesMenuShow.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new SeriesPlace(""));
			}
		});
		adminMenuFlushAllPipelineJobs.setCommand(new Command() {

			@Override
			public void execute() {
				listener.flushAllPipelineJobs();	
			}

		});
		adminMenuCleanup.setCommand(new Command() {

			@Override
			public void execute() {
				listener.cleanUp();	
			}

		});
		orchMenuShow.setCommand(new Command() {
			@Override
			public void execute() {
				listener.goTo(new AdminOrchPlace(""));	
			}	
		});

	}

	@Override
	public void setSchemaPresenter(final SchemaPresenter listener) {
		this.schemaListener = listener;
		schemaMenuNew.setCommand(new Command() {
			@Override
			public void execute() {
				schemaListener.createSchema();
			}

		});
	}


	/* (non-Javadoc)
	 * @see net.rugby.foundation.game1.client.ui.SmartBar#setComps(java.util.Map)
	 */
	@Override
	public void setComps(Map<Long, String> result) {
		compBar.clearItems();
		for (final Long id : result.keySet()) {
			compBar.addItem(new MenuItem(result.get(id), new Command()
			{
				public void execute()
				{
					listener.compPicked(id);
				}
			}));
		}
	}

	@Override
	public void setSchemas(List<ScrumMatchRatingEngineSchema> schemas) {
		schemaList.clearItems();
		if (schemas != null) {
			Iterator<ScrumMatchRatingEngineSchema> it = schemas.iterator();
			while (it.hasNext()) {
				final IRatingEngineSchema s = it.next();
				String name = s.getName();
				if (s.getIsDefault()) {
					name += " (Default)";
				}
				schemaList.addItem(new MenuItem(name, new Command()
				{
					public void execute()
					{
						schemaListener.editSchema(s);
					}
				}));
			}		

		}
	}
	

	@Override
	public void setContents(List<IContent> contents, EditContentPresenter listener) {
		this.contentListener = listener;
		adminMenuCreateContent.setCommand(new Command() {
			@Override
			public void execute() {
				contentListener.createContent();
			}

		});
		
		contentList.clearItems();
		if (contents != null) {
			Iterator<IContent> it = contents.iterator();
			while (it.hasNext()) {
				final IContent s = it.next();
				String name = s.getTitle();

				contentList.addItem(new MenuItem(name, new Command()
				{
					public void execute()
					{
						contentListener.editContent(s);
					}
				}));
			}		

		}
	}
}
