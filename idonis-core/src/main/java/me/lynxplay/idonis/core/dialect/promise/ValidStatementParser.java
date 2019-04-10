/*
 * MIT License
 *
 * Copyright (c) 2019 Bjarne Koll
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.lynxplay.idonis.core.dialect.promise;

import me.lynxplay.idonis.dialect.promise.StatementPromise;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represents a very basic statement parser
 */
public class ValidStatementParser implements Function<String, StatementPromise> {

    /**
     * The internal comment pattern to match the comment in which the variables are defined
     */
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?s)/\\*(.*)\\*/");

    /**
     * The placeholder pattern to find placeholders in the SQL statement
     */
    private static final Pattern PLACEHOLDER = Pattern.compile("\\?");

    /**
     * Generates the {@link StatementPromise} instance based on the source string
     *
     * @param source the string used as a source
     *
     * @return the promise instance
     */
    @Override
    public StatementPromise apply(String source) {
        Matcher matcher = COMMENT_PATTERN.matcher(source);
        if (matcher.find()) {
            String group = matcher.group(1);
            Properties properties = new Properties();
            try {
                properties.load(new StringReader(group));
            } catch (IOException e) {
                throw new IllegalArgumentException("The provided comment could not be parsed as a properties file");
            }

            Map<String, Integer> content = properties.stringPropertyNames().stream()
                    .map(s -> Map.entry(s, Integer.parseInt(properties.getProperty(s))))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            source = matcher.replaceAll("");
        }

        String trimmed = source.trim().replaceAll(System.lineSeparator(), " ");
        return new ValidStatementPromise(trimmed, new int[0][0]);
    }
}
