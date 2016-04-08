/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.File;
import java.io.FilenameFilter;
import org.eclipse.jgit.api.Git;

/**
 *
 * @author Grzesiek
 */
public class Source2ClassConverter {
    
    
    static File[] getSourcesFromPath(Git repo)
    {    
        String repoPath = repo.getRepository().getDirectory().toString();
        repoPath = repoPath.substring(0,repoPath.length()-4);
        File dir = new File(repoPath);
        
        return dir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String filename) {
                return filename.endsWith(".java");
            }
            
        });
    }
}
