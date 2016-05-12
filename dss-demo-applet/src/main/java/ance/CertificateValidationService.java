/*******************************************************************************
 * Digital Signature Applet
 * <p/>
 * Copyright (C) 2014 European Commission, Directorate-General for Justice (DG  JUSTICE), BASELINE_B-1049 Bruxelles/Brussel
 * <p/>
 * Developed by: ARHS Developments S.A. (rue Nicolas Bové 2B, L-1253 Luxembourg)
 * <p/>
 * http://www.arhs-developments.com
 * <p/>
 * This file is part of the "Digital Signature Applet" project.
 * <p/>
 * Licensed under the EUPL, version 1.1 or – as soon they are approved by the European  Commission - subsequent versions of the EUPL (the "Licence").
 * You may not use this  work except in compliance with the Licence. You may obtain a copy of the Licence at:
 * <p/>
 * http://ec.europa.eu/idabc/eupl.html
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under   the Licence is distributed on
 * an "AS IS" basis, WITHOUT WARRANTIES OR   CONDITIONS OF ANY KIND, either  express or implied.
 * <p/>
 * See the Licence for the  specific language governing permissions and limitations under the Licence.
 ******************************************************************************/

package ance;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;

import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.client.crl.OnlineCRLSource;
import eu.europa.esig.dss.client.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.SignatureValidationContext;
import eu.europa.esig.dss.validation.ValidationContext;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.x509.RevocationToken;


/**
 * This class allows to validate a {@code List} of {@code X509Certificate}s.
 * The followings criteria are taken into account:
 * - cryptographic validity;
 * - expiration date;
 * - beginning of the validity date;
 * - availability of the revocation data;
 * - status of the certificate;
 * - qualification of the certificate (including TSL);
 * - connectivity to the trust anchor;
 *
 * @author Robert Bielecki
 */
public class CertificateValidationService {

	public static CertificateToken rootCertificateToken;
	public static CertificateToken rootCertificateToken2;

	public final static CommonTrustedCertificateSource trustedCertificateSource = getCommonTrustedCertificateSource();

	private final CertificateVerifier certificateVerifier;

	private Date currentValidationTime;

	public CertificateValidationService() {

		certificateVerifier = new CommonCertificateVerifier();
		certificateVerifier.setCrlSource(new OnlineCRLSource());
		certificateVerifier.setOcspSource(new OnlineOCSPSource());
	}

	//	public static void main(String[] args) {
	//
	//		final File certificateFile = new File("c:/tsa.cer");
	//		final CertificateToken certificateToken = DSSUtils.loadCertificate(certificateFile);
	//
	//		final File rootCertificateFile = new File("C:\\git\\ance-4.5.0\\dss-demo-applet\\src\\main\\resources\\tun-root.pem");
	//		rootCertificateToken = DSSUtils.loadCertificate(rootCertificateFile);
	//
	//		final File rootCertificateFile2 = new File("C:\\git\\ecna-4.5.0\\dss-demo-applet\\src\\main\\resources\\TunServerCA2.crt");
	//		rootCertificateToken2 = DSSUtils.loadCertificate(rootCertificateFile2);
	//
	//		final File expCertificateFile = new File("C:\\git\\ance-4.5.0\\dss-demo-applet\\src\\main\\resources\\user1.crt");
	//		final CertificateToken expCertificateToken = DSSUtils.loadCertificate(expCertificateFile);
	//
	//		final File revokedCertificateFile = new File("C:\\git\\ance-4.5.0\\dss-demo-applet\\src\\main\\resources\\user2.crt");
	//		final CertificateToken revokedCertificateToken = DSSUtils.loadCertificate(revokedCertificateFile);
	//
	//		final File revoked2CertificateFile = new File("C:\\git\\ance-4.5.0\\dss-demo-applet\\src\\main\\resources\\Abdelkader_Mustapha_SFAXI.cer");
	//		final CertificateToken revoked2CertificateToken = DSSUtils.loadCertificate(revoked2CertificateFile);
	//
	//
	//		final CertificateValidationService certificateValidationService = new CertificateValidationService();
	//		certificateValidationService.validate(certificateToken);
	//	}

	private static CommonTrustedCertificateSource getCommonTrustedCertificateSource() {

		InputStream rootCertificateStream = null;
		InputStream rootCertificate2Stream = null;
		try {

			rootCertificateStream = CertificateValidationService.class.getResourceAsStream("/tun-root.pem");
			rootCertificateToken = DSSUtils.loadCertificate(rootCertificateStream);

			rootCertificate2Stream = CertificateValidationService.class.getResourceAsStream("/TunServerCA2.crt");
			rootCertificateToken2 = DSSUtils.loadCertificate(rootCertificate2Stream);
		} finally {
			IOUtils.closeQuietly(rootCertificateStream);
			IOUtils.closeQuietly(rootCertificate2Stream);
		}

		final CommonTrustedCertificateSource trustedCertificateSource = new CommonTrustedCertificateSource();

		//		TrustedListsCertificateSource tslCertificateSource = new TrustedListsCertificateSource();
		//		tslCertificateSource.setDataLoader(new FileCacheDataLoader());
		//		tslCertificateSource.setCheckSignature(true);
		//		tslCertificateSource.setLotlCertificate("");
		//		tslCertificateSource.setLotlUrl("URL");
		//		tslCertificateSource.init();

		trustedCertificateSource.addCertificate(rootCertificateToken);
		trustedCertificateSource.addCertificate(rootCertificateToken2);
		return trustedCertificateSource;
		//		return tslCertificateSource;
	}

	/**
	 * This method sorts the list of provided certificates {@code CertificateToken}, taking into account the relevance of the certificate for signing.
	 *
	 * @param certificateToken {@code List} of {@code X509Certificate} to be ordered
	 * @return the {@code List} of {@code X509CheckResult} taking into account the relevance of the certificate for signing
	 */
	public boolean validate(final CertificateToken certificateToken) {

		certificateVerifier.setTrustedCertSource(trustedCertificateSource);

		final ValidationContext validationContext = new SignatureValidationContext();
		validationContext.initialize(certificateVerifier);
		//		final Date date = DSSUtils.getDate(new Date(), -700);
		//		validationContext.setCurrentTime(date);

		validationContext.addCertificateTokenForVerification(certificateToken);
		validationContext.validate();
		currentValidationTime = validationContext.getCurrentTime();
		System.out.println(certificateToken.toString());

		boolean keyUsage = false;
		boolean trusted = false;
		boolean valid;
		CertificateToken toCheckToken = certificateToken;
		do {

			final boolean validOn = toCheckToken.isValidOn(currentValidationTime);
			final boolean signatureValid = toCheckToken.isSignatureValid();
			final boolean selfSigned = toCheckToken.isSelfSigned();
			Boolean revoked;
			if (selfSigned) {
				revoked = false;
			} else {
				revoked = toCheckToken.isRevoked();
			}
			trusted = trusted | toCheckToken.isTrusted();

			if (toCheckToken.equals(certificateToken)) {

				final List<String> keyUsageBits = toCheckToken.getKeyUsageBits();
				final boolean digitalSignature = keyUsageBits.contains(CertificateToken.DIGITAL_SIGNATURE);
				final boolean nonRepudiation = keyUsageBits.contains(CertificateToken.NON_REPUDIATION);
				keyUsage = digitalSignature || nonRepudiation;
			}
			valid = validOn && signatureValid && revoked != null && !revoked;
			if (!valid) {
				break;
			}
			toCheckToken = toCheckToken.getIssuerToken();
		} while (toCheckToken != null);

		System.out.println("Global validation: " + valid);
		System.out.println("Signing certificate key-usage: " + keyUsage);
		System.out.println("Trust status: " + trusted);
		return valid && keyUsage && trusted;
		//		toCheckToken = certificateToken;
		//		do {
		//
		//			if (toCheckToken.isSelfSigned()) {
		//				final boolean revocationTrust = checkRevocationTrust(toCheckToken.getRevocationToken());
		//				if (!revocationTrust) {
		//
		//					System.out.println("Revocation validation: " + revocationTrust);
		//				}
		//			}
		//			toCheckToken = toCheckToken.getIssuerToken();
		//		} while (toCheckToken != null);
		//		System.out.println("Revocation validation: true");
	}

	/**
	 * @param revocationToken
	 * @return
	 */
	protected boolean checkRevocationTrust(final RevocationToken revocationToken) {

		if (revocationToken == null) {
			return false;
		}
		CertificateToken toCheckToken = revocationToken.getIssuerToken();
		if (toCheckToken == null) {
			return false;
		}
		boolean trusted = false;
		boolean valid;
		do {

			final boolean validOn = toCheckToken.isValidOn(currentValidationTime);
			final boolean signatureValid = toCheckToken.isSignatureValid();
			final boolean selfSigned = toCheckToken.isSelfSigned();
			Boolean revoked;
			if (selfSigned) {
				revoked = false;
			} else {
				revoked = toCheckToken.isRevoked();
			}
			trusted = trusted | toCheckToken.isTrusted();
			valid = validOn && signatureValid && !revoked;
			if (!valid) {
				break;
			}
			toCheckToken = toCheckToken.getIssuerToken();
		} while (toCheckToken != null);
		return valid && trusted;
	}
}
