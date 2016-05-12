package ance;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bouncycastle.tsp.TimeStampToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.applet.PinInputDialog;
import eu.europa.esig.dss.applet.SignatureTokenType;
import eu.europa.esig.dss.client.http.DataLoader;
import eu.europa.esig.dss.client.tsp.OnlineTSPSource;
import eu.europa.esig.dss.token.AbstractSignatureTokenConnection;
import eu.europa.esig.dss.token.MSCAPISignatureToken;
import eu.europa.esig.dss.token.Pkcs11SignatureToken;
import eu.europa.esig.dss.token.Pkcs12SignatureToken;

/**
 * TODO
 *
 * @author Robert Bielecki
 */
public class AnceDataLoader implements DataLoader {

	private static final Logger LOG = LoggerFactory.getLogger(AnceDataLoader.class);
	static protected PinInputDialog pinInputDialog;
	public static final String CONTENT_TYPE = "Content-Type";
	protected String contentType;

	private SignatureTokenType tokenType = null;
	private File pkcsFile;
	private String pkcsPassword;

	public static void main(String[] args) {

		final OnlineTSPSource onlineTSPSource = new OnlineTSPSource();
		final AnceDataLoader dataLoader = new AnceDataLoader();
		dataLoader.setMscapi();
		onlineTSPSource.setDataLoader(dataLoader);
		onlineTSPSource.setTspServer("https://ts.certification.tn:4318");

		final byte[] hellos = DSSUtils.digest(DigestAlgorithm.SHA1, "Hello".getBytes());
		final TimeStampToken timeStampResponse = onlineTSPSource.getTimeStampResponse(DigestAlgorithm.SHA1, hellos);
		System.out.println(timeStampResponse.toString());
	}

	public void setPinInputDialog(final PinInputDialog pinInputDialog) {
		this.pinInputDialog = pinInputDialog;
	}

	@Override
	public byte[] get(String url) {
		return new byte[0];
	}

	@Override
	public DataAndUrl get(List<String> urlStrings) {
		return null;
	}

	@Override
	public byte[] get(String url, boolean refresh) {
		return new byte[0];
	}

	@Override
	public byte[] post(final String url, final byte[] content) {

		try {

			AbstractSignatureTokenConnection token = null;
			switch (tokenType) {
				case MSCAPI:
					token = new MSCAPISignatureToken(null);
					break;
				case PKCS11:
					token = new Pkcs11SignatureToken(pkcsFile.getAbsolutePath(), pinInputDialog);
					break;
				case PKCS12:
					token = new Pkcs12SignatureToken(pkcsPassword, pkcsFile);
					break;
			}
			//			Pkcs11SignatureToken pkcs11 = new Pkcs11SignatureToken("C:\\Temp\\gem\\BIN\\gclib.dll", "435707".toCharArray());
			final KeyStore clientKeyStore = token.getKeyStore();
			//			final KeyStore clientKeyStore = pkcs11.getKeyStore();

			final KeyStore trustKeyStore = KeyStore.getInstance("Windows-ROOT");
			trustKeyStore.load(null, null);
			//			final Enumeration<String> aliases = trustKeyStore.aliases();
			//			while (aliases.hasMoreElements()) {
			//				String s = aliases.nextElement();
			//				System.out.println(s);
			//			}

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(clientKeyStore, new char[0]);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(trustKeyStore);


			SSLContext sslContext = SSLContext.getInstance("TLS");
			//sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			sslContext.init(kmf.getKeyManagers(), null, null);
			HttpClientBuilder httpClientBuilder = HttpClientBuilder.create().setSslcontext(sslContext);

			CloseableHttpClient httpClient = httpClientBuilder.build();
			HttpPost httpRequest = new HttpPost(url);

			final ByteArrayInputStream bis = new ByteArrayInputStream(content);

			final HttpEntity httpEntity = new InputStreamEntity(bis, content.length);
			final HttpEntity requestEntity = new BufferedHttpEntity(httpEntity);
			httpRequest.setEntity(requestEntity);
			if (contentType != null) {
				httpRequest.setHeader(CONTENT_TYPE, contentType);
			}

			CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
			System.out.println("==============================================");
			System.out.println(httpResponse.toString());
			System.out.println("==============================================");
			return readHttpResponse(url, httpResponse);
		} catch (Exception e) {
			throw new DSSException(e);
		}
	}

	protected byte[] readHttpResponse(final String url, final HttpResponse httpResponse) throws DSSException {

		final int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (LOG.isDebugEnabled()) {
			LOG.debug("status code is " + statusCode + " - " + (statusCode == HttpStatus.SC_OK ? "OK" : "NOK"));
		}

		if (statusCode != HttpStatus.SC_OK) {
			LOG.warn("No content available via url: " + url + " - will use nothing: " + url);
			return null;
		}

		final HttpEntity responseEntity = httpResponse.getEntity();
		if (responseEntity == null) {
			LOG.warn("No message entity for this response - will use nothing: " + url);
			return null;
		}

		final byte[] content = getContent(responseEntity);
		return content;
	}

	protected byte[] getContent(final HttpEntity responseEntity) throws DSSException {
		InputStream content = null;
		try {
			content = responseEntity.getContent();
			final byte[] bytes = DSSUtils.toByteArray(content);
			return bytes;
		} catch (IOException e) {
			throw new DSSException(e);
		} finally {
			IOUtils.closeQuietly(content);
		}
	}

	@Override
	public void setContentType(String contentType) {

		this.contentType = contentType;
	}

	public void setMscapi() {

		tokenType = SignatureTokenType.MSCAPI;
	}

	public void setPkcs11(final File pkcs11File, final String pkcs11Password) {

		tokenType = SignatureTokenType.PKCS11;
		pkcsFile = pkcs11File;
		pkcsPassword = pkcs11Password;
	}

	public void setPkcs12(final File pkcs12File, final String pkcs12Password) {

		tokenType = SignatureTokenType.PKCS12;
		pkcsFile = pkcs12File;
		pkcsPassword = pkcs12Password;
	}
}