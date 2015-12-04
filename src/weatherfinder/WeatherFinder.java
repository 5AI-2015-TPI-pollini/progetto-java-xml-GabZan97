
package weatherfinder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Start the Appliacation adding structure
 * @author Zanelli Gabriele
 */

public class WeatherFinder extends Application {
            
    @Override
    public void start(Stage myStage) {
        try {
            // Import file FXML containg GUI Structure and the Icon
            Parent root = FXMLLoader.load(WeatherFinder.class.getClassLoader().getResource("res/GUI.fxml"));
            myStage.setTitle("Weather Finder");
            myStage.getIcons().add(new Image(WeatherFinder.class.getClassLoader().getResourceAsStream("res/icon.png")));
            myStage.setScene((new Scene(root)));
            myStage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(WeatherFinder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Main function starting graphic interface
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private void createApplicationCode(){
        // Create a table
        GridPane rootGrid = new GridPane();
        rootGrid.setPadding(new Insets(15));
        rootGrid.setHgap(5);
        rootGrid.setVgap(5);
        rootGrid.setAlignment(Pos.CENTER);
        
        // Create a scene containing the weather information in a table
        Scene weatherScene = new Scene(rootGrid, 320, 250);
        
        TextField searchField = new TextField();
        rootGrid.add(searchField, 0, 0);
        Button searchButton = new Button();
        searchButton.setText("Search");
        rootGrid.add(searchButton,1,0);
         
        // Create all labels and textfield containing result information
        rootGrid.add(new Label("Weather Type:"), 0, 1);
        TextField weatherField = new TextField();
        rootGrid.add(weatherField, 1, 1);
        
        rootGrid.add(new Label("Temperature:"), 0, 2);
        TextField temperatureField = new TextField();
        rootGrid.add(temperatureField, 1, 2);
        
        rootGrid.add(new Label("Umidity:"), 0, 3);
        TextField umidityField = new TextField();
        rootGrid.add(umidityField, 1, 3);
        
        rootGrid.add(new Label("Wind Speed:"), 0, 4);
        TextField windField = new TextField();
        rootGrid.add(windField, 1, 4);
        
        rootGrid.add(new Label("Precipitation:"), 0, 5);
        TextField precipitationField = new TextField();
        rootGrid.add(precipitationField, 1, 5);
    }
    
}
