/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.scene.web.WebView;

/**
 *
 * @author Grzesiek
 */
public class HighlighterFacade {
    public String htmlPrefix= "<html>\n" +
"\n" +
"<head>\n" +
"<title>Składnia</title>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shCore.js")+"\"></script>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shBrushJava.js")+"\"></script>\n" +
"<link rel=\"stylesheet\" type=\"text/css\" href=\""+getClass().getResource("syntaxhighlighter/shCore.css")+"\">\n" +
"<link rel=\"stylesheet\" type=\"text/css\" href=\""+getClass().getResource("syntaxhighlighter/shThemeDefault.css")+"\">\n" +
"\n" +
"</head>\n" +
"<body>\n" +
"<script type=\"syntaxhighlighter\" class = \"brush: java\"><![CDATA[\n" +
"\n";
    
    public static String htmlPostfix="]]></script>\n"+
"\n" +
"<script type=\"text/javascript\">\n" +
"     SyntaxHighlighter.all()\n" +
"</script>\n" +
"\n" +
"</body>\n" +
"</html>";
    
    public void displayHighlightedCode(String code, WebView webview)
    {
        webview.getEngine().loadContent(htmlPrefix+code+htmlPostfix);
    }
}
