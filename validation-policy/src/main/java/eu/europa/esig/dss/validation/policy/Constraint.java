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
package eu.europa.esig.dss.validation.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.XmlDom;
import eu.europa.esig.dss.validation.policy.rules.AttributeName;
import eu.europa.esig.dss.validation.policy.rules.AttributeValue;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.policy.rules.MessageTag;
import eu.europa.esig.dss.validation.policy.rules.NodeName;
import eu.europa.esig.dss.validation.policy.rules.NodeValue;
import eu.europa.esig.dss.validation.policy.rules.SubIndication;
import eu.europa.esig.dss.validation.report.Conclusion;

/**
 * This class represents a constraint and indicates its level: IGNORE, INFORM, WARN, FAIL.
 */
public class Constraint implements NodeName, NodeValue, AttributeName, AttributeValue, Indication, SubIndication {

	private static final Logger LOG = LoggerFactory.getLogger(Constraint.class);

	/**
	 * Diagnostic data containing all static information
	 */
	protected XmlDom diagnosticData;

	/**
	 * This field represents the {@code XmlNode} of the constraint
	 */
	protected XmlNode node;

	/**
	 * This field represents the simple {@code String} value of the constraint
	 */
	protected String value;
	/**
	 * This field represents the simple {@code String} expected value of the constraint
	 */
	protected String expectedValue;
	/**
	 * This field represents the list of acceptable identifiers
	 */
	protected List<String> identifierList;
	protected String indication;
	protected String subIndication;
	protected MessageTag failureMessageTag;
	protected Map<String, String> messageAttributes = new HashMap<String, String>();
	protected Conclusion conclusion;
	/**
	 * This {@code Map} contains the list of Info attributes to be added to the constraint node.
	 */
	protected Map<String, String> infoAttributes = null;
	/**
	 * This field represents the {@code List} of {@code String} values of the constraint
	 */
	protected List<String> valueList;

	/**
	 * Message {@code Level} associated to the constraint.
	 */
	protected Level level;

	/**
	 * Defines the {@code Membership} of the {@code List} of value. The default value is {@code Membership.ALL}
	 */
	private Membership membership = Membership.ALL;

	/**
	 * This is the default constructor. It takes a level of the constraint as parameter. The string representing the level is trimmed and capitalized. If there is no corresponding
	 * {@code Level} then the {@code DSSException} is raised.
	 *
	 * @param level the constraint level string.
	 */
	public Constraint(final String level) throws DSSException {

		try {
			this.level = Level.valueOf(level.trim().toUpperCase());
		} catch (IllegalArgumentException e) {

			throw new DSSException("The validation policy configuration file should be checked: " + e.getMessage(), e);
		}
	}

	/**
	 * This method creates the constraint {@code XmlNode}.
	 *
	 * @param parentNode Represents the parent {@code XmlNode} to which the constraint node should be attached.
	 * @param messageTag is the message describing the constraint.
	 * @return the {@code XmlNode} representing the current constraint in the validation process
	 */
	public XmlNode create(final XmlNode parentNode, final MessageTag messageTag) {

		this.node = parentNode.addChild(CONSTRAINT);
		this.node.addChild(NAME, messageTag.getMessage()).setAttribute(NAME_ID, messageTag.name());
		return this.node;
	}

	/**
	 * This method creates the constraint {@code XmlNode}. This method should be used when the message describing the constraint comports dynamic parameters.
	 *
	 * @param parentNode Represents the parent {@code XmlNode} to which the constraint node should be attached.
	 * @param messageTag is the message describing the constraint.
	 * @param parameters the dynamic parameters to integrate into the message.
	 * @return the {@code XmlNode} representing the current constraint in the validation process.
	 */
	public XmlNode create(final XmlNode parentNode, final MessageTag messageTag, final String parameters) {

		this.node = parentNode.addChild(CONSTRAINT);
		final String message = String.format(messageTag.getMessage(), parameters);
		this.node.addChild(NAME, message).setAttribute(NAME_ID, messageTag.name());
		return this.node;
	}

	/**
	 * @return {@code XmlDom} representing encapsulated diagnostic data
	 */
	public XmlDom getDiagnosticData() {
		return diagnosticData;
	}

	/**
	 * Allows to link the diagnostic data to the {@code Constraint}
	 *
	 * @param diagnosticData {@code XmlDom} representing diagnostic data
	 */
	public void setDiagnosticData(final XmlDom diagnosticData) {
		this.diagnosticData = diagnosticData;
	}

	/**
	 * @param value the simple value of the constraint to set.
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * @param booleanValue the simple value of the constraint to set. The {@code boolean} is converted to its {@code String} representation.
	 */
	public void setValue(final boolean booleanValue) {
		this.value = String.valueOf(booleanValue);
	}

	/**
	 * @return the simple value of the constraint.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the list of real values.
	 *
	 * @param stringList {@code List} of {@code String}s
	 */
	public void setValue(final List<String> stringList) {

		this.valueList = stringList;
	}

	public String getExpectedValue() {
		return expectedValue;
	}

	/**
	 * @param expectedValue the simple expected value of the constraint to set.
	 */
	public void setExpectedValue(final String expectedValue) {
		this.expectedValue = expectedValue;
	}

	/**
	 * Defines the {@code Membership} of the element(s) to check.
	 *
	 * @param membership expected {@code Membership}
	 */
	public void setMembership(final Membership membership) {
		this.membership = membership;
	}

	/**
	 * @param validationDataXmlNode this {@code XmlNode} is used to add the constraint nodes
	 * @param conclusion            the {@code Conclusion} which indicates the result of the process
	 */
	public boolean checkCustomized(final XmlNode validationDataXmlNode, final Conclusion conclusion) {
		return true;
	}

	/**
	 * This method carry out the validation of the constraint.
	 *
	 * @return true if the constraint is met, false otherwise.
	 */
	public boolean check() {

		if (ignore()) {

			node.addChild(STATUS, IGNORED);
			return true;
		}
		if (inform()) {

			node.addChild(STATUS, INFORMATION);
			node.addChild(INFO, null, messageAttributes).setAttribute(EXPECTED_VALUE, expectedValue).setAttribute(CONSTRAINT_VALUE, value);
			return true;
		}
		boolean error = value.isEmpty();
		if (!error) {

			if (!"*".equals(expectedValue)) {

				error = expectedValue != null && !expectedValue.equals(value);
			}
		}
		if (error) {

			if (warn()) {

				node.addChild(STATUS, WARN);
				node.addChild(WARNING, failureMessageTag, messageAttributes);
				if (StringUtils.isNotBlank(expectedValue) && !TRUE.equals(expectedValue) && !FALSE.equals(expectedValue)) {
					messageAttributes.put(EXPECTED_VALUE, expectedValue);
					messageAttributes.put(CONSTRAINT_VALUE, value);
				}
				conclusion.addWarning(failureMessageTag, messageAttributes);
				return true;
			}
			node.addChild(STATUS, KO);
			if (StringUtils.isNotBlank(expectedValue) && !TRUE.equals(expectedValue) && !FALSE.equals(expectedValue)) {
				messageAttributes.put(EXPECTED_VALUE, expectedValue);
				messageAttributes.put(CONSTRAINT_VALUE, value);
			}
			if (StringUtils.isNotBlank(indication)) {

				conclusion.setIndication(indication, subIndication);
			}
			conclusion.addError(failureMessageTag, messageAttributes);
			return false;
		}
		addOkNode();
		return true;
	}

	/**
	 * This method carries out the validation of the constraint.
	 *
	 * @return true if the constraint is met, false otherwise.
	 */
	public boolean checkInList() {

		if (ignore()) {

			node.addChild(STATUS, IGNORED);
			return true;
		}
		if (inform()) {

			node.addChild(STATUS, INFORMATION);
			node.addChild(INFO, null, messageAttributes).setAttribute(EXPECTED_VALUE, expectedValue).setAttribute(CONSTRAINT_VALUE, value);
			return true;
		}
		boolean contains = false;
		if (value != null && "*".equals(expectedValue)) {
			contains = true;
		} else if (CollectionUtils.isNotEmpty(valueList)) {

			if (membership == Membership.ALL) {
				contains = valueList.containsAll(identifierList);
			} else {

				for (final String value : valueList) {
					if (identifierList.contains(value)) {

						contains = membership == Membership.ANY ? true : false /*NONE*/;
						break;
					}
				}
			}
			value = valueList.toString();
		} else {
			contains = RuleUtils.contains1(value, identifierList);
		}
		if (!contains) {

			if (warn()) {

				node.addChild(STATUS, WARN);
				messageAttributes.put(EXPECTED_VALUE, expectedValue);
				messageAttributes.put(CONSTRAINT_VALUE, value);
				conclusion.addWarning(failureMessageTag, messageAttributes);
				return true;
			}
			node.addChild(STATUS, KO);
			conclusion.setIndication(indication, subIndication);
			messageAttributes.put(EXPECTED_VALUE, expectedValue);
			messageAttributes.put(CONSTRAINT_VALUE, value);
			conclusion.addError(failureMessageTag, messageAttributes);
			return false;
		}
		addOkNode();
		return true;
	}

	protected void addOkNode() {

		node.addChild(STATUS, OK);
		if (infoAttributes != null) {
			node.addChild(INFO, null, infoAttributes);
		}
	}

	/**
	 * @param indication        to return when failure
	 * @param subIndication     to return when failure
	 * @param failureMessageTag is the answer to be done in case of the constraint failure.
	 */
	public void setIndications(final String indication, final String subIndication, final MessageTag failureMessageTag) {

		this.indication = indication;
		this.subIndication = subIndication;
		this.failureMessageTag = failureMessageTag;
	}

	/**
	 * This method should be called when the failure of the constraint does not cause the failure of the process.
	 *
	 * @param failureMessageTag is the answer to be done in case of the constraint failure.
	 */
	public void setIndications(final MessageTag failureMessageTag) {

		this.failureMessageTag = failureMessageTag;
	}

	public void setConclusionReceiver(final Conclusion conclusion) {
		this.conclusion = conclusion;
	}

	public List<String> getIdentifierList() {
		return identifierList;
	}

	/**
	 * @param identifierList the {@code List} of identifiers to set.
	 */
	public void setIdentifierList(final List<String> identifierList) {
		this.identifierList = identifierList;
	}

	/**
	 * This method allows to add an attribute to the answer node (to the message).
	 *
	 * @param attributeName  the attribute name
	 * @param attributeValue the attribute value
	 */
	public Constraint setAttribute(final String attributeName, final String attributeValue) {

		messageAttributes.put(attributeName, attributeValue);
		return this;
	}

	/**
	 * This method returns the constraint's level.
	 *
	 * @return the {@code Level} of the constraint
	 */
	public Level getLevel() {
		return level;
	}

	public void setLevel(final Level level) {

		if (level == null) {
			throw new DSSException(Level.class.getSimpleName() + " cannot be null!");
		}
		this.level = level;
	}

	/**
	 * Says if the constraint should be ignored.
	 *
	 * @return true if the constraint should be ignored.
	 */
	public boolean ignore() {
		return level.equals(Level.IGNORE);
	}

	/**
	 * Indicates if the constraint should only return information.
	 *
	 * @return true if the constraint should only return information.
	 */
	public boolean inform() {
		return level.equals(Level.INFORM);
	}

	/**
	 * Says if the result of the constraint should be considered as warning.
	 *
	 * @return true if the constraint should be considered as warning.
	 */
	public boolean warn() {
		return level.equals(Level.WARN);
	}

	/**
	 * Indicates whether the constraint should fail when it is not met.
	 *
	 * @return true if the constraint should fail.
	 */
	public boolean fail() {
		return level.equals(Level.FAIL);
	}

	/**
	 * This method allows to clear all attributes.
	 */
	public void clearAttributes() {

		messageAttributes.clear();
	}

	/**
	 * This method allows to add an INFO note to the constraint. The attribute must be set beforehand: {@link #setAttribute(String, String)}
	 *
	 * @param attributeName {@code String}
	 */
	public void addInfo(final String attributeName) {

		final String attributeValue = messageAttributes.get(attributeName);
		if (attributeValue == null) {
			throw new DSSException("The attribute '" + attributeName + "' is not defined!");
		}
		if (infoAttributes == null) {
			infoAttributes = new HashMap<String, String>();
		}
		infoAttributes.put(attributeName, attributeValue);
	}

	public enum Level {IGNORE, INFORM, WARN, FAIL}

	public enum Membership {ALL, ANY, NONE}
}
