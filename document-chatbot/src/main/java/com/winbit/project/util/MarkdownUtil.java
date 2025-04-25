package com.winbit.project.util;

import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.html.HtmlRenderer;

public class MarkdownUtil {
	private static final Parser parser = Parser.builder().build();
    private static final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public static String toHtml(String markdown) {
        return renderer.render(parser.parse(markdown));
    }
}	
