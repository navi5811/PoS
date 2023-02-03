<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format" exclude-result-prefixes="fo">
    <xsl:template match="billData">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin-top="2cm"
                                       margin-bottom="2cm" margin-left="2cm" margin-right="2cm">
                    <fo:region-body space-before="0.5in"/>
                    <fo:region-before region-name="xsl-region-before" space-after="3in"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4">
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block space-before="120pt" font-size="14pt" line-height="24px" font-weight="bold">
                        INVOICE
                    </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" space-before="120pt" margin-top="5in">
                    <fo:block font-size="10pt">
                        <fo:block font-size="10pt"  space-after="3mm">Order ID:
                            <xsl:value-of select="orderId"/>
                        </fo:block>
                        <fo:block font-size="10pt"  space-after="5mm">Time:
                            <xsl:value-of select="datetime"/>
                        </fo:block>
                        <fo:table width="100%" border-collapse="fixed" padding="1mm">
                            <fo:table-column column-width="8cm"/>
                            <fo:table-column column-width="2cm"/>
                            <fo:table-column column-width="3.5cm"/>
                            <fo:table-column column-width="3.5cm"/>
                            <fo:table-header>
                                <fo:table-row background-color="#f5f5f5" text-align="center" font-weight="bold">
                                    <fo:table-cell border="1px solid #b8b6b6">
                                        <fo:block padding="1mm">DESCRIPTION</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1px solid #b8b6b6">
                                        <fo:block padding="1mm">QTY</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1px solid #b8b6b6">
                                        <fo:block padding="1mm">UNIT PRICE</fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell border="1px solid #b8b6b6">
                                        <fo:block padding="1mm">AMOUNT</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-header>
                            <fo:table-body>
                                <xsl:apply-templates select="orderItemData"/>
                                <fo:table-row font-weight="bold">
                                            <fo:table-cell number-columns-spanned="3" text-align="right" padding-right="37pt" border="1px solid #b8b6b6">
                                              <fo:block padding="1mm">Total</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell  text-align="center" padding-right="3pt" background-color="#f5f5f5" border="1px solid #b8b6b6" >
                                              <fo:block padding="1mm">
                                                <xsl:value-of select="billAmount" />
                                              </fo:block>
                                            </fo:table-cell>
                                          </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:block>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="orderItemData">
        <fo:table-row>
<!--            <xsl:attribute name="font-weight">bold</xsl:attribute>-->

            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block padding="1mm">
                    <xsl:value-of select="name"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block padding="1mm">
                    <xsl:value-of select="productQuantity"/>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block padding="1mm">
                    <xsl:value-of select="productSellingPrice"/>
                </fo:block>
            </fo:table-cell>

            <fo:table-cell border="1px solid #b8b6b6" text-align="center">
                <fo:block padding="1mm">
                    <xsl:value-of select="(productQuantity * productSellingPrice)"/>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </xsl:template>
</xsl:stylesheet>