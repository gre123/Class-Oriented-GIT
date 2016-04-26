/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.fxml.Initializable;

/**
 *
 * @author Grzesiek
 */
public abstract class COGController implements Initializable{
    protected Initializable parentController;

    public void setParentController(Initializable parent)
    {
        parentController = parent;
    }
}
