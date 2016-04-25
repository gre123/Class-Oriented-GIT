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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */
public class ChooseRepoWindowController extends COGController {

    @FXML
    ListView reposListView;
    
    @FXML
    TextField regexField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
            setReposListView(GitFacade.repos.keySet());
 
    } 
    
    @FXML
    private void onAddRepoClicked() {
        try {
            createInputTextWindow(PathSetupWindowController.WindowBehaviour.REPO_PATH);
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void onSelectRepoClicked() throws Exception
    {
        MainWindowController controller = (MainWindowController) parentController;
        String s = (String) reposListView.getSelectionModel().getSelectedItem();
        
        Git repo = GitFacade.repos.get(s);
        controller.loadCurrentRepository(repo, regexField.getText());
        Stage stage = (Stage)reposListView.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void onSetRepoPathClicked()
    {
        try {
            createInputTextWindow(PathSetupWindowController.WindowBehaviour.SET_PATH);
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createInputTextWindow(PathSetupWindowController.WindowBehaviour behaviour) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PathSetupWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<PathSetupWindowController>getController().setBehaviour(behaviour);
        loader.<PathSetupWindowController>getController().setParentController(this);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    public void setReposListView(Set<String> repos)
    {
        ObservableList<String> list = FXCollections.observableArrayList(repos);
        reposListView.setItems(list);
    }    
    
}
