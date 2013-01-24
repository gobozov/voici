package com.voici.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
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
    private Context context;
    private SharedPreferences prefs;

    public ResponseParser(Context context) {
        this.context = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public ResponseResult getEnrollResult(InputStream xmlStream, boolean isVerify){
        ResponseResult result = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Document dom = null;
        String score = null;
        try {
            builder = factory.newDocumentBuilder();
            dom = builder.parse(xmlStream);
            Element root = dom.getDocumentElement();

            String status = root.getAttribute(ATTRIBUTE_STATUS);
            String cardId = root.getAttribute(ATTRIBUTE_CARD_ID);

            Log.d("voici: ", "status = " + status);
            Log.d("voici: ", "cardId = " + cardId);

            if (isVerify){
                score = getScores(dom);
                Log.d("voici: ", "score = " + score);

            }
            if (!status.equals(ResponseResult.Status.ERROR.getName())){

                if (isVerify){
                    int i = (int) (Double.valueOf(score) * 100);
                    String barrier = prefs.getString("key_score", "80");

                    if (i >= Integer.valueOf(barrier)){
                        result = new ResponseResult(ResponseResult.Status.valueOf(status), cardId, score);
                    } else {
                        result = new ResponseResult("Voice verification scores is too low", ResponseResult.Status.ERROR);
                    }
                }
                else
                    result = new ResponseResult(ResponseResult.Status.valueOf(status), cardId);
            } else {
                result = new ResponseResult(root.getTextContent(), ResponseResult.Status.ERROR);
            }
            Log.d("voici: ", "result = " + result);

        }catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
         return result;
    }


//    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
//        ResponseParser responseParser = new ResponseParser();
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document dom = builder.parse(new FileInputStream(new File("C:\\Users\\gb\\Desktop\\SpeechPro\\response_verify.xml")));
//        String scores = responseParser.getScores(dom);
//        System.out.println("scores = " + scores);
//        double d = Double.valueOf(scores);
//        int i = (int) (d * 100);
//        System.out.println("i = " + i);
//    }

    private String getScores(Document document){
        String score = null;
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        try {
            score = xpath.evaluate("/EnrollVerify/CardCompareResult/VoiceKeyScore/text()", document.getDocumentElement());
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return score;
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
