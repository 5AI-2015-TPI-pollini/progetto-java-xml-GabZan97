package weatherfinder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import static java.lang.System.exit;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
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
 * @author Zanelli Gabriele
 */

public class ConnectionUtilities {
    
    private String serverAddress, proxyPort, ID, PW;

    public ConnectionUtilities() {
        tryConnection();
    }

    private void tryConnection() {
        try {
            URLConnection check = new URL("http://www.google.com").openConnection();
            System.out.println("Internet connection established!");
        } catch (MalformedURLException ex) {
            Logger.getLogger(ConnectionUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Can't connect to Internet! Trying with proxy...");
            try {
                setProxy();
                URLConnection check = new URL("http://www.google.com").openConnection();
            } catch (IOException ex1) {
                System.out.println("Internet connection is not available!");
            }
        }
    }
    
    // Set proxy settings from file
    private void setProxy() {
        readSettings();
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
        dialog.setTitle("Login Dialog");
        dialog.setHeaderText("Please insert proxy configuration");

        ButtonType loginButtonType = new ButtonType("Login", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create labels and fields in order to get proxy settings
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField address = new TextField();
        address.setPromptText("192.168.0.1");
        TextField port = new TextField();
        port.setPromptText("8080");
        TextField username = new TextField();
        username.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");

        grid.add(new Label("Server Address:"), 0, 0);
        grid.add(address, 1, 0);
        grid.add(new Label("Server Port:"), 0, 1);
        grid.add(port, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(username, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(password, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter((ButtonType dialogButton) -> {
            if (dialogButton == loginButtonType) {
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
        writeSettings();
    }
    
    // Read proxy configuration from file
    private void readSettings() {
        try (BufferedReader br = new BufferedReader(new FileReader("proxy.config"))) {
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
    private void writeSettings(){
        try (PrintWriter pw = new PrintWriter ("proxy.config", "UTF-8")){
            pw.println(serverAddress);
            pw.println(proxyPort);
            pw.println(ID);
            pw.println(PW);
            pw.close();
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(ConnectionUtilities.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
