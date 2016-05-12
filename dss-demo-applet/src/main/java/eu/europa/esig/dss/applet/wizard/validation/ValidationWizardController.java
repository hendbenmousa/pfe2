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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ance.CertificateValidationService;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.FileDocument;
import eu.europa.esig.dss.applet.controller.ActivityController;
import eu.europa.esig.dss.applet.controller.DSSWizardController;
import eu.europa.esig.dss.applet.main.DSSAppletCore;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.ValidationModel;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardController;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardStep;
import eu.europa.esig.dss.applet.view.validation.ReportView;
import eu.europa.esig.dss.applet.view.validation.ValidationView;
import eu.europa.esig.dss.client.crl.OnlineCRLSource;
import eu.europa.esig.dss.client.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.client.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.report.DetailedReport;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.dss.validation.report.Reports;
import eu.europa.esig.dss.validation.report.SimpleReport;

/**
 * TODO
 */
public class ValidationWizardController extends DSSWizardController<ValidationModel> {

	private ReportView reportView;

	private ValidationView formView;

	/**
	 * The default constructor for ValidationWizardController.
	 *
	 * @param core
	 * @param model
	 */
	public ValidationWizardController(final DSSAppletCore core, final ValidationModel model) {

		super(core, model);
		final Parameters parameters = core.getParameters();
	}

	/**
	 *
	 */
	public void displayFormView() {

		formView.show();
	}

	@Override
	protected void doCancel() {

		getCore().getController(ActivityController.class).display();
	}

	@Override
	protected Class<? extends WizardStep<ValidationModel, ? extends WizardController<ValidationModel>>> doStart() {
		return FormStep.class;
	}

	@Override
	protected void registerViews() {

		formView = new ValidationView(getCore(), this, getModel());
		reportView = new ReportView(getCore(), this, getModel());
	}

	@Override
	protected Map<Class<? extends WizardStep<ValidationModel, ? extends WizardController<ValidationModel>>>, ? extends WizardStep<ValidationModel, ? extends WizardController<ValidationModel>>> registerWizardStep() {

		final Map steps = new HashMap();
		steps.put(FormStep.class, new FormStep(getModel(), formView, this));
		steps.put(ReportStep.class, new ReportStep(getModel(), reportView, this));
		return steps;
	}

	/**
	 * Validate the document with the 102853 validation policy
	 *
	 * @throws IOException
	 */
	public void validateDocument() throws DSSException {

		final ValidationModel model = getModel();
		System.out.println("VALIDATION.....");

		final File signedFile = model.getSignedFile();
		final DSSDocument signedDocument = new FileDocument(signedFile);

		final File detachedFile = model.getOriginalFile();
		final DSSDocument detachedDocument = detachedFile != null ? new FileDocument(detachedFile) : null;

		final SignedDocumentValidator validator = SignedDocumentValidator.fromDocument(signedDocument);
		final CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setCrlSource(new OnlineCRLSource());
		certificateVerifier.setOcspSource(new OnlineOCSPSource());

		certificateVerifier.setDataLoader(new FileCacheDataLoader());

		certificateVerifier.setTrustedCertSource(CertificateValidationService.trustedCertificateSource);

		validator.setCertificateVerifier(certificateVerifier);

		if (detachedDocument != null) {

			final List<DSSDocument> detachedContents = new ArrayList<DSSDocument>();
			detachedContents.add(detachedDocument);
			validator.setDetachedContents(detachedContents);
		}

		final String validationPolicyPath = getParameter().getValidationPolicyPath();
		final File validationPolicy = (StringUtils.isNotBlank(validationPolicyPath)) ? new File(validationPolicyPath) : null;
		final Reports reports = validator.validateDocument(validationPolicy);

		final SimpleReport simpleReport = reports.getSimpleReport();
		model.setSimpleReport(simpleReport);

		DiagnosticData diagnosticData = reports.getDiagnosticData();
		model.setDiagnosticData(diagnosticData);

		final DetailedReport detailedReport = reports.getDetailedReport();
		model.setDetailedReport(detailedReport);
	}
}