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
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import eu.europa.esig.dss.applet.model.ValidationModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;
import eu.europa.esig.dss.applet.wizard.validation.ValidationWizardController;

/**
 * TODO
 */
public class ValidationView extends WizardView<ValidationModel, ValidationWizardController> {

	private static final String I18N_NO_FILE_SELECTED = ResourceUtils.getI18n("NO_FILE_SELECTED");
	private static final String I18N_BROWSE_SIGNED = ResourceUtils.getI18n("BROWSE_SIGNED");
	private static final String I18N_BROWSE_ORIGINAL = ResourceUtils.getI18n("BROWSE_ORIGINAL");
	private static final String I18N_SIGNED_FILE_TO_VALIDATE = ResourceUtils.getI18n("SIGNED_FILE_TO_VALIDATE");
	private static final String I18N_ORIGINAL_FILE = ResourceUtils.getI18n("ORIGINAL_FILE") + " " + ResourceUtils.getI18n("ONLY_IF_DETACHEDJAVA");
	private final JLabel fileB;
	private final JLabel fileA;
	private final JButton selectFileA;
	private final JButton selectFileB;
	private final JButton clear;

	/**
	 * The default constructor for ValidationView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public ValidationView(final AppletCore core, final ValidationWizardController controller, final ValidationModel model) {

		super(core, controller, model);

		selectFileA = ComponentFactory.createFileChooser(I18N_BROWSE_SIGNED, true, new SelectFileAEventListener());
		selectFileA.setName("fileA");
		selectFileB = ComponentFactory.createFileChooser(I18N_BROWSE_ORIGINAL, true, new SelectFileBEventListener());
		selectFileB.setName("fileB");
		clear = ComponentFactory.createClearButton(true, new ClearEventListener());
		clear.setName("clear");
		fileA = ComponentFactory.createLabel(I18N_NO_FILE_SELECTED);
		fileB = ComponentFactory.createLabel(I18N_NO_FILE_SELECTED);
	}

	@Override
	public void doInit() {

		final ValidationModel model = getModel();
		fileA.setText(model.getSignedFile() != null ? model.getSignedFile().getName() : I18N_NO_FILE_SELECTED);
		fileB.setText(model.getOriginalFile() != null ? model.getOriginalFile().getName() : I18N_NO_FILE_SELECTED);
	}

	@Override
	protected Container doLayout() {

		final FormLayout layout = new FormLayout("5dlu, pref, 5dlu, pref, 5dlu, pref:grow, 5dlu",
			  "5dlu, p, 5dlu, pref, 5dlu, p, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu");

		final CellConstraints cc = new CellConstraints();
		final PanelBuilder builder = ComponentFactory.createBuilder(layout);
		int row = 2;
		builder.addSeparator(I18N_SIGNED_FILE_TO_VALIDATE, cc.xyw(2, 2, 6));
		builder.add(selectFileA, cc.xy(2, row = row + 2));
		builder.add(fileA, cc.xyw(4, row, 4));
		builder.addSeparator(I18N_ORIGINAL_FILE, cc.xyw(2, row = row + 2, 6));
		builder.add(selectFileB, cc.xy(2, row = row + 2));
		builder.add(fileB, cc.xyw(4, row, 4));
		builder.add(clear, cc.xy(2, row + 2));

		return ComponentFactory.createPanel(builder);
	}

	@Override
	public void wizardModelChange(final PropertyChangeEvent evt) {

		final ValidationModel model = getModel();
		if (evt.getPropertyName().equals(ValidationModel.CHANGE_PROPERTY_ORIGINAL_FILE)) {

			if (model.getOriginalFile() == null) {
				fileB.setText(I18N_NO_FILE_SELECTED);
			} else {
				fileB.setText(model.getOriginalFile().getName());
			}
			return;
		}

		if (evt.getPropertyName().equals(ValidationModel.CHANGE_PROPERTY_SIGNED_FILE)) {

			if (model.getSignedFile() == null) {
				fileA.setText(I18N_NO_FILE_SELECTED);
			} else {
				fileA.setText(model.getSignedFile().getName());
			}
			return;
		}
	}

	/**
	 * TODO
	 */
	private class ClearEventListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			final ValidationModel model = getModel();
			model.setOriginalFile(null);
			model.setSignedFile(null);
		}
	}

	/**
	 * TODO
	 */
	private class SelectFileAEventListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			final JFileChooser chooser = new JFileChooser(getModel().getSignedFile());
			final int result = chooser.showOpenDialog(getCore());
			if (result == JFileChooser.APPROVE_OPTION) {
				getModel().setSignedFile(chooser.getSelectedFile());
			}
		}
	}

	/**
	 * TODO
	 */
	private class SelectFileBEventListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			final JFileChooser chooser = new JFileChooser();
			final int result = chooser.showOpenDialog(getCore());
			if (result == JFileChooser.APPROVE_OPTION) {
				getModel().setOriginalFile(chooser.getSelectedFile());
			}
		}
	}
}