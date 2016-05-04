/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import gitbk.COGElement.COGElement;
import java.util.List;
import java.util.Map;

public class COGClass implements Comparable<COGClass>,COGElement{
    
    private String className="";
    private Map<String, COGMethod> classMethods;
    private String classSource="";
    
    private String superClass="";
    private List<String> implementedInterfaces;
    private boolean isAbstract;
    private String classAccess = "";
    
    private List<String> authors;
    private String lastChange;
    private String createDate;
    private List<String> changingCommits;

    @Override
    public int compareTo(COGClass t) {
        return className.compareTo(t.className);
    }
    
    public class COGMethod implements COGElement
    {
        private String methodName;
        private String methodAccess;
        private String methodSource;
        private String methodReturnType;
        private boolean isAbstract;
        
        private List<String> authors;
        private String lastChange;
        private String createDate;
        private List<String> changingCommits;
        
        @Override
        public void setName(String name)
        {
            methodName = name;
        }
        public void setAccess(String access)
        {
            methodAccess = access;
        }
        @Override
        public void setSource(String source)
        {
            methodSource = source;
        }
        @Override
        public String getName()
        {
            return methodName;
        }
        @Override
        public String getSource()
        {
            return methodSource;
        }
    }
    
    @Override
    public void setName(String name)
    {
        this.className = name;
    }
    public void setMethods(Map<String,COGMethod> methods)
    {
        this.classMethods = methods;
    }
    
    @Override
    public void setSource(String source)
    {
        classSource = source;
    }
    
    public void setSuperClass(String superClass)
    {
        this.superClass = superClass;
    }
    public void setImplementedInterfaces(List<String> interfaces)
    {
        implementedInterfaces = interfaces;
    }
    
    @Override
    public String getSource()
    {
        return classSource;
    }
    
    @Override
    public String getName()
    {
        return className;
    }
    
    public Map<String, COGMethod> getMethods()
    {
        return classMethods;
    }
    public String getSuperClass()
    {
        return superClass;
    }
    public List<String> getImplementedInterfaces()
    {
        return implementedInterfaces;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getClassAccess() {
        return classAccess;
    }

    public void setClassAccess(String classAccess) {
        this.classAccess = classAccess;
    }
    
}
