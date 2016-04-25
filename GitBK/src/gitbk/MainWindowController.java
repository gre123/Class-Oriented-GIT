package gitbk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    void onSearchClass(ActionEvent event) {

    }

    @FXML
    void onSelectRepository(ActionEvent event) {
//        DirectoryChooser directoryChooser = new DirectoryChooser();
//        File selectedDirectory = directoryChooser.showDialog(((Node) event.getTarget()).getScene().getWindow());

        try {          
            createChooseRepoWindow();

        } catch (IOException e) {
//            selectedRepositoryLabel.setText("Brak repozytorium");
          //  Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Brak repozytorium");
//            alert.setHeaderText("Nie znaleziono repozytorium");
//            alert.setContentText("W katalogu " + selectedDirectory.toString() + " nie znaleziono repozytorium GIT. Wskaż inny katalog.");
//            alert.showAndWait();
            //TODO chyba nie działa poprawnie
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //TODO MainWindowController Initialize
    }
    
    
    private void createChooseRepoWindow() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseRepoWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<COGController>getController().setParentController(this);
        
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void loadCurrentRepository(Git selectedRepo, String reg) throws Exception
    {
        repository = selectedRepo.getRepository();
        String[] directoryPieces = repository.getDirectory().toString().split("\\\\");
        String name = directoryPieces[directoryPieces.length - 2];
        selectedRepositoryLabel.setText(name);
        
        classes = GitFacade.getSourceFromCommit(repository,reg);
        populateTreeView();
    }
    
    private void populateTreeView()
    {
        if(classes != null)
        {
            Collections.sort(classes);
            
            TreeItem allClasses = new TreeItem("Klasy");
            classTreeView.setRoot(allClasses);
            
            for(COGClass cl:classes)
            {
                TreeItem item = new TreeItem(cl.getName());
                allClasses.getChildren().add(item);
            }
        }
    }

}
