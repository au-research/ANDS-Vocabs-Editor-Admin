<?xml version="1.0" encoding="UTF-8"?>
<!--

This script is based on:
XSLT script to format SPARQL Query Results XML Format into xhtml

Copyright 2004, 2005 World Wide Web Consortium, (Massachusetts
Institute of Technology, European Research Consortium for
Informatics and Mathematics, Keio University). All Rights
Reserved. This work is distributed under the W3C® Software
License [1] in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.

[1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231

$Id: result-to-html.xsl,v 1.1 2012/11/07 00:38:29 sandro Exp $

Modified by ANDS for Editor Admin Tool.

-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:res="http://www.w3.org/2005/sparql-results#" exclude-result-prefixes="res xsl">

  <!--
  <xsl:output
    method="html"
    media-type="text/html"
    doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
    indent="yes"
    encoding="UTF-8"/>
  -->

  <!-- or this? -->

  <xsl:output method="xml"
   indent="yes"
    encoding="UTF-8"
     doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
     doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      omit-xml-declaration="yes"/>


  <xsl:template name="header">
    <div>
      <h2>Header</h2>
      <xsl:for-each select="res:head/res:link"> 
	<p>Link to <xsl:value-of select="@href"/></p>
      </xsl:for-each>
    </div>
  </xsl:template>

  <xsl:template name="boolean-result">
    <div>
      <h2>Boolean Result</h2>
      <p>Value <xsl:value-of select="res:boolean"/></p>
    </div>
  </xsl:template>


  <xsl:template name="vb-result">
    <div>
      <h4>Variable Bindings Result</h4>

      <table class="table table-condensed table-striped">
	<xsl:text>
	</xsl:text>
  <thead>
	<tr>
	  <xsl:for-each select="res:head/res:variable">
        <!-- ANDS modification: support nicer headings. Use an underscore
             in the variable name; it will be replaced with a space. -->
	    <th><xsl:value-of select="translate(@name,'_',' ')"/></th>
	  </xsl:for-each>
	</tr>
  </thead>
	<xsl:text>
	</xsl:text>
  <tbody>
	<xsl:for-each select="res:results/res:result">
	  <tr>
	    <xsl:apply-templates select="."/>
	  </tr>
	</xsl:for-each>
  </tbody>
      </table>
    </div>
  </xsl:template>

  <xsl:template match="res:result">
    <xsl:variable name="current" select="."/>
    <xsl:for-each select="//res:head/res:variable">
      <xsl:variable name="name" select="@name"/>
      <td>
	<xsl:choose>
	  <xsl:when test="$current/res:binding[@name=$name]">
	    <!-- apply template for the correct value type (bnode, uri,
	    literal) -->
	    <xsl:apply-templates select="$current/res:binding[@name=$name]"/>
	  </xsl:when>
	  <xsl:otherwise>
	    <!-- no binding available for this variable in this solution -->
	    [unbound]
	  </xsl:otherwise>
	</xsl:choose>
      </td>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="res:bnode">
    <xsl:text>nodeID </xsl:text>
    <xsl:value-of select="text()"/>
  </xsl:template>

  <xsl:template match="res:uri">
    <xsl:variable name="uri" select="text()"/>
    <xsl:text>URI </xsl:text>
    <xsl:value-of select="$uri"/>
  </xsl:template>

  <xsl:template match="res:literal">
    <xsl:choose>
      <!-- ANDS modification: specific nice treatment for booleans:
           don't print the datatype. -->
      <xsl:when test="@datatype and @datatype = 'http://www.w3.org/2001/XMLSchema#boolean'">
        <!-- Boolean literal value -->
        <xsl:value-of select="text()"/>
      </xsl:when>
      <xsl:when test="@datatype">
  <!-- datatyped literal value -->
  <xsl:value-of select="text()"/> (datatype <xsl:value-of select="@datatype"/> )
      </xsl:when>
      <xsl:when test="@xml:lang">
	<!-- lang-string -->
	<xsl:value-of select="text()"/> @ <xsl:value-of select="@xml:lang"/>
      </xsl:when>
      <xsl:when test="string-length(text()) != 0">
	<!-- present and not empty -->
	<xsl:value-of select="text()"/>
      </xsl:when>
      <xsl:when test="string-length(text()) = 0">
	<!-- present and empty -->
	[empty literal]
      </xsl:when>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="res:sparql">

	<xsl:if test="res:head/res:link">
	  <xsl:call-template name="header"/>
	</xsl:if>

	<xsl:choose>
	  <xsl:when test="res:boolean">
	    <xsl:call-template name="boolean-result"/>
	  </xsl:when>

	  <xsl:when test="res:results">
	    <xsl:call-template name="vb-result"/>
	  </xsl:when>

	</xsl:choose>

  </xsl:template>
</xsl:stylesheet>