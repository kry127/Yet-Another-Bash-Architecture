package ru.spb.kry127.yaba.ast;

import org.jetbrains.annotations.NotNull;
import ru.spb.kry127.yaba.io.Environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Определяет, что выражение является строковым литералом.
 * Используется для выражения:
 * <ul>
 *     <li>Параметров команд</li>
 *     <li>присвоений переменной</li>
 * </ul>
 */
public abstract class Literal implements Expression {
    private static final Pattern DOLLARZ = Pattern.compile("\\$([A-Za-z_][\\w\\d]*)");
    private String contents;

    protected Literal(String contents) {
        this.contents = contents;
    }

    protected String getRawContents() {
        return contents;
    }

    // TODO точно ли этот метод нужен здесь, а не в парсере?
    // Ответ -- пока что нет, мы предполагаем, что парсер не знает, как собирается строка
    @Override
    public String interpolate(@NotNull Environment environment) {
        Matcher m = DOLLARZ.matcher(contents);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String repString = environment.getEnvVariable(m.group(1));
            m.appendReplacement(sb, repString);
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
