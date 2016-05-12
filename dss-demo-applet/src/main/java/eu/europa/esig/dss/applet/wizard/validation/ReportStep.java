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
package eu.europa.esig.dss.applet.wizard.validation;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.applet.model.ValidationModel;
import eu.europa.esig.dss.applet.swing.mvc.ControllerException;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;

/**
 * TODO
 */
public class ReportStep extends WizardStep<ValidationModel, ValidationWizardController> {
	/**
	 * The default constructor for ReportStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public ReportStep(final ValidationModel model, final WizardView<ValidationModel, ValidationWizardController> view, final ValidationWizardController controller) {
		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {
		// TODO Auto-generated method stub
	}

	@Override
	protected Class<? extends WizardStep<ValidationModel, ValidationWizardController>> getBackStep() {
		return FormStep.class;
	}

	@Override
	protected Class<? extends WizardStep<ValidationModel, ValidationWizardController>> getNextStep() {
		return null;
	}

	@Override
	protected int getStepProgression() {
		return 2;
	}

	@Override
	protected void init() throws ControllerException {
		try {
			getController().validateDocument();
		} catch (final DSSException e) {
			throw e;
		}
	}

	@Override
	protected boolean isValid() {
		return false;
	}
}