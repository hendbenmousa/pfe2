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

import java.io.File;
import java.util.List;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.applet.PinInputDialog;
import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.ControllerException;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;
import eu.europa.esig.dss.token.SignatureTokenConnection;

/**
 * TODO
 */
public class CertificateStep extends WizardStep<SignatureModel, SignatureWizardController> {

	/**
	 * The default constructor for CertificateStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public CertificateStep(final SignatureModel model, final WizardView<SignatureModel, SignatureWizardController> view, final SignatureWizardController controller) {
		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {

	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getBackStep() {
		//		return SignatureDigestAlgorithmStep.class;

		final Parameters parameter = getController().getParameter();
		final List<SignatureTokenType> tokenTypeList = parameter.getTokenTypeList();
		if (tokenTypeList.size() == 1) {

			final SignatureTokenType signatureTokenType = tokenTypeList.get(0);
			if (signatureTokenType == SignatureTokenType.PKCS12) {
				return PKCS12Step.class;
			}
			if (signatureTokenType == SignatureTokenType.PKCS11) {
				return PKCS11Step.class;
			}
			if (parameter.getFormList().size() == 1 && parameter.getPackagingList().size() == 1 && parameter.getLevelList().size() == 1) {
				return FileStep.class;
			}
			return SignatureStep.class;
		}
		return TokenStep.class;
	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getNextStep() {

		final Parameters parameter = getController().getParameter();
		final String claimedRole = parameter.getClaimedRole();
		if (claimedRole != null) {
			return PersonalDataStep.class;
		}
		return SaveStep.class;
	}

	@Override
	protected int getStepProgression() {
		return 4;
	}

	@Override
	protected void init() throws ControllerException {

		final SignatureModel model = getModel();

		SignatureTokenConnection tokenConnection;
		switch (model.getTokenType()) {

			case MSCAPI:

				tokenConnection = new MSCAPISignatureToken(new PinInputDialog(getController().getCore()));
				break;
			case PKCS11:

				final File pkcs11File = model.getPkcs11File();
				tokenConnection = new Pkcs11SignatureToken(pkcs11File.getAbsolutePath(), model.getPkcs11Password().toCharArray());
				break;
			case PKCS12:

				tokenConnection = new Pkcs12SignatureToken(model.getPkcs12Password(), model.getPkcs12File());
				break;
			default:
				throw new RuntimeException("No token connection selected");
		}
		try {
			model.setTokenConnection(tokenConnection);
			model.setPrivateKeys(tokenConnection.getKeys());
		} catch (final DSSException e) {
			throw new ControllerException(e);
		}
	}

	@Override
	protected boolean isValid() {
		return getModel().getSelectedPrivateKey() != null;
	}
}
