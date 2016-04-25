/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectStream;

/**
 *
 * @author Grzesiek
 */
public class Source2ClassConverter {
    
    
    public static List<COGClass> convert(ObjectStream inputStream)
    {
        List<COGClass> classes = new ArrayList<>();
        String regex = "(class (([a-zA-z])+))";
        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\n");
        while(scanner.hasNext())
        {
            Matcher m = Pattern.compile(regex).matcher(scanner.next());
            if(m.find()){
                COGClass c = COGClassFactory.newInstance();
                c.setName(m.group(2));
                classes.add(c);
                
//                System.out.println(m.group(2));
            }
        }
        return classes;
    }
    
    
    
    
    
    
    
    
    
    
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
