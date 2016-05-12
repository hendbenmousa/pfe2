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
package eu.europa.esig.dss.applet.view.validation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.swing.*;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DSSXMLUtils;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.ValidationModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.XsltConverter;
import eu.europa.esig.dss.applet.wizard.validation.ValidationWizardController;
import eu.europa.esig.dss.validation.report.DetailedReport;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.dss.validation.report.SimpleReport;

/**
 * TODO
 */
public class ReportView extends WizardView<ValidationModel, ValidationWizardController> {

	private final ValueHolder simpleReportValueHolder;
	private JTextArea simpleReportText;

	/**
	 * The default constructor for ReportView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public ReportView(final AppletCore core, final ValidationWizardController controller, final ValidationModel model) {

		super(core, controller, model);
		simpleReportValueHolder = new ValueHolder("");

		simpleReportText = ComponentFactory.createTextArea(simpleReportValueHolder);
		simpleReportText.setTabSize(2);
	}

	@Override
	public void doInit() {

		final ValidationModel model = getModel();

		final SimpleReport simpleReport = model.getSimpleReport();
		final java.util.List<String> signatureIdList = simpleReport.getSignatureIdList();
		final String signatureId = signatureIdList.isEmpty() ? "" : signatureIdList.get(0);
		final String indication = simpleReport.getIndication(signatureId);
		final String subIndication = simpleReport.getSubIndication(signatureId);
		final String signatureFormat = simpleReport.getSignatureFormat(signatureId);
		final Date validationTime = simpleReport.getValidationTime();
		final String validationPolicyName = simpleReport.getValidationPolicyName();
		StringBuilder sb = new StringBuilder();
		sb.append("The signatureValidation was executed on " + DSSUtils.formatDate(validationTime)).append('\n');
		sb.append(String.format("The form of the signature is '%s'.", signatureFormat)).append('\n');
		sb.append(String.format("The validation process returned '%s' indication.", indication)).append('\n');
		if (!subIndication.isEmpty()) {
			sb.append(String.format("The additional information on the problem: '%s'", subIndication)).append('\n');
		}
		sb.append('\n').append(String.format("The validation used '%s' validation policy.", validationPolicyName));
		simpleReportValueHolder.setValue(sb.toString());
	}

	@Override
	protected Container doLayout() {

		final JPanel simpleReportText = getSimpleReportText();
		return simpleReportText;
	}

	private JPanel getSimpleReportText() {

		final String[] columnSpecs = new String[]{"5dlu", "fill:default:grow", "5dlu"};
		final String[] rowSpecs = new String[]{"5dlu", "pref", "5dlu", "fill:default:grow", "5dlu", "pref", "5dlu"};
		final PanelBuilder builder = ComponentFactory.createBuilder(columnSpecs, rowSpecs);
		final CellConstraints cc = new CellConstraints();

		builder.addSeparator("Simple Validation Report XML", cc.xyw(2, 2, 1));
		final JScrollPane scrollPane = ComponentFactory.createScrollPane(simpleReportText);
		builder.add(scrollPane, cc.xyw(2, 4, 1));
		builder.add(ComponentFactory.createOpenButton("Open Validation report", true, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {

				try {
					final Parameters parameter = getController().getParameter();
					final String outPath = parameter.getValidationOutPath();
					final String htmlValidationReportPath = outPath + "/ance-rapport.html";
					final FileOutputStream htmlOutputStream = new FileOutputStream(htmlValidationReportPath);

					final SimpleReport simpleReportXmlDom = getModel().getSimpleReport();
					final File simpleReportFile = new File(outPath + "/simple-report.xml");
					final byte[] simpleReportBytes = simpleReportXmlDom.toByteArray();
					DSSUtils.saveToFile(simpleReportBytes, simpleReportFile);

					final DiagnosticData diagnosticDataXmlDom = getModel().getDiagnosticData();
					final File diagnosticDataFile = new File(outPath + "/diagnostic-data.xml");
					final byte[] diagnosticDataBytes = diagnosticDataXmlDom.toByteArray();
					DSSUtils.saveToFile(diagnosticDataBytes, diagnosticDataFile);

					final DetailedReport detailedReportXmlDom = getModel().getDetailedReport();
					final File detailedReportFile = new File(outPath + "/detailed-report.xml");
					final byte[] detailedReportBytes = detailedReportXmlDom.toByteArray();
					DSSUtils.saveToFile(detailedReportBytes, detailedReportFile);

					final String xsltPath = "/simpleReport.xslt";
					final Document document = XsltConverter.renderAsHtml(simpleReportXmlDom, xsltPath, null);
					DSSXMLUtils.transform(document, htmlOutputStream);
					IOUtils.closeQuietly(htmlOutputStream);
					System.out.println("Transformation done: " + htmlValidationReportPath);
					final File htmlFile = new File(htmlValidationReportPath);
					Desktop.getDesktop().browse(htmlFile.toURI());
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}), cc.xyw(2, 6, 1));

		return ComponentFactory.createPanel(builder);
	}
}