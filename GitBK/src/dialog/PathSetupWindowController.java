/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dialog;

import window.ChooseRepoWindowController;
import facade.GitFacade;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    void onOKButtonClicked() throws Exception {
        if (GitFacade.validateUrl(pathInputText.getText())) {
            Set<String> repoNames = GitFacade.cloneRepo(pathInputText.getText());
            ChooseRepoWindowController controller = (ChooseRepoWindowController) parentController;
            controller.setReposListView(repoNames);
            Stage stage = (Stage) pathInputText.getScene().getWindow();
            stage.close();
        } else {
            showErrorDialog();
        }
    }

    private void showErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd!");
        alert.setHeaderText("Brak repozytorium");
        alert.setContentText("Pod wskazanym adresem URL nie znajduje się żadne repozytorium do sklonowania");
        alert.showAndWait();
    }

    @FXML
    void onCancelButtonClicked() {
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
