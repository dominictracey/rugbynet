package net.rugby.foundation.core.client.resources.js;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface JsBundle extends ClientBundle {
	public static final JsBundle INSTANCE =  GWT.create(JsBundle.class);

//	@Source("theme.js")
//	public JsResource themeJs();

}
