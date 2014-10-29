package net.rugby.foundation.topten.server.utilities.notes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

import net.rugby.foundation.core.server.factory.IPlayerFactory;
import net.rugby.foundation.topten.model.shared.INote;
import net.rugby.foundation.topten.model.shared.INote.LinkType;
import net.rugby.foundation.topten.model.shared.ITopTenList;
import net.rugby.foundation.topten.model.shared.Note;
import net.rugby.foundation.topten.server.factory.ITopTenListFactory;
import net.rugby.foundation.topten.server.model.INotesRenderer;

public class TwitterNotesRenderer implements INotesRenderer {

	private IPlayerFactory pf;
	private ITopTenListFactory ttlf;
	protected Map<String, String> templateMap = new HashMap<String, String>();

	@Inject
	public TwitterNotesRenderer(IPlayerFactory pf, ITopTenListFactory ttlf) {
		this.pf = pf;
		this.ttlf = ttlf;
		try {
			BufferedReader reader = new BufferedReader(new FileReader("ear\\default\\resources\\notesTemplates\\default.txt")); 

			// read into dictionary
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.contains("=")) {
					// TT = [P1] was on [CONTEXT] [LINK]
					String key = line.split("=")[0].trim();
					String val = line.split("=")[1].trim();
					templateMap.put(key, val);
				}
			}
		} catch (Throwable e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.SEVERE, "ctor" + e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String render(List<INote> notes, ITopTenList context, boolean includeDetails) {
		StringBuilder builder = new StringBuilder();
		for (INote note : notes) {
			builder.append(render(note, context, includeDetails));
		}
		return builder.toString();
	}

	@Override
	public String render(INote note, ITopTenList context, boolean includeDetails) {
		StringBuilder builder = new StringBuilder(templateMap.get(note.getTemplateSelector()));

		if (note.getPlayer1Id() != null) {
			String name = pf.get(note.getPlayer1Id()).getTwitterHandle();
			if (name == null || name.isEmpty()) {
				name = pf.get(note.getPlayer1Id()).getDisplayName();
			}
			swap(builder, Note.PLAYER1, name);
		}

		if (note.getPlayer2Id() != null) {
			String name = pf.get(note.getPlayer2Id()).getTwitterHandle();
			if (name == null || name.isEmpty()) {
				name = pf.get(note.getPlayer2Id()).getDisplayName();
			}
			swap(builder, Note.PLAYER2, name);
		}

		if (note.getPlayer3Id() != null) {
			String name = pf.get(note.getPlayer3Id()).getTwitterHandle();
			if (name == null || name.isEmpty()) {
				name = pf.get(note.getPlayer3Id()).getDisplayName();
			}
			swap(builder, Note.PLAYER3, name);
		}

		if (includeDetails && note.getDetails() != null) {
			swap(builder, Note.DETAILS, note.getDetails());			
		} else {
			swap(builder, Note.DETAILS, "");			
		}

		if (context != null && context.getId().equals(note.getContextListId())) {
			// just blank out the context and link (since we are going to show this with the TTL, which is the context)
			swap(builder, Note.CONTEXT, "");
			swap(builder, Note.LINK, "");
		} else {
			ITopTenList ttl = ttlf.get(note.getContextListId());
			swap(builder, Note.CONTEXT, " on " +  ttl.getTitle());
			swap(builder, Note.LINK, "http://rugby.net/" + note.getLink());
		}

		// same with the link
		swap(builder, Note.LINK, "http://rugby.net/" + note.getLink());

		return builder.toString() + "\n";
	}

	private boolean swap(StringBuilder builder, String from, String to) {
		int index = builder.indexOf(from);
		while (index != -1)
		{
			builder.replace(index, index + from.length(), to);
			index += to.length(); // Move to the end of the replacement
			index = builder.indexOf(from, index);
		}

		return true;
	}

}
