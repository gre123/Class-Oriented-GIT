package gitbk;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController implements Initializable {

    private Repository repository;
    private RevWalk revWalk;

    @FXML
    private Label selectedRepositoryLabel;

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
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    

    public void loadCurrentRepository(Git selectedRepo)
    {
        repository = selectedRepo.getRepository();
        String[] directoryPieces = repository.getDirectory().toString().split("\\\\");
        String name = directoryPieces[directoryPieces.length - 2];
        selectedRepositoryLabel.setText(name);
        revWalk = new RevWalk(repository);
        try{
            RevCommit lastCommit = revWalk.next();
            System.out.println(lastCommit.getCommitTime());
        }catch(Exception e){}
    }

}
