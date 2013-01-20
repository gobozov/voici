package com.speechpro.util;

import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

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
            Log.d("speechpro: ", "status = " + status);
            String cardId = root.getAttribute(ATTRIBUTE_CARD_ID);
            Log.d("speechpro: ", "cardId = " + cardId);

            if (Utils.isValidAttributes(status, cardId)){
                result = new ResponseResult(ResponseResult.Status.valueOf(status), cardId);
                Log.d("speechpro: ", "result = " + result);
            }

        }catch (ParserConfigurationException e) {
            e.printStackTrace();
            Log.d("speechpro: ", e.getMessage());
        } catch (SAXException e) {
            e.printStackTrace();
            Log.d("speechpro: ", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("speechpro: ", e.getMessage());
        }
         return result;
    }



}
