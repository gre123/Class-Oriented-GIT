/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectStream;

/**
 *
 * @author Grzesiek
 */
public class Source2ClassConverter {
    
    static List<COGClass> classes = new ArrayList<>();
    
    static List<COGClass> convertFromStream(ObjectStream inputStream)
    {
        CompilationUnit cu = null;
        classes.clear();
        
        try{
            cu = JavaParser.parse(inputStream);
            for(TypeDeclaration type:cu.getTypes())
            {
                generateCOGClasses(type);
            }
        }catch(ParseException e)
        {
            e.printStackTrace();
        }
        
        return classes;
    }
    
    private static void generateCOGClasses(TypeDeclaration type)
    {
        List<COGClass.COGMethod> currentClassMethods = new ArrayList<>();
        COGClass currentClass = COGClassFactory.newInstance();
        currentClass.setName(type.getName()); 
        currentClass.setSource((String) type.toStringWithoutComments());
                
        List<BodyDeclaration> declarations = type.getMembers();
        for(BodyDeclaration declaration:declarations)
        {
            if(declaration instanceof TypeDeclaration)
            {
                generateCOGClasses((TypeDeclaration) declaration);
            }
            if(declaration instanceof MethodDeclaration)
            {
                MethodDeclaration method = (MethodDeclaration) declaration;
                COGClass.COGMethod currentClassMethod = currentClass.new COGMethod();
                        
                currentClassMethod.setName(method.getDeclarationAsString());
                currentClassMethod.setSource(method.getBody().toString());
                        
                currentClassMethods.add(currentClassMethod);
                        
            }
        }        
        currentClass.setMethods(currentClassMethods);
        classes.add(currentClass);
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
