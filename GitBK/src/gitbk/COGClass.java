/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import gitbk.COGElement.COGElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class COGClass extends COGElement implements Comparable<COGClass> {

    private String className = "---";
    private Map<String, COGMethod> classMethods;
    private String classSource = "---";

    private String superClass = "---";
    private List<String> implementedInterfaces = new ArrayList<>();
    private boolean isAbstract;
    private String classAccess = "---";

    private List<String> authors = new ArrayList<>();
    private String lastChange;
    private String createDate;

    @Override
    public int compareTo(COGClass t) {
        return className.compareTo(t.className);
    }

    public class COGMethod extends COGElement {
        private String methodName;
        private String methodAccess;
        private String methodSource;
        private String methodReturnType = "---";
        private boolean isAbstract;

        private List<String> authors = new ArrayList<>();
        private String lastChange;
        private String createDate;
        private List<String> changingCommits = new ArrayList<>();

        public void setReturnType(String methodReturnType) {
            this.methodReturnType = methodReturnType;
        }

        @Override
        public void setName(String name) {
            methodName = name;
        }

        @Override
        public void setAccess(String access) {
            methodAccess = access;
        }

        @Override
        public void setSource(String source) {
            methodSource = source;
        }

        @Override
        public String getName() {
            return methodName;
        }

        @Override
        public String getSource() {
            return methodSource;
        }

        @Override
        public void setIsAbstract(boolean isAbstract) {
            this.isAbstract = isAbstract;
        }

        @Override
        public void setAuthors(List<String> authors) {
            this.authors = authors;
        }

        @Override
        public void setLastChange(String lastChange) {
            this.lastChange = lastChange;
        }

        @Override
        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }


        @Override
        public boolean isAbstract() {
            return isAbstract;
        }

        @Override
        public String getAccess() {
            return methodAccess;
        }

        @Override
        public List<String> getAuthors() {
            return authors;
        }

        @Override
        public String getLastChange() {
            return lastChange;
        }

        @Override
        public String getCreateDate() {
            return createDate;
        }

        public String getReturnType() {
            return methodReturnType;
        }


    }

    @Override
    public void setName(String name) {
        this.className = name;
    }

    public void setMethods(Map<String, COGMethod> methods) {
        this.classMethods = methods;
    }

    @Override
    public void setSource(String source) {
        classSource = source;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void setImplementedInterfaces(List<String> interfaces) {
        implementedInterfaces = interfaces;
    }

    @Override
    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public void setLastChange(String lastChange) {
        this.lastChange = lastChange;
    }

    @Override
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


    @Override
    public String getSource() {
        return classSource;
    }

    @Override
    public String getName() {
        return className;
    }

    public Map<String, COGMethod> getMethods() {
        return classMethods;
    }

    public String getSuperClass() {
        return superClass;
    }

    public List<String> getImplementedInterfaces() {
        return implementedInterfaces;
    }

    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    @Override
    public void setIsAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    @Override
    public String getAccess() {
        return classAccess;
    }

    @Override
    public void setAccess(String access) {
        this.classAccess = access;
    }

    @Override
    public List<String> getAuthors() {
        return authors;
    }

    @Override
    public String getLastChange() {
        return lastChange;
    }

    @Override
    public String getCreateDate() {
        return createDate;
    }
}
