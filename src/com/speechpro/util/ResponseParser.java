package com.speechpro.util;

import android.media.MediaRecorder;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 18.01.13
 * Time: 0:58
 * To change this template use File | Settings | File Templates.
 */
public class ResponseParser {

    private static final String ATTRIBUTE_STATUS = "Status";
    private static final String ATTRIBUTE_CARD_ID = "CardID";


    public ResponseResult getEnrollResult(InputStream xmlStream){
        ResponseResult result = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document dom = null;
        try {
            builder = factory.newDocumentBuilder();
            dom = builder.parse(xmlStream);
            Element root = dom.getDocumentElement();

            String status = root.getAttribute(ATTRIBUTE_STATUS);
            String cardId = root.getAttribute(ATTRIBUTE_CARD_ID);

            Log.d("speechpro: ", "status = " + status);
            Log.d("speechpro: ", "cardId = " + cardId);

            if (!status.equals(ResponseResult.Status.ERROR.getName())){
                result = new ResponseResult(ResponseResult.Status.valueOf(status), cardId);
            } else {
                result = new ResponseResult(root.getTextContent(), ResponseResult.Status.ERROR);
            }
            Log.d("speechpro: ", "result = " + result);

        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         return result;
    }


    public static void printDocument(Document doc, OutputStream out) throws IOException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(doc),
                new StreamResult(new OutputStreamWriter(out, "UTF-8")));
    }


}
