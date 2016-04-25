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
import java.util.TreeMap;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 *
 * @author Grzesiek
 */
public class GitFacade {
    public static String filePath = "C:/Users/Grzesiek/Moje rzeczy/rewizje/repos";
    public static TreeMap<String, Git> repos = new TreeMap<>();
    
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
                        repos.put(getRepoName(repo),repo);
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
        repos.put(getRepoName(git),git);
        return repos.keySet();
    }
    
    static String getRepoName(Git git) throws IOException
    {
        String[] directoryPieces = git.getRepository().getDirectory().toString().split("\\\\");
        String name = directoryPieces[directoryPieces.length-2];
        return name+" ["+git.getRepository().getBranch()+"]";

    }
    public static void commitRepo()
    {
        
    }
    public static void pushRepo()
    {
        
    }
 
    public static List<COGClass> getSourceFromCommit(Repository repo, String reg) throws Exception
    {
       List<COGClass> allClasses = new ArrayList<>();
       String regex = reg;
       RevWalk revWalk = new RevWalk(repo);  
       ObjectId lastcommitID = repo.resolve(Constants.HEAD);
       
       RevCommit commit = revWalk.parseCommit(lastcommitID);
       RevTree tree = commit.getTree();
       
       System.out.println("Commit tree "+tree);
       
       TreeWalk treeWalk = new TreeWalk(repo);
       treeWalk.addTree(tree);
       treeWalk.setRecursive(true);
       treeWalk.setFilter(TreeFilter.ALL);
       
       while(treeWalk.next()){
           String extension = treeWalk.getPathString().substring(treeWalk.getPathLength()-4);
           if(!extension.equals("java")) continue;      
           
           System.out.println(treeWalk.getPathString());
           ObjectId id = treeWalk.getObjectId(0);
           ObjectLoader loader = repo.open(id);
           
           allClasses.addAll(Source2ClassConverter.convert(loader.openStream()));   
           
       }
       return allClasses;
    }
}
