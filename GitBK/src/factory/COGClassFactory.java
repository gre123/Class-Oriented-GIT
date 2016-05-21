/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package factory;

import model.COGClass;

public class COGClassFactory {

    public static COGClass newInstance() {
        return new COGClass();
    }
}
