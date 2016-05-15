/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.eclipse.jgit.lib.ObjectStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Grzesiek
 */
public class Source2ClassConverter {

    static Map<String, COGClass> classes = new TreeMap<>();

    static Map<String, COGClass> convertFromStream(ObjectStream inputStream) {
        CompilationUnit cu = null;
        classes.clear();

        try {
            cu = JavaParser.parse(inputStream);
            for (TypeDeclaration type : cu.getTypes()) {
                if (type instanceof ClassOrInterfaceDeclaration) generateCOGClasses(type);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static void generateCOGClasses(TypeDeclaration type) {
        Map<String, COGClass.COGMethod> currentClassMethods = new TreeMap<>();
        COGClass currentClass = COGClassFactory.newInstance();
        ClassOrInterfaceDeclaration classDeclaration = (ClassOrInterfaceDeclaration) type;

        if (!classDeclaration.getExtends().isEmpty())
            currentClass.setSuperClass(classDeclaration.getExtends().get(0).toString());
        currentClass.setImplementedInterfaces(convertImplementedInterfaces(classDeclaration));
        currentClass.setName(type.getName());
        currentClass.setSource((String) type.toStringWithoutComments());
        currentClass.setAccess(ModifierSet.getAccessSpecifier(type.getModifiers()).toString());
        currentClass.setIsAbstract(ModifierSet.isAbstract(type.getModifiers()));
        currentClass.setBeginLine(type.getBeginLine());
        currentClass.setEndLine(type.getEndLine());

        List<BodyDeclaration> declarations = type.getMembers();
        for (BodyDeclaration declaration : declarations) {
            if (declaration instanceof ClassOrInterfaceDeclaration) {
                generateCOGClasses((TypeDeclaration) declaration);
            }
            if (declaration instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) declaration;
                COGClass.COGMethod currentClassMethod = currentClass.new COGMethod();

                currentClassMethod.setName(method.getName());
                currentClassMethod.setSource(method.toStringWithoutComments());
                currentClassMethod.setAccess(ModifierSet.getAccessSpecifier(method.getModifiers()).toString());
                currentClassMethod.setIsAbstract(ModifierSet.isAbstract(method.getModifiers()));
                currentClassMethod.setReturnType(method.getType().toString());


                currentClassMethod.setBeginLine(method.getBeginLine());
                currentClassMethod.setEndLine(method.getEndLine());

                currentClassMethods.put(currentClassMethod.getName(), currentClassMethod);

            }
        }
        currentClass.setMethods(currentClassMethods);
        classes.put(currentClass.getName(), currentClass);
    }

    static List<String> convertImplementedInterfaces(ClassOrInterfaceDeclaration classDeclaration) {
        List<String> interfaces = new ArrayList<>();
        for (ClassOrInterfaceType i : classDeclaration.getImplements()) {
            interfaces.add(i.getName());
        }
        return interfaces;
    }

    static List<CommitChange> convertCommitToSingleCommitChange(String commitSource) {
        int i = 1;
        ArrayList<CommitChange> commitChangeList = new ArrayList<>();
        Pattern pattern = Pattern.compile("@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@");
        Matcher matcher = pattern.matcher(commitSource);

        String[] commitChanges = commitSource.split("(@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@)");
        while (matcher.find()) {
            int start = Integer.valueOf(matcher.group(3));
            int end = Integer.valueOf(matcher.group(3)) + Integer.valueOf(matcher.group(4)) - 1;

            commitChangeList.add(new CommitChange(start, end, commitChanges[i]));
            i++;
        }
        return commitChangeList;
    }
}
