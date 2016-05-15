/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Grzesiek
 */
public class GitBK extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        //GitFacade.selectedDirectory = new File("C:/Users/Grzesiek/Moje rzeczy/rewizje/repos");
        //GitFacade.findAllReposInDirectory();

        Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("COK : Class-Oriented Git");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
