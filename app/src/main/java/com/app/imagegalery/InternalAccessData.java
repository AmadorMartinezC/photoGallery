package com.app.imagegalery;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class InternalAccessData {

    public static List<Image> getImagesFromStorage(Context context) {
        List<Image> imagesList = new ArrayList<Image>();

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;

        String imagesDirectory;
        Bitmap bitmap;
        try {
            Log.d("CONTEXT", context.getFilesDir().toString());
            imagesDirectory = context.getFilesDir() + File.separator + "images";
            File[] listImages = new File(imagesDirectory).listFiles();
            for (File files : listImages) {
                if (files.getName().endsWith(".jpg") || files.getName().endsWith(".png")) {
                    bitmap = BitmapFactory.decodeFile(files.getPath(), bitmapOptions);
                    Image imageItem = new Image(files.getName(), bitmap, commentFinder(context, files.getName()));
                    imagesList.add(imageItem);
                }
            }

            File userImages = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File[] photos = userImages.listFiles();
            if (photos != null) {
                for (File file : photos) {
                    bitmap = BitmapFactory.decodeFile(file.getPath(), bitmapOptions);
                    Image it = new Image(file.getName(), bitmap, commentFinder(context, file.getName()));
                    imagesList.add(it);
                }
            }

            return imagesList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void copyAssetsIntoInternalStorage(Context context) {
        AssetManager am = context.getAssets();
        String[] filesArray = null;
        try {
            filesArray = am.list("example-files");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String name : filesArray) {
            InputStream inputStream;
            OutputStream outputStream;
            try {
                inputStream = am.open("example-files" + File.separator + name);
                File outFile = new File(context.getFilesDir(), "images" + File.separator + name);
                File imagesDir = new File(context.getFilesDir(), "images");
                if (!imagesDir.exists())
                    imagesDir.mkdirs();
                outputStream = new FileOutputStream(outFile);
                copyFile(inputStream, outputStream);
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public static String commentFinder(Context context, String imageName) {
        String text = null;

        Document commentsDoc = null;
        try {
            commentsDoc = getCommentsDocument(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDoc != null) {
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();
            XPathExpression xPathExpr = null;
            try {
                xPathExpr = xpath.compile("//comment[@fileName = '" + imageName + "']/text()");
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }

            try {
                if (xPathExpr != null)
                    text = (String) xPathExpr.evaluate(commentsDoc, XPathConstants.STRING);
            } catch (XPathExpressionException e) {
                e.printStackTrace();
            }
        }

        return text;
    }

    public static void saveComment(Context context, String name, String comment) {
        Document commentsDoc = null;

        try {
            commentsDoc = getCommentsDocument(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (commentsDoc != null) {
            try {
                XPath xPath = XPathFactory.newInstance().newXPath();
                Node node = (Node) xPath.compile("//comment[@fileName = '" + name + "']").evaluate(commentsDoc, XPathConstants.NODE);
                if (node != null)
                    node.setTextContent(comment);
                else {
                    Element e = commentsDoc.createElement("comment");
                    e.setAttribute("fileName", name);
                    e.setTextContent(comment);
                    Element root = commentsDoc.getDocumentElement();
                    root.appendChild(e);
                }
                Transformer transformer = createXmlTransformer();
                overwriteXmlFile(new File(context.getFilesDir(), "images" + File.separator + "imageComments.xml"), commentsDoc, transformer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Document getCommentsDocument(Context context) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        FileInputStream fis = new FileInputStream(new File(context.getFilesDir(), "images" + File.separator + "imageComments.xml"));
        return builder.parse(fis);
    }

    private static Transformer createXmlTransformer() throws Exception {
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }

    private static void overwriteXmlFile(File xmlFile, Document document, Transformer transformer)
            throws FileNotFoundException, TransformerException {
        StreamResult result = new StreamResult(new PrintWriter(
                new FileOutputStream(xmlFile, false)));
        DOMSource source = new DOMSource(document);
        transformer.transform(source, result);
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}