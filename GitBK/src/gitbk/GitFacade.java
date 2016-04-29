/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Grzesiek
 */
public class GitFacade {
    public static String directoryPath = "C:/Users/Piotr/Desktop/Konfiguracje";  //TODO do usunięcia!!
    public static File selectedDirectory;
    public static TreeMap<String, Git> repos = new TreeMap<>();

    public static void findAllReposInDirectory() {
        repos.clear();
        for (File file : selectedDirectory.listFiles()) {
            try {
                Git repo = Git.open(file);
                repos.put(getRepoName(repo), repo);
            } catch (IOException ex) {
                System.out.println("Nie jestem repo!");
            }
        }
    }

    public static Set<String> cloneRepo(String url) throws Exception {
        String name = url.substring(url.lastIndexOf("/") + 1);
        CloneCommand clone = Git.cloneRepository().setURI(url).setDirectory(new File(selectedDirectory.getAbsolutePath() + "/" + name));
        Git git = clone.call();
        repos.put(getRepoName(git), git);
        return repos.keySet();
    }

    static String getRepoName(Git git) throws IOException {
        String[] directoryPieces = git.getRepository().getDirectory().toString().split("\\\\");
        String name = directoryPieces[directoryPieces.length - 2];
        return name + " [" + git.getRepository().getBranch() + "]";

    }

    public static void commitRepo() {

    }

    public static void pushRepo() {

    }
 
    public static Map<String,COGClass> getCOGClassesFromCommit(Repository repository, ObjectId commitID) throws Exception
    {
       Map<String,COGClass> allClasses = new TreeMap<>();
       RevWalk revWalk = new RevWalk(repository);  
       RevCommit commit = revWalk.parseCommit(commitID);
       RevTree tree = commit.getTree();
       
       TreeWalk treeWalk = new TreeWalk(repository);
       treeWalk.addTree(tree);
       treeWalk.setRecursive(true);
       treeWalk.setFilter(TreeFilter.ALL);
       
       while(treeWalk.next()){
           String extension = treeWalk.getPathString().substring(treeWalk.getPathLength()-4);
           if(!extension.equals("java")) continue;      
           
           System.out.println(treeWalk.getPathString());
           ObjectId id = treeWalk.getObjectId(0);
           ObjectLoader loader = repository.open(id);
           allClasses.putAll(Source2ClassConverter.convertFromStream(loader.openStream()));   
           
       }
       return allClasses;
    }
}
