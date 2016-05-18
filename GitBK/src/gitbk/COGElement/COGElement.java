/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk.COGElement;

import java.util.*;

/**
 * @author Grzesiek
 */
public abstract class COGElement {
    private int beginLine;
    private int endLine;
    private Map<String, String> commitsDiffs = new LinkedHashMap<>();

    public abstract void setName(String name);

    public abstract void setSource(String source);

    public abstract void setIsAbstract(boolean isAbstract);

    public abstract void setAccess(String access);

    public abstract void setAuthors(List<String> authors);

    public abstract void setLastChange(String lastChange);

    public abstract void setCreateDate(String createDate);

    public void setBeginLine(int beginLine) {
        this.beginLine = beginLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public abstract String getName();

    public abstract String getSource();

    public abstract boolean isAbstract();

    public abstract String getAccess();

    public abstract List<String> getAuthors();

    public abstract String getLastChange();

    public abstract String getCreateDate();

    public String getCommitById(String commitId) {
        return commitsDiffs.get(commitId);
    }

    public String getCommitByIndex(int index) {
        Iterator<String> it = commitsDiffs.keySet().iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        return commitsDiffs.get(it.next());
    }
    
    public String getCommitIdByIndex(int index) {
        Iterator<String> it = commitsDiffs.keySet().iterator();
        for (int i = 0; i < index; i++) {
            it.next();
        }
        return it.next();
    }

    public int getBeginLine() {
        return beginLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public boolean addCommitToList(String commitId, String diff, int start, int end) {
        if (beginLine <= end && endLine >= start) {
            if (!commitsDiffs.containsKey(commitId)) {
                if (!diff.equals(getOldestCommitDiff())) {
                    commitsDiffs.put(commitId, diff);
                }
                return true;
            }
        }
        return false;
    }

    public Set<String> getCommitIdSet() {
        return commitsDiffs.keySet();
    }

    public String getLastCommitId() {
        return commitsDiffs.keySet().iterator().next();
    }

    public String getOldestCommitId() {
        Iterator<String> iterator = commitsDiffs.keySet().iterator();
        String oldestCommitId = null;

        while (iterator.hasNext()) {
            oldestCommitId = iterator.next();
        }

        return oldestCommitId;
    }

    public String getOldestCommitDiff() {
        Iterator<String> iterator = commitsDiffs.values().iterator();
        String oldestCommitDiff = null;

        while (iterator.hasNext()) {
            oldestCommitDiff = iterator.next();
        }

        return oldestCommitDiff;
    }
}
