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
package eu.europa.esig.dss.applet.view.signature;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.swing.*;

import org.apache.commons.io.IOUtils;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DSSXMLUtils;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.x509.SignatureForm;

/**
 * TODO
 */
public class FileView extends WizardView<SignatureModel, SignatureWizardController> {

	private static final String I18N_NO_FILE_SELECTED = ResourceUtils.getI18n("NO_FILE_SELECTED");
	private static final String I18N_BROWSE_SIGNED = ResourceUtils.getI18n("BROWSE_SIGNED");
	private static final String I18N_FILE_TO_SIGN = ResourceUtils.getI18n("FILE_TO_SIGN");
	private final JLabel fileSourceLabel;
	private final JButton selectFileSource;

	private final JTextArea textDocument;

	/**
	 * The default constructor for FileView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public FileView(final AppletCore core, final SignatureWizardController controller, final SignatureModel model) {

		super(core, controller, model);
		fileSourceLabel = ComponentFactory.createLabel(I18N_NO_FILE_SELECTED);
		selectFileSource = ComponentFactory.createFileChooser(I18N_BROWSE_SIGNED, true, new SelectFileAEventListener());
		textDocument = new JTextArea(5, 200);
		textDocument.setEditable(false);
	}

	@Override
	public void doInit() {
		final File selectedFile = getModel().getSelectedFile();
		fileSourceLabel.setText(selectedFile != null ? selectedFile.getName() : I18N_NO_FILE_SELECTED);
	}

	@Override
	protected Container doLayout() {

		final FormLayout layout = new FormLayout("5dlu, fill:pref, 5dlu, pref, 5dlu, pref:grow, 5dlu", "5dlu, pref, 5dlu, pref, 5dlu, fill:pref:grow, 5dlu");
		final PanelBuilder builder = ComponentFactory.createBuilder(layout);
		final CellConstraints cc = new CellConstraints();
		builder.addSeparator(I18N_FILE_TO_SIGN, cc.xyw(2, 2, 5));
		builder.add(selectFileSource, cc.xy(2, 4));
		builder.add(fileSourceLabel, cc.xyw(4, 4, 3));
		builder.add(new JScrollPane(textDocument), cc.xyw(2, 6, 5));
		return ComponentFactory.createPanel(builder);
	}

	@Override
	public void wizardModelChange(final PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals(SignatureModel.PROPERTY_SELECTED_FILE)) {

			final File selectedFile = getModel().getSelectedFile();
			final String text = selectedFile == null ? I18N_NO_FILE_SELECTED : selectedFile.getName();
			fileSourceLabel.setText(text);
		}
	}

	private void setFileContent(final File toSignFile) {

		InputStream inputStream = null;
		try {

			inputStream = new FileInputStream(toSignFile);
			final String lowerCase = toSignFile.getAbsolutePath().toLowerCase();
			if (lowerCase.endsWith(".xml")) {

				DSSXMLUtils.buildDOM(inputStream);
			} else if (lowerCase.endsWith(".pdf")) {

				textDocument.setText("File opened with an external reader.");
				Desktop.getDesktop().open(toSignFile);
				return;
			}
			inputStream = new FileInputStream(toSignFile);
			final byte[] bytes = DSSUtils.toByteArray(inputStream);
			textDocument.setText(DSSUtils.toString(bytes));
		} catch (DSSException dssEx) {
			throw dssEx;
		} catch (Exception e1) {
			throw new DSSException(e1);
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
	}

	/**
	 * TODO
	 */
	private class SelectFileAEventListener implements ActionListener {
		/*
		 * (non-Javadoc)
		 *
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		@Override
		public void actionPerformed(final ActionEvent e) {

			final JFileChooser chooser = new JFileChooser();
			final int result = chooser.showOpenDialog(getCore());

			if (result == JFileChooser.APPROVE_OPTION) {

				final File selectedFile = chooser.getSelectedFile();
				final File toSignFile = selectedFile.getAbsoluteFile();
				if (!toSignFile.exists()) {

					throw new DSSException(String.format("File '%s' does not exist!", toSignFile.getAbsolutePath()));
				}
				setFileContent(toSignFile);
				getModel().setSelectedFile(selectedFile);
				final Parameters parameter = getController().getParameter();
				final List<SignatureForm> formList = parameter.getFormList();
				if (formList.size() == 1) {
					getModel().setForm(formList.get(0));
				} else {
					getModel().setForm(null);
				}
				final List<SignaturePackaging> packagingList = parameter.getPackagingList();
				if (packagingList.size() == 1) {
					getModel().setPackaging(packagingList.get(0));
				} else {
					getModel().setPackaging(null);
				}
			}
		}
	}

}
