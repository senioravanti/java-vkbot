package ru.senioravanti.vkbot.utils;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.util.ArrayList;

import ru.senioravanti.vkbot.models.vk.VkFormatData;

import static ru.senioravanti.vkbot.models.vk.VkFormatData.Item.Type;

public class MarkdownVkFormatter {
    private static final Parser PARSER = Parser.builder().build();

    private MarkdownVkFormatter() {
    }

    public static Result parse(String text) {
        var markdownDocument = PARSER.parse(text);
        var textBuilder = new StringBuilder();
        var formatItems = new ArrayList<VkFormatData.Item>();
        markdownDocument.accept(new AbstractVisitor() {
            @Override
            public void visit(Text n) {
                textBuilder.append(n.getLiteral());
            }

            @Override
            public void visit(SoftLineBreak n) {
                textBuilder.append("\n");
            }

            @Override
            public void visit(HardLineBreak n) {
                textBuilder.append("\n");
            }

            @Override
            public void visit(StrongEmphasis n) {
                var delimiter = n.getOpeningDelimiter();
                var formatType = switch (delimiter) {
                    case "**" -> Type.BOLD;
                    case "__" -> Type.UNDERLINE;
                    default -> throw new IllegalStateException("unknown delimiter " + delimiter);
                };
                int offset = textBuilder.length();
                visitChildren(n);
                int length = textBuilder.length() - offset;
                formatItems.add(new VkFormatData.Item(formatType, offset, length));
            }

            @Override
            public void visit(Emphasis n) {
                int offset = textBuilder.length();
                visitChildren(n);
                int length = textBuilder.length() - offset;
                formatItems.add(new VkFormatData.Item(Type.ITALIC, offset, length));
            }
        });
        return new Result(textBuilder.toString(), new VkFormatData(formatItems));
    }

    public record Result(
        String text,
        VkFormatData formatData
    ) {
    }
}
