/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */

public class PathSetupWindowController implements Initializable {
    private Initializable parentController;

    @FXML
    private TextField pathInputText;

    @FXML
    private void onOKButtonClicked() throws Exception {
        Set<String> repoNames = GitFacade.cloneRepo(pathInputText.getText());
        ChooseRepoWindowController controller = (ChooseRepoWindowController) parentController;
        controller.setReposListView(repoNames);
        Stage stage = (Stage) pathInputText.getScene().getWindow();
        stage.close();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    public void setParentController(Initializable controller) {
        this.parentController = controller;
    }

}
