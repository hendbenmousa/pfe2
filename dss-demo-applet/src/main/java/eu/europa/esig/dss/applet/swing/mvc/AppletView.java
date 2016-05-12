/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p/>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p/>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.applet.swing.mvc;

import java.awt.*;

import eu.europa.esig.dss.applet.util.ComponentFactory;

/**
 *
 * @param <M>
 * @param <C>
 */
public abstract class AppletView<M, C extends AppletController<? extends AppletCore, M>> {

	protected static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppletView.class);

	private final AppletCore core;
	private final M model;
	private final C controller;

	/**
	 * The default constructor for AbstractView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public AppletView(final AppletCore core, final C controller, final M model) {

		this.core = core;
		this.controller = controller;
		this.model = model;
	}

	/**
	 * @return the controller
	 */
	public C getController() {
		return controller;
	}

	/**
	 * @return
	 */
	protected AppletCore getCore() {
		return core;
	}

	/**
	 * @return the model
	 */
	public M getModel() {
		return model;
	}

	/**
	 * @return
	 */
	protected abstract Container layout();

	/**
	 *
	 */
	public void show() {

		final Container content = layout();
		ComponentFactory.updateDisplay(getCore(), content);
	}
}