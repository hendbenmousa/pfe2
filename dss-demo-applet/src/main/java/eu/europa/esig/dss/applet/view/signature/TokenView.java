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

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.main.Parameters;
import eu.europa.esig.dss.applet.model.SignatureModel;
import eu.europa.esig.dss.applet.swing.mvc.AppletCore;
import eu.europa.esig.dss.applet.swing.mvc.wizard.WizardView;
import eu.europa.esig.dss.applet.util.ComponentFactory;
import eu.europa.esig.dss.applet.util.ResourceUtils;
import eu.europa.esig.dss.applet.wizard.signature.SignatureWizardController;

import static eu.europa.esig.dss.applet.SignatureTokenType.MSCAPI;
import static eu.europa.esig.dss.applet.SignatureTokenType.PKCS11;
import static eu.europa.esig.dss.applet.SignatureTokenType.PKCS12;

/**
 * TODO
 */
public class TokenView extends WizardView<SignatureModel, SignatureWizardController> {

	private static final String I18N_TOKEN_PKCS11 = ResourceUtils.getI18n("PKCS11");
	private static final String I18N_TOKEN_PKCS12 = ResourceUtils.getI18n("PKCS12");
	private static final String I18N_TOKEN_MSCAPI = ResourceUtils.getI18n("MSCAPI");

	private final JRadioButton pkcs11Button;
	private final JRadioButton pkcs12Button;
	private final JRadioButton msCapiButton;

	private final PresentationModel<SignatureModel> presentationModel;

	/**
	 * The default constructor for TokenView.
	 *
	 * @param core
	 * @param controller
	 * @param model
	 */
	public TokenView(final AppletCore core, final SignatureWizardController controller, final SignatureModel model) {

		super(core, controller, model);

		presentationModel = new PresentationModel<SignatureModel>(getModel());

		final ValueModel tokenValue = presentationModel.getModel(SignatureModel.PROPERTY_TOKEN_TYPE);

		pkcs11Button = ComponentFactory.createRadioButton(I18N_TOKEN_PKCS11, tokenValue, PKCS11);
		pkcs12Button = ComponentFactory.createRadioButton(I18N_TOKEN_PKCS12, tokenValue, PKCS12);
		msCapiButton = ComponentFactory.createRadioButton(I18N_TOKEN_MSCAPI, tokenValue, MSCAPI);
	}

	@Override
	protected Container doLayout() {

		final String[] colSpecs = new String[]{"5dlu", "pref", "50dlu", "pref:grow", "5dlu"};
		final String[] rowSpecs = new String[]{"5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu", "pref", "5dlu"};

		final PanelBuilder builder = ComponentFactory.createBuilder(colSpecs, rowSpecs);
		final CellConstraints cc = new CellConstraints();

		builder.addSeparator(ResourceUtils.getI18n("TOKEN_API"), cc.xyw(2, 2, 3));
		final Parameters parameter = getController().getParameter();
		final java.util.List<SignatureTokenType> tokenTypeList = parameter.getTokenTypeList();
		if (tokenTypeList.contains(PKCS11)) {
			builder.add(pkcs11Button, cc.xy(2, 4));
		}
		if (tokenTypeList.contains(PKCS12)) {
			builder.add(pkcs12Button, cc.xy(2, 6));
		}
		if (tokenTypeList.contains(MSCAPI)) {
			builder.add(msCapiButton, cc.xy(2, 8));
		}
		//        if (new MOCCAAdapter().isMOCCAAvailable()) {
		//            builder.add(moccaButton, cc.xy(2, 10));
		//        }

		return ComponentFactory.createPanel(builder);
	}
}
