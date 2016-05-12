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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import ance.CertificateValidationService;
import eu.europa.esig.dss.AbstractSignatureParameters;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.SignatureLevel;
import eu.europa.esig.dss.TimestampParameters;
import eu.europa.esig.dss.applet.controller.ActivityController;
import eu.europa.esig.dss.applet.controller.DSSWizardController;
import eu.europa.esig.dss.applet.main.DSSAppletCore;
import eu.europa.esig.dss.applet.main.Level;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.util.SigningUtils;
import eu.europa.esig.dss.applet.view.signature.CertificateView;
import eu.europa.esig.dss.applet.view.signature.FileView;
import eu.europa.esig.dss.applet.view.signature.FinishView;
import eu.europa.esig.dss.applet.view.signature.PKCS11View;
import eu.europa.esig.dss.applet.view.signature.PKCS12View;
import eu.europa.esig.dss.applet.view.signature.PersonalDataView;
import eu.europa.esig.dss.applet.view.signature.SaveView;
import eu.europa.esig.dss.applet.view.signature.SignatureDigestAlgorithmView;
import eu.europa.esig.dss.applet.view.signature.SignatureView;
import eu.europa.esig.dss.applet.view.signature.TokenView;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.x509.SignatureForm;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;

import static eu.europa.esig.dss.SignatureLevel.CAdES_BASELINE_B;
import static eu.europa.esig.dss.SignatureLevel.CAdES_BASELINE_LT;
import static eu.europa.esig.dss.SignatureLevel.CAdES_BASELINE_LTA;
import static eu.europa.esig.dss.SignatureLevel.CAdES_BASELINE_T;
import static eu.europa.esig.dss.SignatureLevel.PAdES_BASELINE_B;
import static eu.europa.esig.dss.SignatureLevel.PAdES_BASELINE_LT;
import static eu.europa.esig.dss.SignatureLevel.PAdES_BASELINE_LTA;
import static eu.europa.esig.dss.SignatureLevel.PAdES_BASELINE_T;
import static eu.europa.esig.dss.SignatureLevel.XAdES_BASELINE_B;
import static eu.europa.esig.dss.SignatureLevel.XAdES_BASELINE_LT;
import static eu.europa.esig.dss.SignatureLevel.XAdES_BASELINE_LTA;
import static eu.europa.esig.dss.SignatureLevel.XAdES_BASELINE_T;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_B;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_LT;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_LTA;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_T;
import static eu.europa.esig.dss.x509.SignatureForm.CAdES;
import static eu.europa.esig.dss.x509.SignatureForm.PAdES;
import static eu.europa.esig.dss.x509.SignatureForm.XAdES;

/**
 * TODO
 */
public class SignatureWizardController extends DSSWizardController<SignatureModel> {

	private FileView fileView;
	private SignatureView signatureView;
	private TokenView tokenView;
	private PKCS11View pkcs11View;
	private PKCS12View pkcs12View;
	private SignatureDigestAlgorithmView signatureDigestAlgorithmView;
	private CertificateView certificateView;
	private PersonalDataView personalDataView;
	private SaveView saveView;
	private FinishView signView;

	private boolean successful = false;

	/**
	 * The default constructor for SignatureWizardController.
	 *
	 * @param core
	 * @param model
	 */
	public SignatureWizardController(final DSSAppletCore core, final SignatureModel model) {
		super(core, model);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController#doCancel()
	 */
	@Override
	protected void doCancel() {

		getCore().getController(ActivityController.class).display();
	}

	/**
	 *
	 */
	public void doRefreshPrivateKeys() {

		try {
			final SignatureTokenConnection tokenConnection = getModel().getTokenConnection();
			getModel().setPrivateKeys(tokenConnection.getKeys());
		} catch (final DSSException e) {
			// FIXME
			LOG.error(e.getMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController#doStart()
	 */
	@Override
	protected Class<? extends WizardStep<SignatureModel, ? extends WizardController<SignatureModel>>> doStart() {

		return FileStep.class;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController#registerViews()
	 */
	@Override
	protected void registerViews() {

		fileView = new FileView(getCore(), this, getModel());
		signatureView = new SignatureView(getCore(), this, getModel());
		tokenView = new TokenView(getCore(), this, getModel());
		pkcs11View = new PKCS11View(getCore(), this, getModel());
		pkcs12View = new PKCS12View(getCore(), this, getModel());
		signatureDigestAlgorithmView = new SignatureDigestAlgorithmView(getCore(), this, getModel());
		certificateView = new CertificateView(getCore(), this, getModel());
		personalDataView = new PersonalDataView(getCore(), this, getModel());
		saveView = new SaveView(getCore(), this, getModel());
		signView = new FinishView(getCore(), this, getModel());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController#registerWizardStep()
	 */
	@Override
	protected Map<Class<? extends WizardStep<SignatureModel, ? extends WizardController<SignatureModel>>>, ? extends WizardStep<SignatureModel, ? extends WizardController<SignatureModel>>> registerWizardStep() {

		final SignatureModel model = getModel();

		final Map steps = new HashMap();
		steps.put(FileStep.class, new FileStep(model, fileView, this));
		steps.put(SignatureStep.class, new SignatureStep(model, signatureView, this));
		steps.put(TokenStep.class, new TokenStep(model, tokenView, this));
		steps.put(PKCS11Step.class, new PKCS11Step(model, pkcs11View, this));
		steps.put(PKCS12Step.class, new PKCS12Step(model, pkcs12View, this));
		steps.put(SignatureDigestAlgorithmStep.class, new SignatureDigestAlgorithmStep(model, signatureDigestAlgorithmView, this));
		steps.put(CertificateStep.class, new CertificateStep(model, certificateView, this));
		steps.put(PersonalDataStep.class, new PersonalDataStep(model, personalDataView, this));
		steps.put(SaveStep.class, new SaveStep(model, saveView, this));
		steps.put(FinishStep.class, new FinishStep(model, signView, this));

		return steps;
	}

	/**
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws DSSException
	 */
	public void signDocument() throws IOException, NoSuchAlgorithmException, DSSException {

		final SignatureModel model = getModel();

		final boolean validSigningCertificate = isValidSigningCertificate(model);

		if (!validSigningCertificate) {
			setSuccessful(false);
			//			showDialogBox("Signing certificate validation error!");
			return;
		}

		final File fileToSign = model.getSelectedFile();
		final SignatureTokenConnection tokenConnection = model.getTokenConnection();
		final DSSPrivateKeyEntry privateKey = model.getSelectedPrivateKey();

		AbstractSignatureParameters signatureParameters = null;
		switch (model.getForm()) {
			case XAdES:
				signatureParameters = new XAdESSignatureParameters();
				break;
			case CAdES:
				signatureParameters = new CAdESSignatureParameters();
				break;
			case PAdES:
				final PAdESSignatureParameters pAdESSignatureParameters = new PAdESSignatureParameters();
				pAdESSignatureParameters.setSignatureSize(9472 * 3);
				signatureParameters = pAdESSignatureParameters;
				break;
		}
		signatureParameters.setSigningCertificate(privateKey.getCertificate());
		signatureParameters.setCertificateChain(privateKey.getCertificateChain());
		signatureParameters.setDigestAlgorithm(getParameter().getSignatureHashAlgorithm());

		final SignatureForm form = model.getForm();
		final Level level = model.getLevel();

		SignatureLevel signatureLevel = getSignatureLevel(form, level);
		signatureParameters.setSignatureLevel(signatureLevel);
		signatureParameters.setSignaturePackaging(model.getPackaging());

		final TimestampParameters signatureTimestampParameters = new TimestampParameters();
		signatureTimestampParameters.setDigestAlgorithm(getParameter().getTimestampHashAlgorithm());
		signatureParameters.setSignatureTimestampParameters(signatureTimestampParameters);

		final DSSDocument signedDocument = SigningUtils.signDocument(fileToSign, signatureParameters, privateKey, tokenConnection, model);
		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		try {
			fileOutputStream = new FileOutputStream(model.getTargetFile());
			inputStream = signedDocument.openStream();
			IOUtils.copy(inputStream, fileOutputStream);
			setSuccessful(true);
		} finally {
			IOUtils.closeQuietly(inputStream);
			IOUtils.closeQuietly(fileOutputStream);
		}
	}

	private SignatureLevel getSignatureLevel(SignatureForm form, Level level) {

		if (PAdES == form) {

			if (level == BASELINE_B) {
				return PAdES_BASELINE_B;
			}
			if (level == BASELINE_T) {
				return PAdES_BASELINE_T;
			}
			if (level == BASELINE_LT) {
				return PAdES_BASELINE_LT;
			}
			if (level == BASELINE_LTA) {
				return PAdES_BASELINE_LTA;
			}
		} else if (CAdES == form) {

			if (level == BASELINE_B) {
				return CAdES_BASELINE_B;
			}
			if (level == BASELINE_T) {
				return CAdES_BASELINE_T;
			}
			if (level == BASELINE_LT) {
				return CAdES_BASELINE_LT;
			}
			if (level == BASELINE_LTA) {
				return CAdES_BASELINE_LTA;
			}
		} else if (XAdES == form) {

			if (level == BASELINE_B) {
				return XAdES_BASELINE_B;
			}
			if (level == BASELINE_T) {
				return XAdES_BASELINE_T;
			}
			if (level == BASELINE_LT) {
				return XAdES_BASELINE_LT;
			}
			if (level == BASELINE_LTA) {
				return XAdES_BASELINE_LTA;
			}
		}
		return null;
	}

	//	protected void showDialogBox(final String message) {
	//
	//		JDialog.setDefaultLookAndFeelDecorated(true);
	//		JOptionPane.showMessageDialog(null, message);
	//	}


	private boolean isValidSigningCertificate(final SignatureModel model) {

		CertificateValidationService certificateValidationService = new CertificateValidationService();
		final DSSPrivateKeyEntry selectedPrivateKey = model.getSelectedPrivateKey();
		final boolean valid = certificateValidationService.validate(selectedPrivateKey.getCertificate());
		return valid;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
