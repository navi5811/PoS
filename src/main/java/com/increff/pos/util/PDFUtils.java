package com.increff.pos.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import com.increff.pos.model.BillData;



public class PDFUtils {

    public static void generatePDFFromJavaObject(BillData billData, String fileName) throws Exception {
        // XML source reads an XML data file and populates the columns in the source output with the data
        ByteArrayOutputStream xmlSource = getXMLSource(billData);
        //Acts as an holder for a transformation Source in the form of a stream of XML markup.
        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(xmlSource.toByteArray()));
        generatePDF(streamSource, fileName);
    }

    //marshaller converts java context to xml
    private static ByteArrayOutputStream getXMLSource(BillData billData) {
        JAXBContext context;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            context = JAXBContext.newInstance(BillData.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(billData, outStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return outStream;
    } 

    private static void generatePDF(StreamSource streamSource, String fileName)
            throws FOPException, TransformerException, IOException {
        File xsltFile = new File("template.xsl");

        // create an instance of fop factory
        FopFactory fopFactory = FopFactory.newInstance();

        // a user agent is needed for transformation
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        // Setup output

        // Set File Location

        try (OutputStream out = new java.io.FileOutputStream(fileName)) {
            // Construct fop with desired output format
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);

            // Setup XSLT
            TransformerFactory factory = TransformerFactory.newInstance();

            Transformer transformer = factory.newTransformer(new StreamSource(xsltFile));

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Start XSLT transformation and FOP processing
            // That's where the XML is first transformed to XSL-FO and then
            // PDF is created
            transformer.transform(streamSource, res);
        }
    }
}