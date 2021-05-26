package com.app.imagegalery;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Data {

    static ArrayList recordList;

    public static void Dades(File records){

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // definimos el nodo que contendrá los elementos
            Element eData = doc.createElement("Data");

            // definimos cada uno de los elementos y le asignamos un valor
            Element eImage = doc.createElement("Image");
            eData.appendChild(eImage);

            Element eText = doc.createElement("Text");
            eData.appendChild(eText);

            // clases necesarias finalizar la creación del archivo XML
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("data.xml"));

            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



}