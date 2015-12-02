
package weatherfinder;

import java.text.DecimalFormat;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author Zanelli Gabriele
 */

public class WeatherFinder extends Application {
    
    @Override
    public void start(Stage myStage) {
        myStage.setTitle("WeatherFinder");
        
        ConnectionUtilities checker = new ConnectionUtilities();
        
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
//        GridPane.setHalignment(searchButton, HPos.CENTER);
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
        
        // Second type of action of click
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });

        // Add an action on button click
         searchButton.setOnAction(e -> {
            String[] splitted;
    //        ProxyUtilities proxy = new ProxyUtilities("wwww.google.it");
            String coords = WebServices.findAddressCoords(searchField.getText().replace(" ","+"));
            
            // If Google can't find user's search, show an alert and allow the user to search again
            if(coords==null)
            {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Nothing was found :(");
                alert.setHeaderText("Ooops!");
                alert.setContentText("Oops! Seems like we can't find this address!\n"
                                + "Try to search something else!");
                alert.show();
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
        
        // Set the scene visible on stage
        myStage.setScene(weatherScene);
        myStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
