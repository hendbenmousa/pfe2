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
package eu.europa.esig.dss.applet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.applet.main.DSSAppletCore;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.ActivityModel;
import eu.europa.esig.dss.applet.view.ActivityView;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;
import eu.europa.esig.dss.applet.wizard.validation.ValidationWizardController;

/**
 * TODO
 */
public class ActivityController extends DSSAppletController<ActivityModel> {


	private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);


	private ActivityView view;

	/**
	 * The default constructor for ActivityController.
	 *
	 * @param core
	 * @param model
	 */
	public ActivityController(final DSSAppletCore core, final ActivityModel model) {

		super(core, model);
		view = new ActivityView(getCore(), this, getModel());
	}

	/**
	 *
	 */
	public void display() {

		view.show();
		final Parameters parameter = getParameter();
		if (parameter.getUsageList().size() == 1) {
			view.click();
		}
	}

	/**
	 *
	 */
	public void startAction() {

		switch (getModel().getAction()) {
			case SIGN:
				getCore().getController(SignatureWizardController.class).start();
				break;
			case VALIDATE:
				getCore().getController(ValidationWizardController.class).start();
				break;
			default:
				logger.error("Unknown action : " + getModel().getAction());
				break;
		}
	}

	public boolean isValid() {
		return getModel().getAction() != null;
	}
}