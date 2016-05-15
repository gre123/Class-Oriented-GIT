/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import gitbk.COGElement.COGElement;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class HighlighterFacade {
    public enum CodeType {
        JAVA, DIFF
    }

    public String htmlPrefix;
    public String htmlPostfix;

    public HighlighterFacade(CodeType code) {
        switch (code) {
            case JAVA: {
                htmlPrefix = "<html>\n" +
                        "<head>\n" +
                        "<title>Składnia</title>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shCore.js") + "\"></script> \n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shBrushDiff.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shBrushJava.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/jquery-1.12.3.min.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/jquery-ui.min.js") + "\"></script>\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/shCore.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/jquery-ui.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/shThemeDefault.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/style.css") + "\">\n" +
                        "\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div id=\"progressbar\"><div class=\"progress-label\">Loading...</div></div>\n" +
                        "<script> $( \"#progressbar\" ).progressbar({\n  value: false\n" +
                        "    });</script>\n" +
                        "<script type=\"syntaxhighlighter\" class = \"brush: java;\"><![CDATA[\n" +
                        "\n";
                break;
            }
            case DIFF: {
                htmlPrefix = "<html>\n" +
                        "<head>\n" +
                        "<title>Składnia</title>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shCore.js") + "\"></script> \n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shBrushDiff.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/shBrushJava.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/jquery-1.12.3.min.js") + "\"></script>\n" +
                        "<script src=\"" + getClass().getResource("syntaxhighlighter/jquery-ui.min.js") + "\"></script>\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/shCore.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/jquery-ui.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/shThemeDefault.css") + "\">\n" +
                        "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + getClass().getResource("syntaxhighlighter/style.css") + "\">\n" +
                        "\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div id=\"progressbar\"><div class=\"progress-label\">Loading...</div></div>\n" +
                        "<script> $( \"#progressbar\" ).progressbar({\n  value: false\n" +
                        "    });</script>\n" +
                        "<script type=\"syntaxhighlighter\" class = \"brush: diff;\"><![CDATA[\n" +
                        "\n";
                break;
            }
        }
        htmlPostfix = "]]></script>\n" +
                "\n" +
                "<script type=\"text/javascript\">\n" +
                "SyntaxHighlighter.all()\n" +
                "$( \"#progressbar\" ).hide()\n" +
                "</script>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }


    public void displayHighlightedCode(String code, WebView webview) {
        webview.getEngine().loadContent(htmlPrefix + code + htmlPostfix);
    }

    public static String expandSourceCode(COGElement actualElement, String sourceCode, String commit) throws Exception {

        String prefix = "]]></script>\n<hr/><div class=\"commitchange\"><script type=\"syntaxhighlighter\" class = \"brush: diff;  gutter:false\"><![CDATA[\n";
        String postfix = "]]></script></div>\n<script type=\"syntaxhighlighter\" class = \"brush: java; gutter:false\"><![CDATA[\n\n";

        List<String> sourceLines = Arrays.asList(sourceCode.split("\n"));
        List<String> resultList = sourceLines;
        String result = "";

        List<CommitChange> commitChanges = Source2ClassConverter.convertCommitToSingleCommitChange(commit);
        
        for(CommitChange change:commitChanges)
        {
           int beginIndex = change.begin-actualElement.getBeginLine();
           if(beginIndex < 0 || beginIndex > resultList.size()) continue;
           List<String> tempList = new ArrayList<String>(resultList.subList(0, beginIndex));
           tempList.add("\n");
           tempList.addAll(Arrays.asList(change.changeCode.split("\n")));
           tempList.addAll(sourceLines.subList(change.end-actualElement.getBeginLine(), sourceLines.size()));
           
           resultList = tempList;
        }
        
        for(String r:resultList) result+=r;
        System.out.println(result);
        return result;
    }
}
