/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import gitbk.COGElement.COGElement;
import static gitbk.GitFacade.classesInFileMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.web.WebView;


public class HighlighterFacade {
    public String htmlPrefix= "<html>\n" +
"\n" +
"<head>\n" +
"<title>Składnia</title>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shCore.js")+"\"></script>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shBrushDiff.js")+"\"></script>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shBrushJava.js")+"\"></script>\n" +
            "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js\"></script>\n"+
"<script src=\"http://code.jquery.com/ui/1.10.4/jquery-ui.js\"></script>\n"+
"<link href=\"http://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css\" rel=\"stylesheet\">\n"+
"<link rel=\"stylesheet\" type=\"text/css\" href=\""+getClass().getResource("syntaxhighlighter/shCore.css")+"\">\n" +
"<link rel=\"stylesheet\" type=\"text/css\" href=\""+getClass().getResource("syntaxhighlighter/shThemeDefault.css")+"\">\n" +
"\n" +
"</head>\n" +
"<body>\n" +
"<script type=\"syntaxhighlighter\" class = \"brush: java;  gutter:false\"><![CDATA[\n" +
"\n";
    
    public static String htmlPostfix="]]></script>\n"+
"\n" +
"<script type=\"text/javascript\">\n"
            + "$(\"div\").tooltip()\n"+
"     SyntaxHighlighter.all()\n" +
"</script>\n" +
"\n" +
"</body>\n" +
"</html>";
    
    public void displayHighlightedCode(String code, WebView webview)
    {
        webview.getEngine().loadContent(htmlPrefix+code+htmlPostfix);
    }
    
    public static String expandSourceCode(COGElement actualElement, String sourceCode, String commit) throws Exception
    {
        
        String prefix = "]]></script>\n<script type=\"syntaxhighlighter\" class = \"brush: diff;  gutter:false\"><![CDATA[\n";
        String postfix = "]]></script>\n<script type=\"syntaxhighlighter\" class = \"brush: java; gutter:false\"><![CDATA[\n\n";
   
        List<String> sourceLines = Arrays.asList(sourceCode.split("\n"));
        List<String> resultList = sourceLines;
        String result = "";
        
        List<CommitChange> commitChanges = Source2ClassConverter.convertCommitToSingleCommitChange(commit);
        
        for(CommitChange change:commitChanges)
        {
           int beginIndex = change.begin-actualElement.getBeginLine();
           if(beginIndex < 0) continue;
           List<String> tempList = new ArrayList<String>(resultList.subList(0, beginIndex));
           tempList.add(prefix+change.changeCode+postfix);
           tempList.addAll(sourceLines.subList(change.end,sourceLines.size()-actualElement.getBeginLine()));
           
           resultList = tempList;
        }
        
        for(String r:resultList) result+=r;
        System.out.println(commit);
        return result;
    }
}
