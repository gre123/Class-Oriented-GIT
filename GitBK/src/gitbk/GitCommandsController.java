/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.TextInputDialog;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author Grzesiek
 */
public class GitCommandsController {

    private MainWindowController parentController;
    private String commandStatus = "";
    private Optional<String> result;

    private String username = "";
    private String password = "";
    private String pushResult = "";

    public GitCommandsController(MainWindowController parentController) {
        this.parentController = parentController;
    }

    public void pullCommand() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        try {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    commandStatus = GitFacade.pullRepo(parentController.repository);
                    parentController.loadCurrentRepository(new Git(parentController.repository));
                    HighlighterFacade.clearWebView(parentController.sourceCodeView);
                    return commandStatus;
                }

            };
            task.setOnSucceeded(new EventHandler() {
                @Override
                public void handle(Event event) {
                    parentController.loadCurrentRepositoryInGUI();
                    parentController.getScene().setCursor(Cursor.DEFAULT);
                    parentController.showGitResultDialog("GIT PULL:", commandStatus);
                }
            });
            task.setOnFailed(new EventHandler() {

                @Override
                public void handle(Event event) {
                    parentController.getScene().setCursor(Cursor.DEFAULT);
                    parentController.showGitErrorDialog("GIT PULL ERROR", "Nie powiodło się");
                }
            });
            parentController.getScene().setCursor(Cursor.WAIT);
            service.execute(task);

        } catch (Exception ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void commitCommand() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        TextInputDialog dialog = new TextInputDialog("Commit przy pomocy COG");
        dialog.setTitle("Wiadomosc commitu");
        dialog.setHeaderText("Podaj wiadomosc commitu");

        result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Task task = new Task() {
                    @Override
                    protected Object call() throws Exception {
                        GitFacade.commitRepo(parentController.repository, result.get());
                        parentController.loadCurrentRepository(new Git(parentController.repository));
                        HighlighterFacade.clearWebView(parentController.sourceCodeView);
                        return commandStatus;
                    }
                };
                task.setOnSucceeded(new EventHandler() {
                    @Override
                    public void handle(Event event) {
                        parentController.loadCurrentRepositoryInGUI();
                        parentController.getScene().setCursor(Cursor.DEFAULT);
                        parentController.showGitResultDialog("GIT COMMIT:", "Commit command executed successfully");
                    }
                });
                task.setOnFailed(new EventHandler() {

                @Override
                public void handle(Event event) {
                    parentController.getScene().setCursor(Cursor.DEFAULT);
                    parentController.showGitErrorDialog("GIT COMMIT ERROR", "Nie powiodło się");
                }
            });
                parentController.getScene().setCursor(Cursor.WAIT);
                service.execute(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pushCommand(String uname, String pass) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        this.username = uname;
        this.password = pass;

        try {
            Task task = new Task() {
                @Override
                protected Object call() throws Exception {

                    pushResult = GitFacade.pushRepo(parentController.repository, username, password);
                    parentController.loadCurrentRepository(new Git(parentController.repository));
                    return commandStatus;
                }
            };
            task.setOnSucceeded(new EventHandler() {
                @Override
                public void handle(Event event) {
                    parentController.loadCurrentRepositoryInGUI();
                    parentController.getScene().setCursor(Cursor.DEFAULT);
                    parentController.showGitResultDialog("GIT PUSH:", pushResult);
                }
            });
            task.setOnFailed(new EventHandler() {

                @Override
                public void handle(Event event) {
                    parentController.getScene().setCursor(Cursor.DEFAULT);
                    parentController.showGitErrorDialog("GIT PUSH ERROR", "Nie powiodło się");
                }
            });
            parentController.getScene().setCursor(Cursor.WAIT);
            service.execute(task);
        } catch (Exception ex) {
            parentController.showGitResultDialog("GIT PUSH:", ex.getMessage());
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
