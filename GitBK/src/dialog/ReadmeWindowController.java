/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialog;

import window.MainWindowController;
import controller.COGController;
import facade.GitFacade;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.Constants;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */
public class ReadmeWindowController extends COGController {

    /**
     * Initializes the controller class.
     */

    @FXML
    TextArea readmeArea;

    @FXML
    void onCloseClicked() {
        Stage stage = (Stage) readmeArea.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    @Override
    public void setParentController(Initializable parent) {
        super.setParentController(parent); //To change body of generated methods, choose Tools | Templates.
        MainWindowController controller = (MainWindowController) parentController;
        try {
            readmeArea.setText(GitFacade.getReadmeFromCommit(controller.repository, controller.repository.resolve(Constants.HEAD)));
        } catch (Exception ex) {
            Logger.getLogger(ReadmeWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
