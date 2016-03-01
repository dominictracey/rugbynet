package net.rugby.foundation.admin.client.ui.promote;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import net.rugby.foundation.admin.client.ui.ColumnDefinition;
import net.rugby.foundation.admin.client.ui.promote.PromoteView.PromoteViewPresenter;
import net.rugby.foundation.admin.shared.IBlurb;

public class PromoteViewColumnDefinitions<T extends IBlurb> {

	private List<ColumnDefinition<T>> columnDefinitions =
			new ArrayList<ColumnDefinition<T>>();

	private PromoteViewPresenter<T> listener;

	public PromoteViewColumnDefinitions() {
		if (columnDefinitions.isEmpty()) {

			columnDefinitions.add(new ColumnDefinition<T>() {
				public Widget render(T c) {
					CheckBox checkBox = new CheckBox();
					if (listener != null)
						checkBox.setValue(listener.isSelected(c));
					return checkBox;
				}

				public boolean isSelectable() {
					return true;
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});


			columnDefinitions.add(new ColumnDefinition<T>() {
				//id
				public Widget render(final T c) {
					String comp = "";

					comp = c.getLinkText();
					return new HTML(comp);
				}     

				public boolean isClickable() {
					return true;
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			columnDefinitions.add(new ColumnDefinition<T>() {
				// summary
				public Widget render(T c) {
					String pos = c.getCreated().toString();
					return new HTML(pos);
				}

				@Override
				public Column<T, ?> getColumn() {
					// TODO Auto-generated method stub
					return null;
				}
			});

		}
			
	}

	public List<ColumnDefinition<T>> getColumnDefinitions() {
		return columnDefinitions;
	}

	public ArrayList<String> getHeaders() {
		ArrayList<String> headers = new ArrayList<String>();
		headers.add("<img src='resources/cb.jpg'>");
		headers.add("Blurb");
		headers.add( "Date");
		return headers;
	}

	public void setListener(PromoteViewPresenter<T> listener2) {
		this.listener =  listener2;
	}
}
