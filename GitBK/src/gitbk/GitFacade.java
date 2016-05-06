/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        git.commit().setMessage(message).call();
    }

    public static void pushRepo(Repository repository) throws GitAPIException {
        Git git = new Git(repository);
        PushCommand pushCommand = git.push();
        Iterable<PushResult> result = pushCommand.call();

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

    public static void checkAllCommitsDiff() throws IOException, GitAPIException {
        Repository repository = git.getRepository();
        String head = repository.resolve(Constants.HEAD).getName();
        String prevCommit = "";
        List<String> changedFiles = new LinkedList<>();

        for (String commit : commitList) {
            if (!commit.equals(head)) {
                //System.out.println("\nNew:" + head + "   Old:" + commit);
                if (!prevCommit.equals("")) {
                    changedFiles = checkInnerCommitsDiff(prevCommit, commit);
                }
                checkHeadCommitDiff(head, commit, changedFiles);
                prevCommit = commit;
            }
        }
    }

    public static void checkHeadCommitDiff(String newCommit, String oldCommit, List<String> changedFiles) throws GitAPIException, IOException {
        Repository repository = git.getRepository();

        List<DiffEntry> diff = checkCommitDiff(newCommit, oldCommit, repository);

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
                        if (cogClass.addCommitToList(oldCommit, start, end)) {
                            for (COGClass.COGMethod cogMethod : cogClass.getMethods().values()) {
                                cogMethod.addCommitToList(oldCommit, start, end);
                            }
                        }
                    }
                }

            }
            outputStream.reset();
        }
    }

    public static List<String> checkInnerCommitsDiff(String newCommit, String oldCommit) throws GitAPIException, IOException {
        Repository repository = git.getRepository();
        List<String> changedFiles = new LinkedList<>();

        List<DiffEntry> diff = checkCommitDiff(newCommit, oldCommit, repository);

        for (DiffEntry entry : diff) {
            if (classesInFileMap.containsKey(entry.getNewPath())) {
                changedFiles.add(entry.getNewPath());
            }
        }

        return changedFiles;
    }

    public static List<DiffEntry> checkCommitDiff(String newCommit, String oldCommit, Repository repository) throws GitAPIException, IOException {
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newCommit);
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldCommit);

        return git.diff().setOldTree(oldTreeParser).setNewTree(newTreeParser).call();
    }

    public static void findAllCommits() throws IOException, GitAPIException {
        commitList.clear();
        Iterable<RevCommit> commits = git.log().all().call();
        for (RevCommit commit : commits) {
            commitList.add(commit.getName());
        }

    }

    public static void findFileCommits(String newCommitId, String oldCommitId, String fileName) throws IOException {
        Repository repository = git.getRepository();
        AbstractTreeIterator newTreeParser = prepareTreeParser(repository, newCommitId);
        AbstractTreeIterator oldTreeParser = prepareTreeParser(repository, oldCommitId);

        try {
            List<DiffEntry> diff = git.diff().
                    setOldTree(oldTreeParser).
                    setNewTree(newTreeParser).
                    //setPathFilter(PathSuffixFilter.create(fileName)).
                            call();
            for (DiffEntry entry : diff) {
                System.out.println("Entry: " + entry + ", from: " + entry.getOldId() + ", to: " + entry.getNewId());
                try (DiffFormatter formatter = new DiffFormatter(System.out)) {
                    formatter.setRepository(repository);
                    formatter.format(entry);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
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
