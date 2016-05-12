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
package eu.europa.esig.dss.applet.wizard.signature;

import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.ControllerException;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;

/**
 * TODO
 */
public class TokenStep extends WizardStep<SignatureModel, SignatureWizardController> {

	/**
	 * The default constructor for TokenStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public TokenStep(final SignatureModel model, final WizardView<SignatureModel, SignatureWizardController> view, final SignatureWizardController controller) {
		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {

	}

	@Override
	protected void init() {

	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getBackStep() {

		final Parameters parameters = getController().getParameter();
		if (parameters.getFormList().size() == 1 && parameters.getPackagingList().size() == 1 && parameters.getLevelList().size() == 1) {
			return FileStep.class;
		}
		return SignatureStep.class;
	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getNextStep() {

		//        return SignatureDigestAlgorithmStep.class;
		final SignatureTokenType tokenType = getModel().getTokenType();
		if (tokenType==null) {
			return this.getClass();
		}
		switch (tokenType) {
			case MSCAPI:
				return CertificateStep.class;
			case PKCS11:
				return PKCS11Step.class;
			case PKCS12:
				return PKCS12Step.class;
		}
		return this.getClass();
	}

	@Override
	protected int getStepProgression() {
		return 3;
	}

	@Override
	protected boolean isValid() {

		final SignatureTokenType tokenType = getModel().getTokenType();
		return tokenType != null;
	}
}
