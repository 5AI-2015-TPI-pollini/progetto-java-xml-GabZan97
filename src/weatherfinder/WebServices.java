
package weatherfinder;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.xml.sax.SAXException;

/**
 * @author Zanelli Gabriele
 */

public class WebServices {
   
    public static String findAddressCoords(String address) {
    
        try {
            // Create the proper URL in order to search the address with Google Geocode
            URL googleUrl = new URL("http://maps.googleapis.com/maps/api/geocode/xml?address=" + address + "&sensor=false");
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(googleUrl.openStream());
            XPath xpath= XPathFactory.newInstance().newXPath();
            
            // Obtain latitude and longitude values
            String latitude = getValues(xpath,doc,"/GeocodeResponse/result/geometry/location/lat/text()");
            String longitude = getValues(xpath,doc,"/GeocodeResponse/result/geometry/location/lng/text()");
            
            // If the address searched by the User doesn't exist, returns null
            if(latitude=="" || longitude=="")
            {
                System.out.println("This address doesn't exist!");
                return null;
            }
            else {
                System.out.println("Latitude:"+latitude+"\nLongitude"+longitude+" found!");
                return latitude+"§"+longitude;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String findMeteoByCoords(String latitude, String longitude) {
        try {
            // Create the proper URL in order to search the coordinates with Open Weather
            URL meteoUrl = new URL("http://api.openweathermap.org/data/2.5/weather?"
                    + "lat="+ latitude + "&lon="+ longitude +"&mode=xml"
                    + "&appid=2de143494c0b295cca9337e1e96b00e0");
            Document doc = (Document) DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(meteoUrl.openStream());
            XPath xpath= XPathFactory.newInstance().newXPath();
            
            // Obtain weather conditions of the place
            String temperature = getValues(xpath,doc,"/current/temperature/@value"); // float Kelvin
            String humidity = getValues(xpath,doc,"/current/humidity/@value"); // int %
            String windSpeed = getValues(xpath,doc,"/current/wind/speed/@value"); // float km
            String weatherType = getValues(xpath,doc,"/current/weather/@value"); // string
            String precipitation = getValues(xpath,doc,"/current/precipitation/@mode"); // string
            
            return weatherType+"§"+temperature+"§"+humidity+"§"+windSpeed+"§"+precipitation;
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
    private static String getValues(XPath xpath, Document doc, String path) {
        try {
            XPathExpression genericPath = xpath.compile(path);
            String genericValue = genericPath.evaluate(doc, XPathConstants.STRING).toString();
            return genericValue;
        } catch (XPathExpressionException ex) {
            Logger.getLogger(WebServices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
