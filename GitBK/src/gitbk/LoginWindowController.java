/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */
public class LoginWindowController extends COGController {

    @FXML
    TextField usernameField;

    @FXML
    TextField passwordField;

    @FXML
    private void loginButtonClicked() {
        MainWindowController controller = (MainWindowController) parentController;
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.close();
        controller.onPushLoginClicked(usernameField.getText(), passwordField.getText());
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void costam()
    {
    }
    

}
