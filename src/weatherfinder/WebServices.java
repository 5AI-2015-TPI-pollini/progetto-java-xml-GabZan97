
package weatherfinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Zanelli Gabriele
 */

public class WebServices {
    private static XPath xpath;
    
    public WebServices() {
        xpath = XPathFactory.newInstance().newXPath();
    }
    
    public static String findAddressCoords(String address) {
    
        try {
            URL googleUrl = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address=" + address + "&sensor=false");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(googleUrl.openStream());
            
            String latitude = getValues(doc,"/GeocodeResponse/result/geometry/location/lat/text()");
            String longitude = getValues(doc,"/GeocodeResponse/result/geometry/location/lng/text()");
            System.out.println("Latitude:"+latitude+"\nLongitude"+longitude);
            return latitude+","+longitude;
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String findMeteoByCoords(String latitude, String longitude) {
        try {
            URL meteoUrl = new URL("http://api.openweathermap.org/data/2.5/weather?"
                    + "lat="+ latitude + "&lon="+ longitude +"&mode=xml"
                    + "&appid=2de143494c0b295cca9337e1e96b00e0");
            Document doc = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(meteoUrl.openStream());
            
            String temperature = getValues(doc,"/current/temperature/@value"); // float Kelvin
            String humidity = getValues(doc,"/current/humidity/@value"); // int %
            String windSpeed = getValues(doc,"/current/wind/@value"); // float km
            String weatherType = getValues(doc,"/current/weather/@value"); // string
            String precipitation = getValues(doc,"/current/precipitation/@mode"); // string
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
    private static String getValues(Document doc, String path) {
        try {
            XPathExpression genericPath = xpath.compile(path);
            String genericValue = ((NodeList)genericPath.evaluate(doc, XPathConstants.STRING)).item(0).getNodeValue();
            return genericValue;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static void configProxy() {
        try {
            BufferedReader wantProxy = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Wanna use proxy?");
            String choose = wantProxy.readLine();
            if(choose.equals("Yes"))
                return ;
            else {
                Authenticator.setDefault(
                        new Authenticator() {
                            @Override
                            public PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("Username", "Password".toCharArray());
                            }
                        }
                );
                System.setProperty("http.proxyUser", "Username");
                System.setProperty("http.proxyPassword", "Password");
            }
            System.setProperty("http.proxyHost", "IP");
            System.setProperty("http.proxyPort", "Port");
        } catch (IOException ex) {
            Logger.getLogger(ProxyUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
