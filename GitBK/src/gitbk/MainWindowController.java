package gitbk;

import gitbk.COGClass.COGMethod;
import gitbk.COGElement.COGElement;
import gitbk.HighlighterFacade.CodeType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.layout.GridPane;


/**
 * Created by Piotr on 2016-04-22.
 */

public class MainWindowController extends COGController {

    public Map<String, COGClass> classes;
    public Repository repository;
    public String repositoryName = "";
    private COGElement actualShowedElement;
    private String wayToDisplayCommits = "Data";

    private GitCommandsController gitCommand = new GitCommandsController(this);

    private DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @FXML
    private Label selectedRepositoryLabel;

    @FXML
    TreeView classTreeView;

    @FXML
    WebView sourceCodeView;

    @FXML
    WebView commitWebView;

    @FXML
    Pane classDetailsPane;

    @FXML
    Pane methodDetailsPane;

    @FXML
    Pane commitDetailsPane;

    @FXML
    GridPane commitPane;

    @FXML
    ListView<String> commitsListView;

    @FXML
    private Label leftStatusLabel;

    @FXML
    Accordion commitAccordion;

    @FXML
    private Label rightStatusLabel;

    @FXML
    private ListView interfacesListView;

    @FXML
    private Hyperlink readmeLink;

    @FXML
    private TextField searchField;

    @FXML
    private Menu repoMenu;

    @FXML
    private ToggleGroup commitsButtonGroup;

    @FXML
    private ToggleButton showCommitButton;
    @FXML
    void onPullRepository(ActionEvent event) {
        gitCommand.pullCommand();
    }

    @FXML
    void onCommitRepository(ActionEvent event) {
        gitCommand.commitCommand();
    }

    @FXML
    void onPushRepository(ActionEvent event) {
        try {
            showLoginWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @FXML
    void onAboutClicked() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("O programie");
        alert.setHeaderText("Class-Oriented Git");
        alert.setContentText("Program utworzony w ramach projektu z przedmiotu Konfiguracje i Rewizje Oprogramowania. Jest to podgląd repozytoriów, zorientowany na klasy. "
                + "\nAutorzy:\n     inż. Piotr Knop\n     inż. Grzegorz Bylina\n"
                + "\nMenu Repozytorium zostanie udostepnione po wybraniu repozytorium poprzez przycisk.\n"
                + "Prośba o pozytywną ocenę z przedmiotu...");
        alert.show();
    }

    @FXML
    void onReadmeClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReadmeWindow.fxml"));
            Parent root = (Parent) loader.load();
            loader.<COGController>getController().setParentController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOpacity(1);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void onShowCommitClicked() throws Exception{
        if(showCommitButton.isSelected())
        {
            int index = commitsListView.getSelectionModel().getSelectedIndex();
            String additionalInfo = actualShowedElement.getCommitByIndex(index);
            String result = HighlighterFacade.expandSourceCode(actualShowedElement, actualShowedElement.getSource(), additionalInfo);
            new HighlighterFacade(CodeType.DIFF).displayHighlightedCode(result, commitWebView);
            commitPane.setVisible(true);
        }
        else commitPane.setVisible(false);

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        commitsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if (actualShowedElement != null) {
                    int index = commitsListView.getSelectionModel().getSelectedIndex();
                    String cid = commitsListView.getSelectionModel().getSelectedItem();
//                    String additionalInfo = actualShowedElement.getCommitByIndex(index);
                    try {
                        showCommitButton.setText(cid);
                        showCommitButton.setVisible(true);
                        if(showCommitButton.isSelected())
                        {
                            onShowCommitClicked();
                        }
                        
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }


        });

        searchField.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                populateTreeView(newValue);
            }

        });

        commitsButtonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                wayToDisplayCommits = ((RadioButton) commitsButtonGroup.getSelectedToggle()).getText();
                try {
                    setCommitsListView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
        repositoryName = directoryPieces[directoryPieces.length - 2];
        GitFacade.git = new Git(repository);
        GitFacade.findAllCommits();
        classes = GitFacade.getCOGClassesFromCommit(repository, repository.resolve(Constants.HEAD));
        GitFacade.checkAllCommitsDiff();
    }

    public void loadCurrentRepositoryInGUI() {
        selectedRepositoryLabel.setText(repositoryName);
        readmeLink.setText("README - " + repositoryName);
        readmeLink.setVisible(true);
        rightStatusLabel.setText("Ilość commitów: " + GitFacade.commitList.size());
        leftStatusLabel.setText("Ilość klas: " + classes.size());
        rightStatusLabel.setVisible(true);
        leftStatusLabel.setVisible(true);
        populateTreeView("");
        repoMenu.setDisable(false);
    }

    private void populateTreeView(String filter) {
        if (classes != null) {
            TreeItem allClasses = new TreeItem("Klasy");
            classTreeView.setRoot(allClasses);

            for (COGClass cl : classes.values()) {
                if (!filter.isEmpty() && !cl.getName().toString().contains(filter)) continue;
                TreeItem item = new TreeItem(cl.getName());
                for (COGClass.COGMethod method : cl.getMethods().values()) {
                    TreeItem methodItem = new TreeItem(method.getName());
                    item.getChildren().add(methodItem);
                }

                allClasses.getChildren().add(item);
            }

            classTreeView.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<TreeItem>() {

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
                    }
            );
            classTreeView.setShowRoot(false);
        }
    }

    void loadCurrentElement(COGElement currentElement) {
        //Ustawianie szczegółów

        actualShowedElement = currentElement;

        //Ustawianie kodu źródłowego
        new HighlighterFacade(CodeType.JAVA).displayHighlightedCode(currentElement.getSource(), sourceCodeView);

        try {
            initializeDetailsPane(currentElement);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void initializeDetailsPane(COGElement currentElement) throws IOException {
        if (currentElement instanceof COGClass) {

            COGClass currentClass = (COGClass) currentElement;

            Label nameView = (Label) classDetailsPane.getChildren().get(0);
            Label baseClassNameView = (Label) classDetailsPane.getChildren().get(1);
            Label accessView = (Label) classDetailsPane.getChildren().get(3);
            Label abstractView = (Label) classDetailsPane.getChildren().get(4);

            nameView.setText(currentElement.getName());
            baseClassNameView.setText(currentClass.getSuperClass());
            ObservableList interfaces = FXCollections.observableArrayList(currentClass.getImplementedInterfaces());
            interfacesListView.setItems(interfaces);
            accessView.setText(currentClass.getAccess());
            String isAbstract = currentClass.isAbstract() ? "Tak" : "Nie";
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
            String isAbstract = currentElement.isAbstract() ? "Tak" : "Nie";
            abstractView.setText(isAbstract);

            classDetailsPane.setVisible(false);
            methodDetailsPane.setVisible(true);
            commitDetailsPane.setVisible(true);
        }

        //Zakładka rozszerzona

        RevWalk walk = new RevWalk(repository);
        RevCommit lastCommit = walk.parseCommit(ObjectId.fromString(currentElement.getLastCommitId()));
        RevCommit oldestCommit = walk.parseCommit(ObjectId.fromString(currentElement.getOldestCommitId()));

        Label createDateLabel = (Label) commitDetailsPane.getChildren().get(0);
        Label createByLabel = (Label) commitDetailsPane.getChildren().get(1);
        Label lastModifyDateLabel = (Label) commitDetailsPane.getChildren().get(2);
        Label lastModifyByLabel = (Label) commitDetailsPane.getChildren().get(3);

        createDateLabel.setText(df.format(oldestCommit.getAuthorIdent().getWhen()));
        createByLabel.setText(oldestCommit.getAuthorIdent().getName());
        lastModifyDateLabel.setText(df.format(lastCommit.getAuthorIdent().getWhen()));
        lastModifyByLabel.setText(lastCommit.getAuthorIdent().getName());

        setCommitsListView();
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

    public void showGitResultDialog(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Git Command");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.show();
    }

    public void showLoginWindow() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
        Parent root = (Parent) loader.load();
        loader.<COGController>getController().setParentController(this);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOpacity(1);
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void onPushLoginClicked(String username, String password) {
        gitCommand.pushCommand(username, password);
    }

    public Scene getScene() {
        return leftStatusLabel.getScene();
    }

    private void setCommitsListView() throws IOException {
        RevWalk walk = new RevWalk(repository);
        RevCommit commitWithDiff;
        List<String> commitsDateList = new LinkedList<>();
        for (String commitId : actualShowedElement.getCommitIdSet()) {
            commitWithDiff = walk.parseCommit(ObjectId.fromString(commitId));
            switch (wayToDisplayCommits) {
                case "Data":
                    commitsDateList.add(df.format(commitWithDiff.getAuthorIdent().getWhen()));
                    break;

                case "Numer":
                    commitsDateList.add(commitWithDiff.getName());
                    break;

                case "Opis":
                    commitsDateList.add(commitWithDiff.getShortMessage());
                    break;
            }
        }
        commitsListView.setItems(FXCollections.observableArrayList(commitsDateList));
    }
}
