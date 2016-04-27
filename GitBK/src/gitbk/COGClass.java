/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.util.List;

public class COGClass implements Comparable<COGClass>{
    
    private String className="";
    private List<COGMethod> classMethods;
    private String classSource="";

    @Override
    public int compareTo(COGClass t) {
        return className.compareTo(t.className);
    }
    
    public class COGMethod
    {
        private String methodName;
        private String methodAccess;
        private String methodSource;
        
        public void setName(String name)
        {
            methodName = name;
        }
        public void setAccess(String access)
        {
            methodAccess = access;
        }
        public void setSource(String source)
        {
            methodSource = source;
        }
        public String getName()
        {
            return methodName;
        }
    }
    
    public void setName(String name)
    {
        this.className = name;
    }
    public void setMethods(List<COGMethod> methods)
    {
        this.classMethods = methods;
    }
    
    public void setSource(String source)
    {
        classSource = source;
    }
    
    public String getSource()
    {
        return classSource;
    }
    
    public String getName()
    {
        return className;
    }
    
    public List<COGMethod> getMethods()
    {
        return classMethods;
    }
}
