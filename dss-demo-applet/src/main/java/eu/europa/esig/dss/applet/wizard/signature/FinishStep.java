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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.ControllerException;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;

/**
 * TODO
 */
public class FinishStep extends WizardStep<SignatureModel, SignatureWizardController> {

	/**
	 * The default constructor for SignStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public FinishStep(final SignatureModel model, final WizardView<SignatureModel, SignatureWizardController> view, final SignatureWizardController controller) {

		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {

		// TODO Auto-generated method stub
	}

	@Override
	protected void init() throws ControllerException {

		try {
			getController().signDocument();
		} catch (final IOException e) {
			throw new DSSException(e);
		} catch (final NoSuchAlgorithmException e) {
			throw new DSSException(e);
		} catch (DSSException e) {
			if (e.getCause() != null && (e.getCause() instanceof SignatureException && getModel().getTokenType() == SignatureTokenType.MSCAPI) || (e
				  .getCause() instanceof javax.crypto.BadPaddingException && getModel().getTokenType() == SignatureTokenType.PKCS11)) {
				// probably because the digest algorithm is not supported by the card
				throw new ControllerException(String.format("Error when signing. The chosen digest algorithm (%s) might not be supported by your signing device.",
					  getModel().getSignatureDigestAlgorithm()), e);
			}
			throw e;
		}

	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getBackStep() {

		return SaveStep.class;
	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getNextStep() {

		return null;
	}

	@Override
	protected int getStepProgression() {

		return 7;
	}

	@Override
	protected boolean isValid() {

		return false;
	}
}
