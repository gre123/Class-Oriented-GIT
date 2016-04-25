/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.util.List;

public class COGClass implements Comparable<COGClass>{
    private String className;
    private List<COGMethod> classMethods;

    @Override
    public int compareTo(COGClass t) {
        return className.compareTo(t.className);
    }
    
    public class COGMethod
    {
        private String methodName;
        private String methodContent;
    }
    
    public void setName(String name)
    {
        this.className = name;
    }
    public void setMethods(List<COGMethod> methods)
    {
        this.classMethods = methods;
    }
    
    public String getName()
    {
        return className;
    }
}
