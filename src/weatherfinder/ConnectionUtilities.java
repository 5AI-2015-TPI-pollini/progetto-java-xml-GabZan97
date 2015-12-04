package weatherfinder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Contains utilities that allows user to check connection, even under proxy
 * @author Zanelli Gabriele
 */

public class ConnectionUtilities {
    
    private String serverAddress, proxyPort, ID, PW;
    private final String configFilePath = "proxy.config";

    /**
     * Try to test connection to internet by opening stream to Google, if it can't reach, automatically
     * show proxy configuration
     */
    public ConnectionUtilities() {
    }

    /**  
     * Check internet connection
     * @return a Boolean indicating if connection is established
     */
    public boolean tryConnection() {
        try {
            InputStream check = new URL("http://www.google.com").openConnection().getInputStream();
            System.out.println("Internet connection established!");
            return true;
        } catch (Exception ex) {
            System.out.println("Can't connect to Internet! Trying with proxy...");
            try {
                setProxy();
                InputStream check = new URL("http://www.google.com").openConnection().getInputStream();
                System.out.println("Internet connection established with proxy!");
                return true;
            } catch (Exception ex1) {
                // If can't reach internet even under proxy, delete the from configuration
                deleteConfigFile();
                System.out.println("Internet connection is not available!");
                return false;
            }
        }
    }
    
    // Set proxy settings from file
    private void setProxy() {
        readConfigFile();
        Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        ID, PW.toCharArray());
            }
        }
        );
        System.setProperty("http.proxyUser", ID);
        System.setProperty("http.proxyPassword", PW);
        System.setProperty("http.proxyHost", serverAddress);
        System.setProperty("http.proxyPort", proxyPort);
    }
    
    //If configuration file is missing, ask user to insert proxy settings
    private void configFileMissing(){
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("Proxy Settings");
        dialog.setHeaderText("Please add proper proxy configuration");

        ButtonType saveButton = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        // Create labels and fields in order to get proxy settings
        GridPane proxyGrid = new GridPane();
        proxyGrid.setHgap(10);
        proxyGrid.setVgap(10);
        proxyGrid.setPadding(new Insets(20, 150, 10, 10));

        TextField address = new TextField();
        address.setPromptText("192.168.0.1");
        TextField port = new TextField();
        port.setPromptText("8080");
        TextField username = new TextField();
        username.setPromptText("user");
        PasswordField password = new PasswordField();
        password.setPromptText("pw");

        proxyGrid.add(new Label("Server Address:"), 0, 0);
        proxyGrid.add(address, 1, 0);
        proxyGrid.add(new Label("Server Port:"), 0, 1);
        proxyGrid.add(port, 1, 1);
        proxyGrid.add(new Label("Username:"), 0, 2);
        proxyGrid.add(username, 1, 2);
        proxyGrid.add(new Label("Password:"), 0, 3);
        proxyGrid.add(password, 1, 3);

        dialog.getDialogPane().setContent(proxyGrid);

        dialog.setResultConverter((ButtonType dialogButton) -> {
            if (dialogButton == saveButton) {
                return new String[]{address.getText(), port.getText(), username.getText(), password.getText()};
            }
            if (dialogButton == ButtonType.CANCEL) {
                exit(2);
            }
            return null;
        });
        
        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(configuration -> {
            serverAddress = configuration[0];
            proxyPort = configuration[1];
            ID = configuration[2];
            PW = configuration[3];
        });
        writeConfigFile();
    }
    
    // Read proxy configuration from file
    private void readConfigFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(configFilePath))) {
            serverAddress = br.readLine();
            proxyPort = br.readLine();
            ID = br.readLine();
            PW = br.readLine();
        }catch (FileNotFoundException ex) {
            configFileMissing();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionUtilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    // Write proxy configuration to file
    private void writeConfigFile(){
        try (PrintWriter pw = new PrintWriter (configFilePath, "UTF-8")){
            pw.println(serverAddress);
            pw.println(proxyPort);
            pw.println(ID);
            pw.println(PW);
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ConnectionUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    // Delete proxy configuration file
    private void deleteConfigFile(){
        new File(configFilePath).delete();
    }
}
