/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import java.io.BufferedReader;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.filter.PathFilter;

/**
 * @author Grzesiek
 */
public class GitFacade {
    public static File selectedDirectory;
    public static Git git; //TODO przejrzenie kodu
    public static LinkedList<String> commitList = new LinkedList<>();
    public static Map<String, List<COGClass>> classesInFileMap = new HashMap<>();
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

    public static void commitRepo(Repository repository, String message) throws GitAPIException {
        Git git = new Git(repository);
        git.add().addFilepattern(".").call();
        git.commit().setAll(true).setMessage(message).call();
        
    }

    public static String pushRepo(Repository repository, String username, String password) throws GitAPIException {
        Git git = new Git(repository);
        CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, password);
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(provider).setForce(true).setPushAll();
        
        Iterable<PushResult> result = pushCommand.call();
        
        if(!result.iterator().next().getMessages().isEmpty()) 
            return result.iterator().next().getMessages();
        else return "OK";

    }

    public static String pullRepo(Repository repository) throws GitAPIException {
        Git git = new Git(repository);
        PullResult result = null;
        org.eclipse.jgit.api.PullCommand pullComand = git.pull();
        result = pullComand.call();
        MergeResult mergeResult = result.getMergeResult();

        return "Merge Status: " + mergeResult.getMergeStatus().toString();

    }

    public static Map<String, COGClass> getCOGClassesFromCommit(Repository repository, ObjectId commitID) throws Exception {
        Map<String, COGClass> allClasses = new TreeMap<>();
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitID);
        RevTree tree = commit.getTree();

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(TreeFilter.ALL);

        classesInFileMap.clear();

        while (treeWalk.next()) {
            String extension = treeWalk.getPathString().substring(treeWalk.getPathLength() - 4);
            if (!extension.equals("java")) continue;

            System.out.println(treeWalk.getPathString());
            ObjectId id = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(id);
            Map<String, COGClass> classes = Source2ClassConverter.convertFromStream(loader.openStream());
            allClasses.putAll(classes);
            classesInFileMap.put(treeWalk.getPathString(), new LinkedList<>(classes.values()));
        }
        return allClasses;
    }
    
    public static String getReadmeFromCommit(Repository repository, ObjectId commitID) throws Exception {
        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitID);
        RevTree tree = commit.getTree();

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create("README.md"));
        
        if(treeWalk.next())
        {
            ObjectId id = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(id);
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.openStream()));
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while(line!=null)
            {
                builder.append(line+"\n");
                line = reader.readLine();
            }
            return builder.toString();
        }
        return "Could not find readme file!";
        
    }

    ///////////////////////////////
    // Obsługa commitów i różnic //
    ///////////////////////////////

    public static void findAllCommits() throws IOException, GitAPIException {
        commitList.clear();
        Iterable<RevCommit> commits = git.log().all().call();
        for (RevCommit commit : commits) {
            commitList.add(commit.getName());
        }
    }

    public static void checkAllCommitsDiff() throws IOException, GitAPIException {
        Repository repository = git.getRepository();
        String headCommitId = repository.resolve(Constants.HEAD).getName();
        String prevCommitId = null;
        List<String> changedFiles = new LinkedList<>();

        for (String currentCommitId : commitList) {
            if (!currentCommitId.equals(headCommitId)) {
                //System.out.println("\nNew:" + headCommitId + "   Old:" + currentCommitId);
                changedFiles = checkInnerCommitsDiff(prevCommitId, currentCommitId);
                checkHeadCommitDiff(headCommitId, currentCommitId, changedFiles, prevCommitId);
            }
            prevCommitId = currentCommitId;
        }
    }

    private static void checkHeadCommitDiff(String newCommitId, String oldCommitId, List<String> changedFiles, String prevCommitId) throws GitAPIException, IOException {
        Repository repository = git.getRepository();

        List<DiffEntry> diff = checkCommitDiff(newCommitId, oldCommitId, repository);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DiffFormatter formatter = new DiffFormatter(outputStream);
        formatter.setRepository(repository);

        //Wzór do wyszukania ciągu znaków pokroju "@@ -10,1 +15,5 @@"
        Pattern pattern = Pattern.compile("@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@");

        for (DiffEntry entry : diff) {
            if (classesInFileMap.containsKey(entry.getNewPath()) &&
                    changedFiles.contains(entry.getNewPath())) {
                //System.out.println(entry.getNewPath());
                formatter.format(entry);
                Matcher matcher = pattern.matcher(outputStream.toString());

                while (matcher.find()) {
                    //System.out.println(matcher.group(1) + "," + matcher.group(2) + "  " + matcher.group(3) + "," + matcher.group(4));

                    //dodanie commitów do list w elementach
                    List<COGClass> classList = classesInFileMap.get(entry.getNewPath());
                    int start = Integer.valueOf(matcher.group(3));
                    int end = Integer.valueOf(matcher.group(3)) + Integer.valueOf(matcher.group(4)) - 1;
                    for (COGClass cogClass : classList) {
                        if (cogClass.addCommitToList(prevCommitId, outputStream.toString(), start, end)) {
                            for (COGClass.COGMethod cogMethod : cogClass.getMethods().values()) {
                                cogMethod.addCommitToList(prevCommitId, outputStream.toString(), start, end);
                            }
                        }
                    }
                }
            }
            outputStream.reset();
        }
    }

    private static List<String> checkInnerCommitsDiff(String newCommitId, String oldCommitId) throws GitAPIException, IOException {
        Repository repository = git.getRepository();
        List<String> changedFiles = new LinkedList<>();

        List<DiffEntry> diff = checkCommitDiff(newCommitId, oldCommitId, repository);

        for (DiffEntry entry : diff) {
            if (classesInFileMap.containsKey(entry.getNewPath())) {
                changedFiles.add(entry.getNewPath());
            }
        }

        return changedFiles;
    }

    private static List<DiffEntry> checkCommitDiff(String newCommitId, String oldCommitId, Repository repository) throws GitAPIException, IOException {
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newCommitId);
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldCommitId);

        return git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String commitId) throws IOException {
        RevWalk walk = new RevWalk(repository);
        RevCommit commit = walk.parseCommit(ObjectId.fromString(commitId));
        RevTree tree = walk.parseTree(commit.getTree().getId());

        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        ObjectReader reader = repository.newObjectReader();
        treeParser.reset(reader, tree.getId());

        walk.dispose();
        return treeParser;
    }
}
