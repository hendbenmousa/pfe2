/*
 * DSS - Digital Signature Services
 *
 * Copyright (C) 2013 European Commission, Directorate-General Internal Market and Services (DG MARKT), B-1049 Bruxelles/Brussel
 *
 * Developed by: 2013 ARHS Developments S.A. (rue Nicolas Bové 2B, L-1253 Luxembourg) http://www.arhs-developments.com
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * "DSS - Digital Signature Services" is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * DSS is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * "DSS - Digital Signature Services".  If not, see <http://www.gnu.org/licenses/>.
 */

package eu.europa.esig.dss.applet.util;

import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import eu.europa.esig.dss.XmlDom;

/**
 *
 */
public class XsltConverter {

	/**
	 * @param xmlDom       the xmlDom representing the report
	 * @param xsltPath     @return a DOM XHTML standalone document.
	 * @param rootFileName
	 */
	public static Document renderAsHtml(final XmlDom xmlDom, String xsltPath, final String rootFileName) {

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		try {

			final InputStream xslStream = XsltConverter.class.getResourceAsStream(xsltPath);
			final Transformer transformer = transformerFactory.newTransformer(new StreamSource(xslStream));
			transformer.setParameter("output_template_name", "");

			final DOMResult domResult = new DOMResult();
			final DOMSource xmlSource = new DOMSource(xmlDom.getRootElement().getOwnerDocument());
			transformer.transform(xmlSource, domResult);

			return (Document) domResult.getNode();
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}
}
