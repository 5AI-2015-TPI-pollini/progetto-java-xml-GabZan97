
package weatherfinder;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Control the Application's Graphic Interface 
 * @author Gabriele
 */

public class GUIController implements Initializable{
    
    @FXML
    private Button searchButton;
    @FXML
    private TextField searchField;
    @FXML
    private TextField weatherField,temperatureField,windField,umidityField,precipitationField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConnectionUtilities checker = new ConnectionUtilities();
        
        // Add an action on button click that check connection and if it works, retrieve information from Web Services
            searchButton.setOnAction(e -> {
                if(!checker.tryConnection()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Internet connection not available");
                    alert.setHeaderText("Ooops!");
                    alert.setContentText("I can't reach the internet!\n "
                            + "Check connection or proxy configuration. ");
                    alert.show();
                }
                
                String[] splitted;
                String coords = WebServices.findAddressCoords(searchField.getText());
                
                // If Google can't find user's search, show an alert and allow the user to search again
                if(coords==null)
                {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Nothing was found :(");
                    alert.setHeaderText("Ooops!");
                    alert.setContentText("Oops! Seems like we can't find this address!\n"
                            + "Try to search something else.");
                    alert.show();
                    searchField.setText("");
                }
                else {
                    splitted = coords.split("§");
                    String conditions = WebServices.findMeteoByCoords(splitted[0],splitted[1]);
                    splitted = conditions.split("§");
                    weatherField.setText(splitted[0]);
                    temperatureField.setText(new DecimalFormat("#").format((Double.valueOf(splitted[1])-273.15))+"° C");
                    umidityField.setText(splitted[2]+"%");
                    windField.setText(splitted[3]+" km/h");
                    precipitationField.setText(splitted[4]);
                }
            });
    }
    
}
