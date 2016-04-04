/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.File;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author Grzesiek
 */
public class GitFacade {
    public static String FILE_PATH = "C:/Users/Grzesiek/Moje rzeczy/rewizje/repos";
    
    public static void cloneRepo(String url) throws Exception
    {
        Git git  = Git.cloneRepository().setURI(url).setDirectory(new File(FILE_PATH)).call();
        
    }
    public static void commitRepo()
    {
        
    }
    public static void pushRepo()
    {
        
    }
}
