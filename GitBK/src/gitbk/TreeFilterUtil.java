/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.scene.control.TreeItem;

/**
 *
 * @author Grzesiek
 */
public class TreeFilterUtil {
    
    
    
    static void filterTreeView(MainWindowController controller, String filter)
    {
        for(int i=0;i<controller.classTreeView.getRoot().getChildren().size();i++)
        {
            TreeItem item = (TreeItem) controller.classTreeView.getRoot().getChildren().get(i);     
            if(!item.getValue().toString().contains(filter))
                controller.classTreeView.getRoot().getChildren().remove(i);
            
        }
        
    }
}
