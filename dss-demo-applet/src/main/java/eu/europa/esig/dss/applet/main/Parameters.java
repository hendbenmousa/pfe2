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
package eu.europa.esig.dss.applet.main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.applet.model.ActivityAction;
import eu.europa.esig.dss.signature.SignaturePackaging;
import eu.europa.esig.dss.validation.ValidationResourceManager;
import eu.europa.esig.dss.x509.SignatureForm;

import static eu.europa.esig.dss.applet.SignatureTokenType.MSCAPI;
import static eu.europa.esig.dss.applet.SignatureTokenType.PKCS11;
import static eu.europa.esig.dss.applet.SignatureTokenType.PKCS12;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_B;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_LT;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_LTA;
import static eu.europa.esig.dss.applet.main.Level.BASELINE_T;
import static eu.europa.esig.dss.applet.model.ActivityAction.SIGN;
import static eu.europa.esig.dss.applet.model.ActivityAction.VALIDATE;
import static eu.europa.esig.dss.signature.SignaturePackaging.DETACHED;
import static eu.europa.esig.dss.signature.SignaturePackaging.ENVELOPED;
import static eu.europa.esig.dss.signature.SignaturePackaging.ENVELOPING;
import static eu.europa.esig.dss.x509.SignatureForm.CAdES;
import static eu.europa.esig.dss.x509.SignatureForm.PAdES;
import static eu.europa.esig.dss.x509.SignatureForm.XAdES;

/**
 * TODO
 */
public class Parameters {

	private static final Logger LOG = LoggerFactory.getLogger(Parameters.class);
	public static final String SETUP_PROPERTIES = "setup.properties";

	public static final String USAGE = "USAGE";
	public static final String FORM = "FORMAT";
	public static final String LEVEL = "LEVEL";
	public static final String PACKAGING = "PACKAGING";
	public static final String TOKEN = "TOKEN";
	public static final String PKCS11_FILE = "PKCS11_FILE";
	public static final String PKCS12_FILE = "PKCS12_FILE";
	public static final String CLAIMED_ROLE = "CLAIMED_ROLE";

	public static final String VALIDATION_OUT_PATH = "VALIDATION_OUT_PATH";

	public static final String SIGNATURE_HASH = "SIGNATURE_HASH";
	public static final String TIMESTAMP_HASH = "TIMESTAMP_HASH";

	public static final String TIMESTAMP_SERVER_URL = "TIMESTAMP_SERVER_URL";
	public static final String VALIDATION_POLICY = "VALIDATION_POLICY_PATH";

	/**
	 *
	 */
	private List<ActivityAction> usageList = new ArrayList<ActivityAction>() {{
		add(SIGN);
		add(VALIDATE);
	}};
	/**
	 *
	 */
	private List<SignatureForm> formList = new ArrayList<SignatureForm>() {{
		add(XAdES);
		add(CAdES);
		add(PAdES);
	}};
	/**
	 *
	 */
	private List<Level> levelList = new ArrayList<Level>() {{
		add(BASELINE_B);
		add(BASELINE_T);
		add(BASELINE_LT);
		add(BASELINE_LTA);
	}};
	/**
	 *
	 */
	private List<SignaturePackaging> packagingList = new ArrayList<SignaturePackaging>() {{
		add(DETACHED);
		add(ENVELOPED);
		add(ENVELOPING);
	}};
	/**
	 *
	 */
	private List<SignatureTokenType> tokenTypeList = new ArrayList<SignatureTokenType>() {{
		add(PKCS11);
		add(PKCS12);
		add(MSCAPI);
	}};
	/**
	 *
	 */
	private File pkcs11File;
	/**
	 *
	 */
	private File pkcs12File;

	/**
	 *
	 */
	private DigestAlgorithm signatureHashAlgorithm = DigestAlgorithm.SHA256;

	/**
	 *
	 */
	private DigestAlgorithm timestampHashAlgorithm = DigestAlgorithm.SHA1;

	/**
	 *
	 */
	private String signaturePolicyAlgorithm;
	/**
	 *
	 */
	private byte[] signaturePolicyValue;
	/**
	 *
	 */
	private String validationPolicyPath;
	/**
	 *
	 */
	private String timestampServerUrl = "https://ts.certification.tn:4318";
	/**
	 *
	 */
	private String claimedRole;
	/**
	 *
	 */
	private String validationOutPath = System.getProperty("java.io.tmpdir");

	/**
	 * The default constructor for Parameters.
	 *
	 * @param setupPath
	 */
	public Parameters(final String setupPath) {

		try {

			InputStream setupInputStream;
			if (StringUtils.isBlank(setupPath)) {

				LOG.info("Try to use default configuration file '{}'", SETUP_PROPERTIES);
				setupInputStream = Parameters.class.getResourceAsStream("/" + SETUP_PROPERTIES);
				if (setupInputStream == null) {

					LOG.warn("Default applet configuration used.");
					return;
				}
				LOG.info("Default configuration file is used.");
			} else {

				File setupFile = new File(setupPath);
				if (!(setupFile.exists() && setupFile.isFile())) {

					LOG.warn("Applet configuration does not exist: " + setupPath);
					LOG.warn("Default applet configuration used.");
					return;
				} else {

					LOG.warn("Applet configuration used: " + setupFile.getAbsolutePath());
					setupInputStream = DSSUtils.toInputStream(setupFile);
				}
			}
			final Properties properties = new Properties();
			properties.load(setupInputStream);

			initUsage(properties);
			initForm(properties);
			initLevel(properties);
			initPackaging(properties);
			initTokenType(properties);
			initSignatureHashAlgorithm(properties);
			initTimestampHashAlgorithm(properties);
			initPkcs11File(properties);
			initPkcs12File(properties);
			initValidationPolicy(properties);
			initTimestampServerUrl(properties);
			initClaimedRole(properties);
			initValidationOutPath(properties);
		} catch (IOException e) {
			throw new DSSException(e);
		}
	}

	private void initUsage(final Properties properties) {

		final String usage = properties.getProperty(USAGE);
		final String[] splitArray = usage.split("\\|");
		final List appletUsageList = new ArrayList();
		for (final String split : splitArray) {

			if (StringUtils.isNotEmpty(split)) {

				final ActivityAction appletUsage = ActivityAction.valueOf(split);
				appletUsageList.add(appletUsage);
			}
		}
		if (!appletUsageList.isEmpty()) {
			setUsageList(appletUsageList);
		}
	}

	private void initForm(final Properties properties) {

		final String form = properties.getProperty(FORM);
		final String[] splitArray = form.split("\\|");
		final List formList = new ArrayList();
		for (final String split : splitArray) {

			if (StringUtils.isNotEmpty(split)) {

				final SignatureForm signatureForm = SignatureForm.valueOf(split);
				formList.add(signatureForm);
			}
		}
		if (!formList.isEmpty()) {
			setFormList(formList);
		}
	}

	private void initLevel(final Properties properties) {

		final String form = properties.getProperty(LEVEL);
		final String[] splitArray = form.split("\\|");
		final List levelList = new ArrayList();
		for (final String split : splitArray) {

			if (StringUtils.isNotEmpty(split)) {

				final Level level = Level.valueOf(split);
				levelList.add(level);
			}
		}
		if (!levelList.isEmpty()) {
			setLevelList(levelList);
		}
	}

	private void initPackaging(final Properties properties) {

		final String packaging = properties.getProperty(PACKAGING);
		final String[] splitArray = packaging.split("\\|");
		final List packagingList = new ArrayList();
		for (final String split : splitArray) {

			if (StringUtils.isNotEmpty(split)) {

				final SignaturePackaging signaturePackaging = SignaturePackaging.valueOf(split);
				packagingList.add(signaturePackaging);
			}
		}
		if (!packagingList.isEmpty()) {
			setPackagingList(packagingList);
		}
	}

	private void initTokenType(final Properties properties) {

		final String token = properties.getProperty(TOKEN);
		final String[] splitArray = token.split("\\|");
		final List tokenTypeList = new ArrayList();
		for (final String split : splitArray) {

			if (StringUtils.isNotEmpty(split)) {

				final SignatureTokenType signatureTokenType = SignatureTokenType.valueOf(split);
				tokenTypeList.add(signatureTokenType);
			}
		}
		if (!tokenTypeList.isEmpty()) {
			setTokenTypeList(tokenTypeList);
		}
	}

	private void initSignatureHashAlgorithm(final Properties properties) {

		final String token = properties.getProperty(SIGNATURE_HASH);
		setSignatureHashAlgorithm(DigestAlgorithm.forName(token));
	}

	private void initTimestampHashAlgorithm(final Properties properties) {

		final String token = properties.getProperty(TIMESTAMP_HASH);
		setTimestampHashAlgorithm(DigestAlgorithm.forName(token));
	}

	private void initPkcs11File(final Properties properties) {

		final String token = properties.getProperty(PKCS11_FILE);
		if (StringUtils.isBlank(token)) {
			return;
		}
		final File file = new File(token);
		if (file.exists() && file.isFile()) {
			setPkcs11File(file);
		}
	}

	private void initPkcs12File(final Properties properties) {

		final String token = properties.getProperty(PKCS12_FILE);
		if (StringUtils.isBlank(token)) {
			return;
		}
		final File file = new File(token);
		if (file.exists() && file.isFile()) {
			setPkcs12File(file);
		}
	}

	private void initValidationPolicy(final Properties properties) {

		final String token = properties.getProperty(VALIDATION_POLICY);
		if (StringUtils.isBlank(token)) {
			return;
		}
		final File file = new File(token);
		if (file.exists() && file.isFile()) {
			setValidationPolicyPath(file.getAbsolutePath());
		}
	}

	private void initTimestampServerUrl(final Properties properties) {

		final String token = properties.getProperty(TIMESTAMP_SERVER_URL);
		if (StringUtils.isBlank(token)) {
			return;
		}
		setTimestampServerUrl(token.trim());
	}

	private void initClaimedRole(final Properties properties) {

		final String token = properties.getProperty(CLAIMED_ROLE);
		if (token == null) {
			return;
		}
		setClaimedRole(token.trim());
	}

	private void initValidationOutPath(final Properties properties) {

		final String token = properties.getProperty(VALIDATION_OUT_PATH);
		if (token == null) {
			return;
		}
		setValidationOutPath(token.trim());
	}

	public List<ActivityAction> getUsageList() {
		return usageList;
	}

	public void setUsageList(List<ActivityAction> usageList) {
		this.usageList = usageList;
	}

	/**
	 * @return the pkcs11File
	 */
	public File getPkcs11File() {
		return pkcs11File;
	}

	/**
	 * @param pkcs11File the pkcs11File to set
	 */
	public void setPkcs11File(final File pkcs11File) {
		this.pkcs11File = pkcs11File;
	}

	/**
	 * @return the pkcs12File
	 */
	public File getPkcs12File() {
		return pkcs12File;
	}

	/**
	 * @param pkcs12File the pkcs12File to set
	 */
	public void setPkcs12File(final File pkcs12File) {
		this.pkcs12File = pkcs12File;
	}

	public List<SignatureForm> getFormList() {
		return formList;
	}

	public void setFormList(List formList) {
		this.formList = formList;
	}

	public List<Level> getLevelList() {
		return levelList;
	}

	public void setLevelList(List signatureLevelList) {
		this.levelList = signatureLevelList;
	}

	public List<SignaturePackaging> getPackagingList() {
		return packagingList;
	}

	public void setPackagingList(List packagingList) {
		this.packagingList = packagingList;
	}

	public DigestAlgorithm getSignatureHashAlgorithm() {
		return signatureHashAlgorithm;
	}

	public void setSignatureHashAlgorithm(DigestAlgorithm signatureHashAlgorithm) {
		this.signatureHashAlgorithm = signatureHashAlgorithm;
	}

	public DigestAlgorithm getTimestampHashAlgorithm() {
		return timestampHashAlgorithm;
	}

	public void setTimestampHashAlgorithm(DigestAlgorithm timestampHashAlgorithm) {
		this.timestampHashAlgorithm = timestampHashAlgorithm;
	}

	/**
	 * @return the signaturePolicyAlgorithm
	 */
	public String getSignaturePolicyAlgorithm() {
		return signaturePolicyAlgorithm;
	}

	/**
	 * @param signaturePolicyAlgorithm the signaturePolicyAlgorithm to set
	 */
	public void setSignaturePolicyAlgorithm(final String signaturePolicyAlgorithm) {
		this.signaturePolicyAlgorithm = signaturePolicyAlgorithm;
	}

	/**
	 * @return the signaturePolicyValue
	 */
	public byte[] getSignaturePolicyValue() {
		if (signaturePolicyValue == null) {
			signaturePolicyValue = DSSUtils.EMPTY_BYTE_ARRAY;
		}
		return signaturePolicyValue;
	}

	/**
	 * @param signaturePolicyValue the signaturePolicyValue to set
	 */
	public void setSignaturePolicyValue(final byte[] signaturePolicyValue) {
		this.signaturePolicyValue = signaturePolicyValue;
	}

	/**
	 * @return the signatureTokenType
	 */
	public List<SignatureTokenType> getTokenTypeList() {
		return tokenTypeList;
	}

	/**
	 * @param tokenTypeList the tokenTypeList to set
	 */
	public void setTokenTypeList(final List<SignatureTokenType> tokenTypeList) {
		this.tokenTypeList = tokenTypeList;
	}

	/**
	 * @return
	 */
	public boolean hasPkcs11File() {
		final File file = getPkcs11File();
		return (file != null) && file.exists() && file.isFile();
	}

	/**
	 * @return
	 */
	public boolean hasPkcs12File() {
		final File file = getPkcs12File();
		return (file != null) && file.exists() && file.isFile();
	}

	/**
	 * @return
	 */
	public boolean hasSignaturePolicyAlgorithm() {
		return StringUtils.isNotEmpty(signaturePolicyAlgorithm);
	}

	/**
	 * @return
	 */
	public boolean hasSignaturePolicyValue() {
		return getSignaturePolicyValue().length != 0;
	}

	/**
	 * @return the validation policy. Can be null.
	 */
	public String getValidationPolicyPath() {

		if (validationPolicyPath == null) {
			return ValidationResourceManager.defaultPolicyConstraintsLocation;
		}
		return validationPolicyPath;
	}

	/**
	 * Set the default policy file for validation. Can be null.
	 *
	 * @param validationPolicyPath
	 */
	public void setValidationPolicyPath(String validationPolicyPath) {
		this.validationPolicyPath = validationPolicyPath;
	}


	public String getTimestampServerUrl() {
		return timestampServerUrl;
	}

	public void setTimestampServerUrl(String timestampServerUrl) {
		this.timestampServerUrl = timestampServerUrl;
	}

	public String getClaimedRole() {
		return claimedRole;
	}

	public void setClaimedRole(String claimedRole) {
		this.claimedRole = claimedRole;
	}

	public String getValidationOutPath() {
		return validationOutPath;
	}

	public void setValidationOutPath(String validationOutPath) {
		this.validationOutPath = validationOutPath;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
}