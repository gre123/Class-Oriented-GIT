/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author Grzesiek
 */
public class GitFacade {
    public static String filePath = "C:/Users/Grzesiek/Moje rzeczy/rewizje/repos";
    public static List<Git> repos = new ArrayList<Git>();
    
    public static void findAllReposInDirectory()
    {
        File rootDir = new File(filePath);
        for(File file:rootDir.listFiles())
        {
            if(file.isDirectory()) {
                for(File dirFile:file.listFiles())
                {
                   try {  
                        Git repo = Git.open(file);
                        repos.add(repo);
                   } catch (IOException ex) {
                        System.out.println("Nie jestem repo!");
                   }
                }
            }
            
        }
    }
            
 
    public static Set<String> cloneRepo(String url) throws Exception
    {
        String name = url.substring(url.lastIndexOf("/")+1);
        CloneCommand clone  = Git.cloneRepository().setURI(url).setDirectory(new File(filePath+"/"+name));
        Git git = clone.call();
        repos.add(git);
        return getRepoNames();
    }
    static Set<String> getRepoNames() throws IOException
    {
        TreeSet<String> repoNames = new TreeSet<String>();
        for(Git repo: repos)
        {
            String[] directoryPieces = repo.getRepository().getDirectory().toString().split("\\\\");
            String name = directoryPieces[directoryPieces.length-2];
            repoNames.add(name+" ["+repo.getRepository().getBranch()+"]");
        }
        return repoNames;
    }
    public static void commitRepo()
    {
        
    }
    public static void pushRepo()
    {
        
    }
}
