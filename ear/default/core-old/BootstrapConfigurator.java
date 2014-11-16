package net.rugby.foundation.core.resources;

import com.github.gwtbootstrap.client.ui.config.Configurator;
import com.github.gwtbootstrap.client.ui.resources.Resources;
import com.google.gwt.core.client.GWT;

public class BootstrapConfigurator implements Configurator {
	public Resources getResources() {
		return GWT.create(BootstrapResources.class);
	}

	public boolean hasResponsiveDesign() {
		return true;
	}
}