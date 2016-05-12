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

import javax.swing.*;

import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;

/**
 * TODO
 */
public class FinishView extends WizardView<SignatureModel, SignatureWizardController> {

	private JLabel message;

	/**
	 * The default constructor for SignView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public FinishView(final AppletCore core, final SignatureWizardController controller, final SignatureModel model) {

		super(core, controller, model);
	}

	@Override
	public void doInit() {

	}

	@Override
	protected Container doLayout() {

		final JPanel panel = ComponentFactory.createPanel();
		if (getController().isSuccessful()) {
			message = ComponentFactory.createLabel(ResourceUtils.getI18n("SIGNED_FILE_SAVED"), ComponentFactory.iconSuccess());
		} else {
			message = ComponentFactory.createLabel(ResourceUtils.getI18n("SIGNATURE_CREATION_ERROR"), ComponentFactory.iconError());
		}
		panel.add(message);
		return panel;
	}
}