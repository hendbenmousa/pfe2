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
package eu.europa.esig.dss.applet.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import eu.europa.esig.dss.applet.controller.ActivityController;
import eu.europa.esig.dss.applet.model.ActivityAction;
import eu.europa.esig.dss.applet.model.ActivityModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;

/**
 * TODO
 */
public class ActivityView extends DSSAppletView<ActivityModel, ActivityController> {

	private static final String I18N_SIGN_DOCUMENT = ResourceUtils.getI18n("SIGN_A_DOCUMENT");
	private static final String I18N_VALIDATION = ResourceUtils.getI18n("VALIDATION");
	private final JRadioButton choice1;
	private final JButton nextButton;

	// validation
	private final JRadioButton choice2;
	private final PresentationModel<ActivityModel> presentationModel;
	private final List<JRadioButton> choices = new ArrayList<JRadioButton>();

	/**
	 * The default constructor for ActivityView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public ActivityView(final AppletCore core, final ActivityController controller, final ActivityModel model) {

		super(core, controller, model);

		presentationModel = new PresentationModel<ActivityModel>(getModel());

		final ValueModel activityValue = presentationModel.getModel(ActivityModel.PROPERTY_ACTIVITY);

		choice1 = ComponentFactory.createRadioButton(I18N_SIGN_DOCUMENT, activityValue, ActivityAction.SIGN);
		choice2 = ComponentFactory.createRadioButton(I18N_VALIDATION, activityValue, ActivityAction.VALIDATE);
		nextButton = ComponentFactory.createNextButton(true, new NextActionListener());
		nextButton.setName("next");

		final List<ActivityAction> appletUsage = getController().getParameter().getUsageList();
		if (appletUsage.size() == 1) {

			getModel().setAction(appletUsage.get(0));
			return;
		}
		for (final ActivityAction usage : appletUsage) {

			switch (usage) {
				case SIGN: {
					choices.add(choice1);
					break;
				}
				case VALIDATE: {
					choices.add(choice2);
					break;
				}
			}
		}
	}

	@Override
	protected Container doLayout() {

		final JPanel panel = ComponentFactory.createPanel(new BorderLayout());

		final String[] colSpecs = new String[]{"5dlu", "pref", "5dlu", "pref:grow,5dlu"};
		final String[] rowSpecs = new String[]{"5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu"};

		final PanelBuilder builder = ComponentFactory.createBuilder(colSpecs, rowSpecs);
		final CellConstraints cc = new CellConstraints();

		builder.addSeparator(ResourceUtils.getI18n("CHOOSE_AN_ACTIVITY"), cc.xyw(2, 2, 3));

		int i = 4;
		for (JRadioButton choice : choices) {

			builder.add(choice, cc.xy(2, i));
			i += 2;
		}

		panel.add(ComponentFactory.createPanel(builder), BorderLayout.CENTER);
		panel.add(ComponentFactory.actionPanel(nextButton), BorderLayout.SOUTH);

		return panel;
	}

	@Override
	protected Container layout() {

		final boolean valid = getController().isValid();
		nextButton.setEnabled(valid);

		return doLayout();
	}

	@Override
	public void modelChanged(final PropertyChangeEvent evt) {

		final boolean enabled = getController().isValid();
		nextButton.setEnabled(enabled);
		//wizardModelChange(evt);
	}

	public void click() {

		nextButton.doClick();
	}

	private final class NextActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (getModel().getAction() != null) {
				getController().startAction();
			}
		}
	}
}