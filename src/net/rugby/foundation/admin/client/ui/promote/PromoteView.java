package net.rugby.foundation.admin.client.ui.promote;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Modal;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import net.rugby.foundation.admin.client.ClientFactory;
import net.rugby.foundation.admin.shared.IBlurb;

public interface PromoteView<T extends IBlurb> extends IsWidget {
	public interface PromoteViewPresenter<T> {
		ClientFactory getClientFactory();
		
		void DeleteBlurb(IBlurb blurb);
		
		void Deactivate(List<IBlurb> blurbs);

		void newBlurb(String text, String text2, String text3);
		
		Boolean isSelected(T c);
		Boolean onItemSelected(T c);
		Boolean onItemClicked(T c, int row);
		void deleteSelected();
		List<T> getSelected();

		void digestPreview(String text, List<Long> blurbIds);

		void sendDigest(String text, List<Long> blurbIds);

		void archive();
		void facebook();
		void twitter();

		void onBulkUploadSaved(String text);

		
	} 



	public abstract void showList(List<T> blurbs);

	public abstract void setPresenter(PromoteViewPresenter<T> p);

	public abstract void showError(T blurb, int index, String message);

	void setColumnHeaders(ArrayList<String> headers);

	void showWait();

	void setColumnDefinitions(PromoteViewColumnDefinitions<T> defs);

	public abstract void updateBlurbRow(T blurb);

	PromoteViewPresenter<T> getPresenter();

	public abstract void setClientFactory(ClientFactory clientFactory);

	public abstract Modal getBlurbModal();

	public abstract void showBlurb(IBlurb c);

	void showPreview(String text);


}