/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gitbk;

import javafx.scene.web.WebView;


public class HighlighterFacade {
    public String htmlPrefix= "<html>\n" +
"\n" +
"<head>\n" +
"<title>Składnia</title>\n" +
"<script src=\""+getClass().getResource("syntaxhighlighter/shCore.js")+"\"></script>\n" +
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
    
    public static String expandSourceCode(String sourceCode, String additionalCode, int beginLine)
    {
        String prefix = "]]></script>\n<div style=\"text-align:left; color:gray; cursor: arrow;\" title=\"";
        String postfix = "\">COMMIT CHANGE-----------------------------------------------------------------------------------------------</div><script type=\"syntaxhighlighter\" class = \"brush: java; gutter:false\"><![CDATA[\n\n";
        
        String[] sourceLines = sourceCode.split("\n");
        String result = "";
        for(int i=0;i<sourceLines.length;i++)
        {
            if(i==beginLine) result+=prefix+additionalCode+postfix;
            result+=sourceLines[i];
        }
        return result;
    }
}
