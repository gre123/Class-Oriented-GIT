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
    public static String htmlPrefix= "<html>\n" +
"\n" +
"<head>\n" +
"<title>Składnia</title>\n" +
"<script src=\"C:/Users/Grzesiek/Moje rzeczy/rewizje/COG/Class-Oriented-GIT/GitBK/shCore.js\"></script>\n" +
"<script src=\"C:/Users/Grzesiek/Moje rzeczy/rewizje/COG/Class-Oriented-GIT/GitBK/shBrushJava.js\"></script>\n" +
"<script src=\"\"></script>\n" +
"<link rel=\"stylesheet\" type=\"text/css\" href=\"C:/Users/Grzesiek/Moje rzeczy/rewizje/COG/Class-Oriented-GIT/GitBK/shCore.css\">\n" +
"<link rel=\"stylesheet\" type=\"text/css\" href=\"C:/Users/Grzesiek/Moje rzeczy/rewizje/COG/Class-Oriented-GIT/GitBK/shThemeDefault.css\">\n" +
"\n" +
"</head>\n" +
"<body>\n" +
"<pre class=\"brush: java\">\n" +
"\n";
    
    public static String htmlPostfix="</pre>\n"+
"\n" +
"<script type=\"text/javascript\">\n" +
"     SyntaxHighlighter.all()\n" +
"</script>\n" +
"\n" +
"</body>\n" +
"</html>";
    
    public static void highlightCode(String code, WebView webview)
    {
        webview.getEngine().loadContent(htmlPrefix+code+htmlPostfix);
    }
}
