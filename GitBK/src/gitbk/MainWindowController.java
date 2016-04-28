package gitbk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.lib.Constants;

/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController extends COGController {

    private COGClassList classes;
    private Repository repository;
    private RevWalk revWalk;

    @FXML
    private Label selectedRepositoryLabel;

    @FXML
    TreeView classTreeView;

    @FXML
    TextArea sourceTextArea;

    @FXML
    WebView sourceCodeView;

    @FXML
    Pane classDetailsPane;

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
//        sourceTextArea.setEditable(false);
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
    
    void loadCurrentClass(COGClass currentClass)
    {
        classDetailsPane.setVisible(true);

        //Ustawianie szczegółów
        Label classNameView = (Label) classDetailsPane.getChildren().get(0);
        classNameView.setText("Nazwa klasy: "+currentClass.getName());

        //Ustawianie kodu źródłowego
        new HighlighterFacade().displayHighlightedCode(currentClass.getSource(), sourceCodeView);
    }

    private void populateTreeView()
    {
        if(classes != null)
        {
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

            classTreeView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {

                @Override
                public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                    int index = (int) newValue-1;
                    TreeItem item = (TreeItem) classTreeView.getRoot().getChildren().get(index);
                    loadCurrentClass(classes.get((String) item.getValue()));
                }


            });
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
