/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */
public class ChooseRepoWindowController extends COGController {

    @FXML
    ListView reposListView;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setReposListView(GitFacade.repos.keySet());

    }

    @FXML
    private void onAddRepoClicked() throws IOException {
        createInputTextWindow();
    }

    @FXML
    private void onSelectRepoClicked() throws Exception {
        ExecutorService service = Executors.newSingleThreadExecutor();

        if (!reposListView.getSelectionModel().isEmpty()) {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    String s = (String) reposListView.getSelectionModel().getSelectedItem();
                    Git repo = GitFacade.repos.get(s);
                    MainWindowController controller = (MainWindowController) parentController;
                    controller.loadCurrentRepository(repo);
                    return null;
                }

            };
            task.setOnSucceeded(new EventHandler() {

                @Override
                public void handle(Event event) {
                    MainWindowController controller = (MainWindowController) parentController;
                    Stage stage = (Stage) reposListView.getScene().getWindow();
                    reposListView.getScene().setCursor(Cursor.DEFAULT);
                    controller.loadCurrentRepositoryInGUI();
                    stage.close();
                }
            });
            reposListView.getScene().setCursor(Cursor.WAIT);
            service.execute(task);
        }
    }

    @FXML
    private void onSetRepoPathClicked(ActionEvent event) {
        File selectedDirectory = showChangeDirectoryDialog(event);
        if (selectedDirectory != null) {
            GitFacade.selectedDirectory = selectedDirectory;
            GitFacade.findAllReposInDirectory();
            setReposListView(GitFacade.repos.keySet());
        }
    }

    private void createInputTextWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PathSetupWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<PathSetupWindowController>getController().setParentController(this);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setReposListView(Set<String> repos) {
        ObservableList<String> list = FXCollections.observableArrayList(repos);
        reposListView.setItems(list);
    }

    public File showChangeDirectoryDialog(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        return directoryChooser.showDialog(((Node) event.getTarget()).getScene().getWindow());

    }
}
