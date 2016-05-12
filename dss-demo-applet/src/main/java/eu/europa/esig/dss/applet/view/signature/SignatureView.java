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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.*;

import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import eu.europa.esig.dss.applet.component.model.AbstractComboBoxModel;
import eu.europa.esig.dss.applet.main.FileType;
import eu.europa.esig.dss.applet.main.Level;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.x509.SignatureForm;

import static eu.europa.esig.dss.signature.SignaturePackaging.DETACHED;
import static eu.europa.esig.dss.signature.SignaturePackaging.ENVELOPED;
import static eu.europa.esig.dss.signature.SignaturePackaging.ENVELOPING;
import static eu.europa.esig.dss.x509.SignatureForm.CAdES;
import static eu.europa.esig.dss.x509.SignatureForm.PAdES;
import static eu.europa.esig.dss.x509.SignatureForm.XAdES;

/**
 * TODO
 */
public class SignatureView extends WizardView<SignatureModel, SignatureWizardController> {

	private static final String I18N_ENVELOPING = ResourceUtils.getI18n("ENVELOPING");
	private static final String I18N_ENVELOPED = ResourceUtils.getI18n("ENVELOPED");
	private static final String I18N_DETACHED = ResourceUtils.getI18n("DETACHED");
	private final JComboBox levelComboBox;
	private final ValueHolder formatValueHolder;
	private final ValueHolder packagingValueHolder;
	private final ValueModel levelValue;
	private final SignatureWizardController controller;
	//	private final JRadioButton asicsButton;
	//	private final JRadioButton asiceButton;
	private JRadioButton envelopingButton;
	private JRadioButton envelopedButton;
	private JRadioButton detachedButton;
	private JRadioButton cadesButton;
	private JRadioButton xadesButton;
	private JRadioButton padesButton;

	/**
	 * The default constructor for SignatureView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public SignatureView(final AppletCore core, final SignatureWizardController controller, final SignatureModel model) {

		super(core, controller, model);
		this.controller = controller;

		final BeanAdapter<SignatureModel> beanAdapter = new BeanAdapter<SignatureModel>(model);

		formatValueHolder = new ValueHolder(model.getForm());
		formatValueHolder.addPropertyChangeListener(new FormatEventListener());

		final Parameters parameter = controller.getParameter();
		final List<SignatureForm> formList = parameter.getFormList();
		if (formList.contains(XAdES)) {
			xadesButton = ComponentFactory.createRadioButton(XAdES.name(), formatValueHolder, XAdES);
		}
		if (formList.contains(CAdES)) {
			cadesButton = ComponentFactory.createRadioButton(CAdES.name(), formatValueHolder, CAdES);
		}
		if (formList.contains(PAdES)) {
			padesButton = ComponentFactory.createRadioButton(PAdES.name(), formatValueHolder, PAdES);
		}

		final List<SignaturePackaging> packagingList = parameter.getPackagingList();

		packagingValueHolder = new ValueHolder(model.getPackaging());
		packagingValueHolder.addPropertyChangeListener(new PackagingEventListener());

		if (packagingList.contains(ENVELOPING)) {
			envelopingButton = ComponentFactory.createRadioButton(I18N_ENVELOPING, packagingValueHolder, ENVELOPING);
		}
		if (packagingList.contains(ENVELOPED)) {
			envelopedButton = ComponentFactory.createRadioButton(I18N_ENVELOPED, packagingValueHolder, ENVELOPED);
		}
		if (packagingList.contains(DETACHED)) {
			detachedButton = ComponentFactory.createRadioButton(I18N_DETACHED, packagingValueHolder, DETACHED);
		}

		levelValue = beanAdapter.getValueModel(SignatureModel.PROPERTY_LEVEL);
		final SelectionInList<String> levels = new SelectionInList<String>(new LevelComboBoxModel(), levelValue);
		levelComboBox = ComponentFactory.createComboBox(levels);
	}

	private JPanel doFormatLayout() {

		if (cadesButton != null && xadesButton != null && padesButton != null) {
			return ComponentFactory.createPanel(cadesButton, xadesButton, padesButton);//, asicsButton, asiceButton);
		}
		if (cadesButton != null && xadesButton != null) {
			return ComponentFactory.createPanel(cadesButton, xadesButton);//, asicsButton, asiceButton);
		}
		if (xadesButton != null && padesButton != null) {
			return ComponentFactory.createPanel(xadesButton, padesButton);//, asicsButton, asiceButton);
		}
		if (cadesButton != null && padesButton != null) {
			return ComponentFactory.createPanel(cadesButton, padesButton);//, asicsButton, asiceButton);
		}
		if (cadesButton != null) {
			return ComponentFactory.createPanel(cadesButton);//, asicsButton, asiceButton);
		}
		if (xadesButton != null) {
			return ComponentFactory.createPanel(xadesButton);//, asicsButton, asiceButton);
		}
		return ComponentFactory.createPanel(padesButton);//, asicsButton, asiceButton);
	}

	@Override
	public void doInit() {

		final SignatureModel model = getModel();
		final SignaturePackaging packaging = model.getPackaging();
		final SignatureForm format = model.getForm();
		final FileType fileType = model.getFileType();
		if (padesButton != null) {
			padesButton.setEnabled(FileType.PDF == fileType);
		}

		formatValueHolder.setValue(format);
		packagingValueHolder.setValue(packaging);
		levelValue.setValue(model.getLevel());
	}

	@Override
	protected Container doLayout() {

		final JPanel formatPanel = doFormatLayout();
		final JPanel packagingPanel = doPackagingLayout();
		final JPanel levelPanel = doLevelLayout();

		final FormLayout layout = new FormLayout("5dlu,pref:grow ,5dlu", "5dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu,pref, 5dlu,pref, 5dlu, pref, 5dlu");
		final PanelBuilder builder = ComponentFactory.createBuilder(layout);
		final CellConstraints cc = new CellConstraints();
		builder.addSeparator(ResourceUtils.getI18n("SIGNATURE_FORMAT"), cc.xyw(2, 2, 1));
		builder.add(formatPanel, cc.xyw(2, 4, 1));
		builder.addSeparator(ResourceUtils.getI18n("PACKAGING"), cc.xyw(2, 6, 1));
		builder.add(packagingPanel, cc.xyw(2, 8, 1));
		builder.addSeparator(ResourceUtils.getI18n("LEVEL"), cc.xyw(2, 10, 1));
		builder.add(levelPanel, cc.xy(2, 12));
		return ComponentFactory.createPanel(builder);
	}

	private JPanel doLevelLayout() {

		final FormLayout layout = new FormLayout("5dlu, fill:default:grow, 5dlu", "5dlu, pref, 5dlu");
		final PanelBuilder builder = ComponentFactory.createBuilder(layout);
		final CellConstraints cc = new CellConstraints();
		builder.add(levelComboBox, cc.xy(2, 2));
		return ComponentFactory.createPanel(builder);
	}

	private JPanel doPackagingLayout() {

		if (envelopingButton != null && envelopedButton != null && detachedButton != null) {
			return ComponentFactory.createPanel(envelopingButton, envelopedButton, detachedButton);
		}
		if (envelopingButton != null && envelopedButton != null) {
			return ComponentFactory.createPanel(envelopingButton, envelopedButton);
		}
		if (envelopingButton != null && detachedButton != null) {
			return ComponentFactory.createPanel(envelopingButton, detachedButton);
		}
		if (envelopedButton != null && detachedButton != null) {
			return ComponentFactory.createPanel(envelopedButton, detachedButton);
		}
		if (envelopingButton != null) {
			return ComponentFactory.createPanel(envelopingButton);
		}
		if (envelopedButton != null) {
			return ComponentFactory.createPanel(envelopedButton);
		}
		return ComponentFactory.createPanel(detachedButton);
	}

	@Override
	public void wizardModelChange(final PropertyChangeEvent evt) {

		final String propertyName = evt.getPropertyName();
		if (SignatureModel.PROPERTY_FORMAT.equals(propertyName)) {

			final SignatureForm form = getModel().getForm();
			if (CAdES.equals(form)) {

				JRadioButton selectedButton = enableJRadioButton(null, envelopingButton);
				selectedButton = enableJRadioButton(selectedButton, detachedButton);
				if (envelopedButton != null) {
					envelopedButton.setEnabled(false);
				}
				selectedButton.doClick();
			}

			if (PAdES.equals(form)) {

				if (envelopingButton != null) {
					envelopingButton.setEnabled(false);
				}
				if (detachedButton != null) {
					detachedButton.setEnabled(false);
				}
				if (envelopedButton != null) {
					envelopedButton.setEnabled(true);
				}
				envelopedButton.doClick();
			}

			if (XAdES.equals(form)) {

				JRadioButton selectedButton = enableJRadioButton(null, envelopingButton);
				selectedButton = enableJRadioButton(selectedButton, detachedButton);
				if (envelopedButton != null) {
					envelopedButton.setEnabled(FileType.XML == getModel().getFileType());
					if (selectedButton == null) {
						selectedButton = envelopedButton;
					}
				}
				selectedButton.doClick();
			}
			levelComboBox.setSelectedIndex(-1);
			if (levelComboBox.getModel().getSize() > 0) {
				levelComboBox.setSelectedIndex(0);
			}
		}
	}

	private JRadioButton enableJRadioButton(final JRadioButton selectedButton, final JRadioButton jRadioButton) {

		if (jRadioButton != null) {

			jRadioButton.setEnabled(true);
			if (selectedButton == null) {
				return jRadioButton;
			}
		}
		return selectedButton;
	}

	@Override
	protected Container layout() {

		final Container panel = super.layout();
		final Parameters parameter = getController().getParameter();
		if (parameter.getFormList().size() == 1 && parameter.getPackagingList().size() == 1 && parameter.getLevelList().size() == 1) {
			nextButton.doClick();
		}
		return panel;
	}

	/**
	 * TODO
	 */
	private final class FormatEventListener implements PropertyChangeListener {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			getModel().setForm((SignatureForm) evt.getNewValue());
		}
	}

	/**
	 * TODO
	 */
	private final class LevelComboBoxModel extends AbstractComboBoxModel {

		@Override
		protected List<?> getElements() {

			final List<Level> levelList = controller.getParameter().getLevelList();
			return levelList;
		}
	}

	/**
	 * TODO
	 */
	private final class PackagingEventListener implements PropertyChangeListener {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			getModel().setPackaging((SignaturePackaging) evt.getNewValue());
		}
	}
}
