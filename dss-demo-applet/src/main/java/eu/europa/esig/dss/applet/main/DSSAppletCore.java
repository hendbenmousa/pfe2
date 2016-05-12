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
package eu.europa.esig.dss.applet.main;

import java.util.Map;

import ance.AnceDataLoader;
import eu.europa.esig.dss.applet.controller.ActivityController;
import eu.europa.esig.dss.applet.model.ActivityModel;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.model.ValidationModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletController;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.util.SigningUtils;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;
import eu.europa.esig.dss.applet.wizard.validation.ValidationWizardController;
import eu.europa.esig.dss.client.tsp.OnlineTSPSource;

/**
 *
 */
@SuppressWarnings("serial")
public class DSSAppletCore extends AppletCore {

	public static final String SETUP_PATH = "setup-path";
	private Parameters parameters;

	/**
	 * @return {@code Parameters}
	 */
	public Parameters getParameters() {
		return parameters;
	}

	@Override
	protected void layout(final AppletCore core) {
		getController(ActivityController.class).display();
	}

	@Override
	protected void registerControllers() {

		final Map<Class<? extends AppletController>, AppletController> controllers = getControllers();
		controllers.put(ActivityController.class, new ActivityController(this, new ActivityModel()));
		controllers.put(SignatureWizardController.class, new SignatureWizardController(this, new SignatureModel()));
		controllers.put(ValidationWizardController.class, new ValidationWizardController(this, new ValidationModel()));
	}

	@Override
	protected void registerParameters(final ParameterProvider parameterProvider) {

		LOG.info("Register applet parameters ");
		final String setupPath = parameterProvider.getParameter(SETUP_PATH);

		parameters = new Parameters(setupPath);

		final OnlineTSPSource onlineTSPSource = new OnlineTSPSource(parameters.getTimestampServerUrl());
		final AnceDataLoader dataLoader = new AnceDataLoader();
		dataLoader.setContentType("application/timestamp-query");
		onlineTSPSource.setDataLoader(dataLoader);

		SigningUtils.setOnlineTSPSource(onlineTSPSource);

		LOG.info("Parameters - {}", parameters);
	}
}