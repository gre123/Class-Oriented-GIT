/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Grzesiek
 */
public class MainDocumentController implements Initializable {
    
    
    @FXML
    private ListView reposView;
            
    @FXML
    private void onAddRepoClicked() {
        try {
            createInputTextWindow();
        } catch (IOException ex) {
            Logger.getLogger(MainDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @FXML
    private void onSetPathMenuButtonClicked(){
        try {
            createInputTextWindow();
        } catch (IOException ex) {
            Logger.getLogger(MainDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void createInputTextWindow() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PathSetupWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<PathSetupWindowController>getController().setBehaviour(PathSetupWindowController.WindowBehaviour.REPO_PATH);
        loader.<PathSetupWindowController>getController().setParentController(this);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            setReposListView(GitFacade.getRepoNames());
        } catch (IOException ex) {
            Logger.getLogger(MainDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    public void setReposListView(Set<String> repos)
    {
        ObservableList<String> list = FXCollections.observableArrayList(repos);
        reposView.setItems(list);
    }
}
