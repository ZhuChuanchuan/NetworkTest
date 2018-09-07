package com.zhcc.networktest;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Create by ZHCC on 2018/9/7
 */
public class MyHandler extends DefaultHandler {

    private String nodeName;
    private StringBuilder id;
    private  StringBuilder name;
    private StringBuilder version;


    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("app".equals(localName)) {
            Log.d("myhandler", "id is " + id.toString().trim());
            Log.d("myhandler", "name is " + name.toString().trim());
            Log.d("myhandler", "version is " + version.toString().trim());
            //清空stringbuilder
            name.setLength(0);
            id.setLength(0);
            version.setLength(0);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if ("id".equals(nodeName)) {
            id.append(ch, start, length);
        } else if ("name".equals(nodeName)) {
            name.append(ch, start, length);
        } else if ("version".equals(nodeName)) {
            version.append(ch, start, length);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //记录当前节点名
        nodeName=localName;
    }

    @Override
    public void startDocument() throws SAXException {
        id = new StringBuilder();
        name = new StringBuilder();
        version = new StringBuilder();
    }
}
