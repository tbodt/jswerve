/*
 * Copyright (C) 2015 theodore
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tbodt.jtl;

import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * A class with static methods that parse and generate code for JTL templates.
 *
 * @author theodore
 */
public final class Jtl {
    public static String generateCode(String jtl) {
        JtlParser parser = new JtlParser(new CommonTokenStream(new JtlLexer(new ANTLRInputStream(jtl))));
        ParseTree tree = parser.template();
        ParseTreeWalker walker = new ParseTreeWalker();
        final StringBuilder builder = new StringBuilder();
        builder.append("StringBuilder _builder = new StringBuilder();\n");
        ParseTreeListener listener = new JtlParserBaseListener() {
            @Override
            public void enterText(JtlParser.TextContext ctx) {
                builder.append("_builder.append(\"");
                builder.append(StringEscapeUtils.escapeJava(ctx.getText()));
                builder.append("\");\n");
            }

            @Override
            public void enterEmbed(JtlParser.EmbedContext ctx) {
                builder.append("_builder.append(");
                builder.append(combineText(ctx.CODE()));
                builder.append(");\n");
            }

            @Override
            public void enterCode(JtlParser.CodeContext ctx) {
                builder.append("\n");
                builder.append(combineText(ctx.CODE()));
                builder.append("\n");
            }
        };
        walker.walk(listener, tree);
        builder.append("return _builder.toString();\n");
        return builder.toString();
    }

    private static String combineText(List<TerminalNode> tokens) {
        StringBuilder builder = new StringBuilder();
        for (TerminalNode token : tokens)
            builder.append(token.getText());
        return builder.toString();
    }

    private Jtl() {
    }
}
