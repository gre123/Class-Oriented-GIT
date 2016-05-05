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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController extends COGController {

    private Map<String, COGClass> classes;
    private Repository repository;
    private RevWalk revWalk;

    DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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
    Pane commitDetailsPane;
    
    @FXML
    ListView<String> commitsListView;

    @FXML
    private Label leftStatusLabel;

    @FXML
    private Label rightStatusLabel;
    
    @FXML
    private ListView interfacesListView;

    @FXML
    private HBox gitButtonsBox;
    
    @FXML
    void onPullRepository(ActionEvent event)
    {
        try {
            String status = GitFacade.pullRepo(repository);
            loadCurrentRepository(new Git(repository));
            showGitResultDialog("GIT PULL:", status);

        } catch (Exception ex) {
            showGitResultDialog("GIT PULL:", ex.getMessage());
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void onCommitRepository(ActionEvent event)
    {
        
        TextInputDialog dialog = new TextInputDialog("Commit przy pomocy COG");
        dialog.setTitle("Wiadomosc commitu");
        dialog.setHeaderText("Podaj wiadomosc commitu");
        
        Optional<String> result = dialog.showAndWait();
         if(result.isPresent()){
             try{
                GitFacade.commitRepo(repository, result.get());
                showGitResultDialog("GIT COMMIT:", "Commit command executed successfully");
             }catch(Exception e){
                 showGitResultDialog("GIT COMMIT", e.getMessage());
                 e.printStackTrace();
                 
             }
         }
    }
    
    @FXML void onPushRepository(ActionEvent event)
    {
        try {
            GitFacade.pushRepo(repository);
            showGitResultDialog("GIT PULL:", "Push command executed successfully");
        } catch (GitAPIException ex) {
            showGitResultDialog("GIT PUSH:", ex.getMessage());
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
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

    public void loadCurrentRepository(Git selectedRepo) throws Exception {
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
        
        gitButtonsBox.setVisible(true);
    }

    void loadCurrentElement(COGElement currentElement) {
        //Ustawianie szczegółów
        
        initializeDetailsPane(currentElement);

        //Ustawianie kodu źródłowego
        new HighlighterFacade().displayHighlightedCode(currentElement.getSource(), sourceCodeView);
    }

    private void initializeDetailsPane(COGElement currentElement)
    {        
        if (currentElement instanceof COGClass) {
            
            COGClass currentClass = (COGClass) currentElement;
            
            Label nameView = (Label) classDetailsPane.getChildren().get(0);
            Label baseClassNameView = (Label) classDetailsPane.getChildren().get(1);
            ListView implementedInterfacesView = (ListView) classDetailsPane.getChildren().get(2);
            Label accessView = (Label) classDetailsPane.getChildren().get(3);
            Label abstractView = (Label) classDetailsPane.getChildren().get(4);

            nameView.setText(currentElement.getName());
            baseClassNameView.setText(currentClass.getSuperClass());
            ObservableList interfaces = FXCollections.observableArrayList(currentClass.getImplementedInterfaces());
            implementedInterfacesView.setItems(interfaces);
            accessView.setText(currentClass.getAccess());
            String isAbstract = currentClass.isAbstract()?"Tak":"Nie";
            abstractView.setText(isAbstract);
            
            methodDetailsPane.setVisible(false);
            classDetailsPane.setVisible(true);
            commitDetailsPane.setVisible(true);
        } else {
            
            COGClass.COGMethod currentMethod = (COGClass.COGMethod) currentElement;
                    
            Label nameView = (Label) methodDetailsPane.getChildren().get(0);
            Label returnTypeView = (Label) methodDetailsPane.getChildren().get(1);
            Label accessView = (Label) methodDetailsPane.getChildren().get(2);
            Label abstractView = (Label) methodDetailsPane.getChildren().get(3);
            
            nameView.setText(currentElement.getName());
            returnTypeView.setText(currentMethod.getReturnType());
            accessView.setText(currentElement.getAccess());
            String isAbstract = currentElement.isAbstract()?"Tak":"Nie";
            abstractView.setText(isAbstract);
            
            classDetailsPane.setVisible(false);
            methodDetailsPane.setVisible(true);
            commitDetailsPane.setVisible(true);
        }

        //Zakładka rozszerzona
        
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = null;
        try {
            ObjectId from = repository.resolve(repository.getFullBranch());
            commit = walk.parseCommit(from);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label createDateLabel = (Label) commitDetailsPane.getChildren().get(0);
        Label authorLabel = (Label) commitDetailsPane.getChildren().get(1);
        Label lastModifyDateLabel = (Label) commitDetailsPane.getChildren().get(2);
        ListView changingCommitsView = (ListView) commitDetailsPane.getChildren().get(3);  
        
        lastModifyDateLabel.setText("" + commit.getAuthorIdent().getWhen());
        authorLabel.setText(commit.getAuthorIdent().getName());

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
    private void showGitResultDialog(String header,String content)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Git Pull");
        alert.setHeaderText("Pull zakończony");
        alert.setContentText(content);
        alert.show();
    }
}
