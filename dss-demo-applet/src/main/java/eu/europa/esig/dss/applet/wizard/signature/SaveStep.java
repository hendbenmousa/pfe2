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

import org.apache.commons.lang.StringUtils;

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
public class SaveStep extends WizardStep<SignatureModel, SignatureWizardController> {

	/**
	 * The default constructor for SaveStep.
	 *
	 * @param model
	 * @param view
	 * @param controller
	 */
	public SaveStep(final SignatureModel model, final WizardView<SignatureModel, SignatureWizardController> view, final SignatureWizardController controller) {
		super(model, view, controller);
	}

	@Override
	protected void finish() throws ControllerException {
		// TODO Auto-generated method stub

	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getBackStep() {

		final Parameters parameter = getController().getParameter();
		final String claimedRole = parameter.getClaimedRole();
		if (claimedRole != null) {
			return PersonalDataStep.class;
		}

		return CertificateStep.class;
	}

	@Override
	protected Class<? extends WizardStep<SignatureModel, SignatureWizardController>> getNextStep() {
		return FinishStep.class;
	}

	@Override
	protected int getStepProgression() {
		return 6;
	}

	@Override
	protected void init() {

		final SignatureModel model = getModel();

		final File selectedFile = model.getSelectedFile();
		// Initialize the target file based on the current selected file
		final SignaturePackaging signaturePackaging = model.getPackaging();
		final SignatureForm signatureForm = model.getForm();
		final Level signatureLevel = model.getLevel();
		final File targetFile = prepareTargetFileName(selectedFile, signaturePackaging, signatureForm, signatureLevel);

		model.setTargetFile(targetFile);

	}

	@Override
	protected boolean isValid() {
		final File targetFile = getModel().getTargetFile();
		return targetFile != null;
	}

	private File prepareTargetFileName(final File file, final SignaturePackaging signaturePackaging, SignatureForm signatureForm, final Level signatureLevel) {

		final File parentDir = file.getParentFile();
		final String originalName = StringUtils.substringBeforeLast(file.getName(), ".");
		final String originalExtension = "." + StringUtils.substringAfterLast(file.getName(), ".");
		final String form = signatureForm.name();
		final String level = signatureLevel.name();

		if (((SignaturePackaging.ENVELOPING == signaturePackaging) || (SignaturePackaging.DETACHED == signaturePackaging)) && signatureForm == SignatureForm.XAdES) {

			final String packaging = signaturePackaging.name().toLowerCase();
			return new File(parentDir, originalName + "-" + form + "-" + packaging + "-" + level + ".xml");
		}

		if (signatureForm == SignatureForm.CAdES && !originalExtension.toLowerCase().equals(".p7m")) {
			return new File(parentDir, originalName + originalExtension + ".p7m");
		}

		return new File(parentDir, originalName + "-signed" + originalExtension);
	}
}