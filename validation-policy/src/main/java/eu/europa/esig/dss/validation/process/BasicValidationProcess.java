package eu.europa.esig.dss.validation.process;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.ProcessParameters;
import eu.europa.esig.dss.validation.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.policy.rules.AttributeName;
import eu.europa.esig.dss.validation.policy.rules.AttributeValue;
import eu.europa.esig.dss.x509.TimestampType;

import static eu.europa.esig.dss.validation.policy.rules.ExceptionMessage.EXCEPTION_TCOPPNTBI;

/**
 * TODO
 *
 * @author Robert Bielecki
 */
public abstract class BasicValidationProcess implements AttributeName, AttributeValue, ValidationXPathQueryHolder {


	protected static void assertDiagnosticData(final XmlDom diagnosticData, final Class<? extends BasicValidationProcess> aClass) {

		if (diagnosticData == null) {
			throw new DSSException(String.format(EXCEPTION_TCOPPNTBI, aClass.getSimpleName(), "diagnosticData"));
		}
	}

	protected static void assertValidationPolicy(final ValidationPolicy validationPolicy, final Class<? extends BasicValidationProcess> aClass) {

		if (validationPolicy == null) {
			throw new DSSException(String.format(EXCEPTION_TCOPPNTBI, aClass.getSimpleName(), "validationPolicy"));
		}
	}

	protected static void assertCurrentTime(final Date currentTime, final Class<? extends BasicValidationProcess> aClass) {

		if (currentTime == null) {
			throw new DSSException(String.format(EXCEPTION_TCOPPNTBI, aClass.getSimpleName(), "currentTime"));
		}
	}

	protected static void assertContextElement(final XmlDom contextElement, final Class<? extends BasicValidationProcess> aClass) {

		if (contextElement == null) {
			throw new DSSException(String.format(EXCEPTION_TCOPPNTBI, aClass.getSimpleName(), "contextElement"));
		}
	}

	protected void setSuitableValidationPolicy(final ProcessParameters params, final String signatureType) {

		final boolean countersignature = COUNTERSIGNATURE.equals(signatureType);
		params.setCurrentValidationPolicy(countersignature ? params.getCountersignatureValidationPolicy() : params.getValidationPolicy());
	}

	protected List<String> getContentTimestampIdList(final List<String> contentTimestampTypeList, final XmlDom signatureContext) {

		if (contentTimestampTypeList.isEmpty()) {

			contentTimestampTypeList.add(TimestampType.CONTENT_TIMESTAMP.name());
			contentTimestampTypeList.add(TimestampType.ALL_DATA_OBJECTS_TIMESTAMP.name());
			contentTimestampTypeList.add(TimestampType.INDIVIDUAL_DATA_OBJECTS_TIMESTAMP.name());
		}
		final List<String> contentTimestampIdList = new ArrayList<String>();
		for (final String type : contentTimestampTypeList) {

			final List<XmlDom> contentTimestampIdXmlDomList = signatureContext.getElements("./Timestamps/Timestamp[@Type='%s']", type);
			for (final XmlDom contentTimestampIdXmlDom : contentTimestampIdXmlDomList) {

				final String timestampId = contentTimestampIdXmlDom.getAttribute(ID);
				contentTimestampIdList.add(timestampId);
			}
		}
		return contentTimestampIdList;
	}
}
