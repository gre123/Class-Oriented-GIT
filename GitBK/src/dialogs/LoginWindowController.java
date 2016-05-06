/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialogs;

import gitbk.MainWindowController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */
public class LoginWindowController implements Initializable {

    @FXML
    TextField usernameField;
    
    @FXML
    TextField passwordField;
    
    private Initializable parentController;
    
    @FXML
    private void loginButtonClicked()
    {
        MainWindowController controller = (MainWindowController) parentController;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
