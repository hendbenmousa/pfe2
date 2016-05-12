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

import java.util.List;

import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.main.Level;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.ControllerException;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.x509.SignatureForm;

/**
 * TODO
 */
public class SignatureStep extends WizardStep<SignatureModel, SignatureWizardController> {

	/**
	 * The default constructor for SignatureStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public SignatureStep(final SignatureModel model, final WizardView<SignatureModel, SignatureWizardController> view, final SignatureWizardController controller) {
		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {

	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getBackStep() {
		return FileStep.class;
	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getNextStep() {

		final Parameters parameters = getController().getParameter();
		final List<SignatureTokenType> tokenTypeList = parameters.getTokenTypeList();
		if (tokenTypeList.size() == 1) {

			final SignatureTokenType signatureTokenType = tokenTypeList.get(0);
			getModel().setTokenType(signatureTokenType);
			switch (signatureTokenType) {
				case MSCAPI:
					return CertificateStep.class;
				case PKCS11:
					return PKCS11Step.class;
				case PKCS12:
					return PKCS12Step.class;
				default:
					throw new RuntimeException("Cannot evaluate token type");
			}
		}
		return TokenStep.class;
	}

	@Override
	protected int getStepProgression() {
		return 2;
	}

	@Override
	protected void init() {

		final SignatureModel model = getModel();
		final Parameters parameters = getController().getParameter();
		final List<SignatureForm> formatList = parameters.getFormList();
		final List<SignaturePackaging> packagingList = parameters.getPackagingList();
		final List<Level> levelList = parameters.getLevelList();

		if (formatList.size() == 1) {

			final SignatureForm signatureForm = formatList.get(0);
			model.setForm(signatureForm);
		}
		if (packagingList.size() == 1) {

			final SignaturePackaging signaturePackaging = packagingList.get(0);
			model.setPackaging(signaturePackaging);
		}
		if (levelList.size() == 1) {

			final Level level = levelList.get(0);
			model.setLevel(level);
		}
	}

	@Override
	protected boolean isValid() {

		final SignatureModel model = getModel();
		return model.getForm() != null && model.getPackaging() != null && model.getLevel() != null;
	}
}
