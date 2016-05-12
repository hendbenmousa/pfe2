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
package eu.europa.esig.dss.applet.model;

import java.io.File;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.jgoodies.binding.beans.Model;
import eu.europa.esig.dss.validation.report.DetailedReport;
import eu.europa.esig.dss.validation.report.DiagnosticData;
import eu.europa.esig.dss.validation.report.SimpleReport;

/**
 * TODO
 */
@SuppressWarnings("serial")
public class ValidationModel extends Model {

	/**
	 *
	 */
	public static final String CHANGE_PROPERTY_SIGNED_FILE = "signedFile";
	/**
	 *
	 */
	public static final String CHANGE_PROPERTY_ORIGINAL_FILE = "originalFile";
	public static final String CHANGE_PROPERTY_DEFAULT_POLICY = "defaultPolicy";
	public static final String CHANGE_PROPERTY_DIAGNOSTIC_DATA_ = "diagnosticData";
	public static final String CHANGE_PROPERTY_DETAILED_REPORT = "detailedReport";
	public static final String CHANGE_PROPERTY_SIMPLE_REPORT_ = "simpleReport";
	private File signedFile;
	private File originalFile;
	private boolean defaultPolicy = true;
	private DiagnosticData diagnosticData;
	private DetailedReport detailedReport;
	private SimpleReport simpleReport;

	/**
	 * @return the originalFile
	 */
	public File getOriginalFile() {
		return originalFile;
	}

	/**
	 * @param originalFile the originalFile to set
	 */
	public void setOriginalFile(final File originalFile) {
		final File oldValue = this.originalFile;
		final File newValue = originalFile;
		this.originalFile = newValue;
		firePropertyChange(CHANGE_PROPERTY_ORIGINAL_FILE, oldValue, newValue);
	}

	/**
	 * @return the signedFile
	 */
	public File getSignedFile() {
		return signedFile;
	}

	/**
	 * @param signedFile the signedFile to set
	 */
	public void setSignedFile(final File signedFile) {
		final File oldValue = this.signedFile;
		final File newValue = signedFile;
		this.signedFile = newValue;
		firePropertyChange(CHANGE_PROPERTY_SIGNED_FILE, oldValue, newValue);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

	public boolean isDefaultPolicy() {
		return defaultPolicy;
	}

	public void setDefaultPolicy(boolean defaultPolicy) {

		final boolean oldValue = this.defaultPolicy;
		final boolean newValue = defaultPolicy;
		this.defaultPolicy = newValue;
		firePropertyChange(CHANGE_PROPERTY_DEFAULT_POLICY, oldValue, newValue);
	}

	public DetailedReport getDetailedReport() {
		return detailedReport;
	}

	public void setDetailedReport(DetailedReport detailedReport) {
		final DetailedReport oldValue = this.detailedReport;
		final DetailedReport newValue = detailedReport;
		this.detailedReport = detailedReport;
		firePropertyChange(CHANGE_PROPERTY_DETAILED_REPORT, oldValue, newValue);
	}

	public DiagnosticData getDiagnosticData() {
		return diagnosticData;
	}

	public void setDiagnosticData(DiagnosticData diagnosticData) {
		final DiagnosticData oldValue = this.diagnosticData;
		final DiagnosticData newValue = diagnosticData;
		this.diagnosticData = diagnosticData;
		firePropertyChange(CHANGE_PROPERTY_DIAGNOSTIC_DATA_, oldValue, newValue);
	}

	public SimpleReport getSimpleReport() {
		return simpleReport;
	}

	public void setSimpleReport(SimpleReport simpleReport) {
		final SimpleReport oldValue = this.simpleReport;
		final SimpleReport newValue = simpleReport;
		this.simpleReport = simpleReport;
		firePropertyChange(CHANGE_PROPERTY_SIMPLE_REPORT_, oldValue, newValue);
	}
}