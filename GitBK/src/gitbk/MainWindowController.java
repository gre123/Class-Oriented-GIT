package gitbk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController extends COGController {

    private List<COGClass> classes;
    private Repository repository;
    private RevWalk revWalk;

    @FXML
    private Label selectedRepositoryLabel;

    @FXML
    TreeView classTreeView;

    @FXML
    TextArea sourceTextArea;

    @FXML
    void onSearchClass(ActionEvent event) {

    }

    @FXML
    void onSelectRepository(ActionEvent event) {
        //Wymusza pokazywanie okna wyboru folderu dopóki nie zostanie on wybrany.
        while (GitFacade.selectedDirectory == null) {
            showChooseInitialDirectoryDialog(event);
        }

        //Otwarcie okna wyboru repozytorium
        try {
            createChooseRepoWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO MainWindowController Initialize
    }


    private void createChooseRepoWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseRepoWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<COGController>getController().setParentController(this);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadCurrentRepository(Git selectedRepo, String reg) throws Exception {
        repository = selectedRepo.getRepository();
        String[] directoryPieces = repository.getDirectory().toString().split("\\\\");
        String name = directoryPieces[directoryPieces.length - 2];
        selectedRepositoryLabel.setText(name);

        classes = GitFacade.getCOGClassesFromCommit(repository, repository.resolve(Constants.HEAD));
        populateTreeView();
    }

    private void populateTreeView() {
        if (classes != null) {
//            Collections.sort(classes);

            TreeItem allClasses = new TreeItem("Klasy");
            classTreeView.setRoot(allClasses);

            for (COGClass cl : classes) {
                TreeItem item = new TreeItem(cl.getName());

                for (COGClass.COGMethod method : cl.getMethods()) {
                    TreeItem methodItem = new TreeItem(method.getName());
                    item.getChildren().add(methodItem);
                }

                allClasses.getChildren().add(item);
            }
        }
    }

    public void showChooseInitialDirectoryDialog(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        GitFacade.selectedDirectory = directoryChooser.showDialog(((Node) event.getTarget()).getScene().getWindow());
        if (GitFacade.selectedDirectory == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Brak katalogu!");
            alert.setHeaderText("Nie wybrano katalogu");
            alert.setContentText("Aby wybrać jedno z dostępnych repozytoriów najpierw trzeba wybrać katalog w którym się one znajdują.");
            alert.showAndWait();
        } else {
            GitFacade.findAllReposInDirectory();
        }
    }
}
