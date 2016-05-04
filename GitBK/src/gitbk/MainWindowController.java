package gitbk;

import gitbk.COGClass.COGMethod;
import gitbk.COGElement.COGElement;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController extends COGController {

    private Map<String, COGClass> classes;
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
    Pane methodDetailsPane;

    @FXML
    ListView<String> commitsListView;

    @FXML
    private Label leftStatusLabel;

    @FXML
    private Label rightStatusLabel;

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
        GitFacade.git = new Git(repository);
        GitFacade.findAllCommits();
        rightStatusLabel.setText("Ilość commitów: " + GitFacade.commitList.size());

        classes = GitFacade.getCOGClassesFromCommit(repository, repository.resolve(Constants.HEAD));
        leftStatusLabel.setText("Ilość klas: " + classes.size());
        populateTreeView();
    }

    void loadCurrentElement(COGElement currentElement) {
        classDetailsPane.setVisible(true);

        //Ustawianie szczegółów
        initializeDetailsPane(currentElement);
            
        //Ustawianie kodu źródłowego
        new HighlighterFacade().displayHighlightedCode(currentElement.getSource(), sourceCodeView);
    }

    private void initializeDetailsPane(COGElement currentElement)
    {
        
        Label nameView = (Label) classDetailsPane.getChildren().get(0);
        Label baseClassNameView = (Label) classDetailsPane.getChildren().get(1);
        if (currentElement instanceof COGClass) {
            COGClass currentClass = (COGClass) currentElement;

            nameView.setText(currentElement.getName());
            baseClassNameView.setText(currentClass.getSuperClass());
            ((Label) classDetailsPane.getChildren().get(8)).setText("Nazwa klasy:");
        } else {
            nameView.setText(currentElement.getName());
            baseClassNameView.setText("");
            ((Label) classDetailsPane.getChildren().get(8)).setText("Nazwa metody:");
        }

        RevWalk walk = new RevWalk(repository);
        RevCommit commit = null;
        try {
            ObjectId from = repository.resolve(repository.getFullBranch());
            commit = walk.parseCommit(from);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label authorLabel = (Label) classDetailsPane.getChildren().get(5);
        authorLabel.setText(commit.getAuthorIdent().getName());

        Label lastModifyDateLabel = (Label) classDetailsPane.getChildren().get(6);
        lastModifyDateLabel.setText("Data ostatniej modyfikacji: " + commit.getAuthorIdent().getWhen());
        //Ustawianie kodu źródłowego
        new HighlighterFacade().displayHighlightedCode(currentElement.getSource(), sourceCodeView);
    }

    private void populateTreeView() {
        if (classes != null) {
//            Collections.sort(classes);

            TreeItem allClasses = new TreeItem("Klasy");
            classTreeView.setRoot(allClasses);

            for (COGClass cl : classes.values()) {
                TreeItem item = new TreeItem(cl.getName());

                for (COGClass.COGMethod method : cl.getMethods().values()) {
                    TreeItem methodItem = new TreeItem(method.getName());
                    item.getChildren().add(methodItem);
                }

                allClasses.getChildren().add(item);
            }

            classTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

                @Override
                public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
                    if (newValue.getParent().equals(classTreeView.getRoot())) {
                        loadCurrentElement(classes.get((String) newValue.getValue()));
                    } else {
                        COGClass currentClass = classes.get((String) newValue.getParent().getValue());
                        COGMethod currentMethod = currentClass.getMethods().get((String) newValue.getValue());
                        loadCurrentElement(currentMethod);
                    }
                }


            });
            classTreeView.setShowRoot(false);
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
