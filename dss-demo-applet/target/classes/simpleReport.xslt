<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:dss="http://dss.esig.europa.eu/validation/diagnostic">

	<xsl:output method="xml"
	            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
	            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" indent="yes"/>

	<xsl:param name="output_template_name"/>

	<xsl:template match="/dss:SimpleReport">
		<html>
			<head>
				<title>Validation Simple Report</title>
				<style type="text/css">
					body {
						font-family: Calibri;
						font-size: 100%;
					}

					th, td {
						text-align: left;
						vertical-align: top;
					}

					th {
						font-weight: inherit;
						width: 150px;
					}

					tr.signature-start, tr.signature-start th, tr.signature-start td {
						border-top: 1px solid gray;
					}

					th.indication {
						font-weight: bold;
					}

					th.indication .indication-icon {
						font-size: 110%;
						margin-right: 0.5em;
						font-style: italic;
					}

					.VALID {
						color: green;
					}

					.INDETERMINATE {
						color: orangered;
					}

					.INVALID {
						color: red;
					}

					td.signatureLevel {
						font-weight: bold;
					}

					tr.documentInformation {
						color: darkgreen;
					}

					tr.documentInformation th {
						padding-left: 2em;
					}

					tr.documentInformation-header, tr.documentInformation-header th, tr.documentInformation-header td {
						border-top: 1px solid gray;
					}

					tr.documentInformation-header th {
						padding-left: 0;
						font-weight: bold;
					}
				</style>
			</head>
			<body>
				<img src="http://www.certification.tn/sites/default/files/images/header_ance.png"/>
				<table>
					<xsl:call-template name="documentInformationHeader"/>
					<!--<xsl:call-template name="Global"/>-->
					<xsl:apply-templates/>
					<tr>
						<th colspan="2"></th>
					</tr>
					<tr class="signature-start">
						<th colspan="2">
							>>>
							<a href="{$output_template_name}diagnostic-data.xml">diagnostic-data</a>
							|
							>>>
							<a href="{$output_template_name}detailed-report.xml">detailed-report</a>
							|
							>>>
							<a href="{$output_template_name}simple-report.xml">simple-report</a>
						</th>
					</tr>
				</table>
			</body>
		</html>
	</xsl:template>


	<xsl:template match="dss:Policy"/>
	<xsl:template match="dss:ValidationTime"/>
	<xsl:template match="dss:DocumentName"/>
	<xsl:template match="dss:DetachedContents"/>
	<xsl:template match="dss:Global"/>

	<xsl:template name="documentInformationHeader">

		<tr class="documentInformation documentInformation-header">
			<th colspan="2">INDEPENDENT VERIFIER VALIDATION REPORT</th>
		</tr>
		<tr class="signature-start">
			<th colspan="2">Documents >>></th>
		</tr>
		<tr class="documentInformation documentInformation-type">
			<th></th>
			<td>
				Document name:
				<br/>
				>
				<xsl:value-of select="dss:DocumentName"/>
			</td>
		</tr>
		<tr class="documentInformation documentInformation-type">
			<th></th>
			<td>
				Detached contents:
				<br/>
				<xsl:for-each select="dss:DetachedContents/dss:DocumentName">
					>
					<xsl:value-of select="text()"/>
					<br/>
				</xsl:for-each>
			</td>
		</tr>
		<tr class="documentInformation documentInformation-type">
			<th></th>
			<td>
				Validation date and time:
				<xsl:value-of select="dss:ValidationTime/text()"/>
				<br/>
				<br/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template name="Global">

		<xsl:if test="dss:Global/dss:SignaturesCount/text() &gt; 1">

			<tr class="documentInformation documentInformation-header">
				<th colspan="2">GLOBAL VALIDATION RESULT</th>
			</tr>
			<xsl:variable name="indicationClass" select="dss:Global/dss:Indication/text()"/>
			<tr class="signature-start">
				<th colspan="2">
					<xsl:attribute name="class" xml:space="preserve">indication <xsl:value-of select="$indicationClass"/></xsl:attribute>
					<span class="indication-icon">
						<xsl:choose>
							<xsl:when test="$indicationClass='VALID'">V</xsl:when>
							<xsl:when test="$indicationClass='INDETERMINATE'">?</xsl:when>
							<xsl:when test="$indicationClass='INVALID'">X</xsl:when>
						</xsl:choose>
					</span>
					<xsl:value-of select="dss:Global/dss:Indication"/>
				</th>
			</tr>
			<xsl:apply-templates select="dss:Global/dss:SubIndication">
				<xsl:with-param name="indicationClass" select="$indicationClass"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="dss:Global/dss:Error">
				<xsl:with-param name="indicationClass" select="$indicationClass"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="dss:Global/dss:Warning">
				<xsl:with-param name="indicationClass" select="$indicationClass"/>
			</xsl:apply-templates>
			<xsl:apply-templates select="dss:Global/dss:Info">
				<xsl:with-param name="indicationClass" select="$indicationClass"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template match="dss:Signature">
		<tr class="documentInformation documentInformation-header">
			<th colspan="2">INFORMATION ON THE SIGNATURE</th>
		</tr>
		<tr class="signature-start">
			<th>Signature >>></th>
			<td>
				On claimed time:
				<xsl:value-of select="dss:SigningTime"/>
			</td>
		</tr>
		<tr>
			<th></th>
			<td>
				Signature format:
				<xsl:value-of select="@SignatureFormat"/>
				<br/>
			</td>
		</tr>
		<tr>
			<th>Signing certificate >>></th>
			<td>
				Signed by:
				<xsl:value-of select="dss:SignedBy"/>
			</td>
		</tr>
		<tr>
			<th></th>
			<td>
				Subject Distinguished Name:
				<br/>
				<xsl:value-of select="dss:SubjectDistinguishedName"/>
			</td>
		</tr>
		<tr>
			<th></th>
			<td>
				Validity: [<xsl:value-of select="dss:NotBefore"/> - <xsl:value-of select="dss:NotAfter"/>]
				<br/>
				<br/>
			</td>
		</tr>
		<tr>
			<th>Timestamps >>></th>
			<td>
				<xsl:for-each select="dss:Timestamps/dss:Timestamp">
					Type:
					<xsl:value-of select="@Type"/>
					<br/>
					Production Time:
					<xsl:value-of select="dss:ProductionTime"/>
					<br/>
					Subject Distinguished Name:
					<xsl:value-of select="dss:SubjectDistinguishedName"/>
					<br/>
					Signing certificate validity: [<xsl:value-of select="dss:NotBefore"/> - <xsl:value-of select="dss:NotAfter"/>]
					<br/>
					<br/>
				</xsl:for-each>
			</td>
		</tr>
		<tr class="documentInformation documentInformation-header">
			<th colspan="2">SUMMARY OF THE VALIDATION</th>
		</tr>
		<xsl:variable name="indicationClass" select="dss:Indication/text()"/>
		<tr class="signature-start">
			<th colspan="2">
				<xsl:attribute name="class" xml:space="preserve">indication <xsl:value-of select="$indicationClass"/>
		</xsl:attribute>
				<span class="indication-icon">
					<xsl:choose>
						<xsl:when test="$indicationClass='VALID'">V</xsl:when>
						<xsl:when test="$indicationClass='INDETERMINATE'">?</xsl:when>
						<xsl:when test="$indicationClass='INVALID'">X</xsl:when>
					</xsl:choose>
				</span>
				<xsl:value-of select="dss:Indication"/>
			</th>
		</tr>
		<xsl:apply-templates select="dss:SubIndication">
			<xsl:with-param name="indicationClass" select="$indicationClass"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="dss:Error">
			<xsl:with-param name="indicationClass" select="$indicationClass"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="dss:Warning">
			<xsl:with-param name="indicationClass" select="$indicationClass"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="dss:Info">
			<xsl:with-param name="indicationClass" select="$indicationClass"/>
		</xsl:apply-templates>
	</xsl:template>


	<xsl:template match="dss:SubIndication">
		<xsl:param name="indicationClass"/>
		<tr class="info">
			<th></th>
			<td class="{$indicationClass}">
				<xsl:value-of select="text()"/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="dss:Error">
		<xsl:param name="indicationClass"/>
		<tr class="info">
			<th></th>
			<td class="{$indicationClass}">
				Error:
				<xsl:value-of select="text()"/>
				<br/>
				> Location:
				<xsl:value-of select="@Location"/>
				<br/>
				<xsl:for-each select="@*[not(name()='NameId') and not(name()='Location')]">
					>
					<xsl:value-of select="name()"/> =
					<xsl:value-of select="."/>
					<br/>
				</xsl:for-each>
				<br/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="dss:Warning">
		<xsl:param name="indicationClass"/>
		<tr class="info">
			<th></th>
			<td class="{$indicationClass}">
				Warning:
				<xsl:value-of select="text()"/>
				<br/>
				<xsl:for-each select="@*[not(name()='NameId') and not(name()='Location')]">
					>
					<xsl:value-of select="name()"/> =
					<xsl:value-of select="."/>
					<br/>
				</xsl:for-each>
				<br/>
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="dss:Info">
		<xsl:param name="indicationClass"/>
		<tr class="info">
			<th></th>
			<td class="{$indicationClass}">
				<xsl:for-each select="@*[not(name()='NameId') and not(name()='Location')]">
					>
					<xsl:value-of select="name()"/> =
					<xsl:value-of select="."/>
					<br/>
				</xsl:for-each>
				<br/>
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
