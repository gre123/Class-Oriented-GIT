/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Grzesiek
 */

public class PathSetupWindowController implements Initializable {

    public enum WindowBehaviour {
        SET_PATH, REPO_PATH
    }
     
    private WindowBehaviour behaviour;
    private Initializable parentController;
            
    @FXML
    private TextField pathInputText;
    
    @FXML
    private void onOKButtonClicked()
    {
        try {
            switch(behaviour)
            {
                case REPO_PATH:
                {
                    Set<String> repoNames = GitFacade.cloneRepo(pathInputText.getText());
                    ChooseRepoWindowController controller = (ChooseRepoWindowController)parentController;
                    controller.setReposListView(repoNames);
                    
                    break;
                }
                case SET_PATH:
                {
                    GitFacade.filePath = pathInputText.toString();
                }
            }
        } catch (Exception ex) {
                Logger.getLogger(PathSetupWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        Stage stage = (Stage)pathInputText.getScene().getWindow();
        stage.close();
    }
    
   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
 
    }    
    public void setBehaviour(WindowBehaviour behaviour)
    {
        this.behaviour = behaviour;
        if(behaviour.equals(WindowBehaviour.SET_PATH))
        {
            pathInputText.setText(GitFacade.filePath);
        }  
    }
    public void setParentController(Initializable controller)
    {
        this.parentController = controller;
    }
    
}
