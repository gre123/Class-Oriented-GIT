/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.util.ArrayList;

/**
 *
 * @author Grzesiek
 */
public class COGClassList extends ArrayList<COGClass>{
    
    public COGClass get(String className)
    {
        for(int i=0;i<size();i++)
        {
            if(get(i).getName().equals(className)) return get(i);
        }
        return null;
    }
}
