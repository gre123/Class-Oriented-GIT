/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk.COGElement;

import java.util.List;

/**
 *
 * @author Grzesiek
 */
public interface COGElement {
    
    public void setName(String name);
    public void setSource(String source);
    public void setIsAbstract(boolean isAbstract);
    public void setAccess(String access);
    public void setAuthors(List<String> authors);
    
    public String getName();
    public String getSource();
    public boolean isAbstract();
    public String getAccess();
    public List<String> getAuthors();
}
