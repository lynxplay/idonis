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

package me.lynxplay.idonis.core.dialect.promise.parser;

import me.lynxplay.idonis.core.dialect.promise.ValidStatementPromise;
import me.lynxplay.idonis.dialect.promise.StatementPromise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
            source = matcher.replaceAll("");

            StringBuilder buffer = new StringBuilder(source);

            List<CachedStringIndexer> indexers = Arrays.stream(group.split(System.lineSeparator()))
                    .filter(s -> !s.isBlank())
                    .map(t -> new CachedStringIndexer(buffer, t))
                    .collect(Collectors.toList());
            CachedStringIndexer normalParameter = new CachedStringIndexer(buffer, "?");

            int fakeIndex = indexers.size();
            int subIndex = 0;

            List<Integer> actualToFake = new ArrayList<>();

            while (true) {
                Map.Entry<Integer, CachedStringIndexer> found = Map.entry(normalParameter.find(subIndex), normalParameter);
                int nextFakeIndex = fakeIndex;
                for (int i = 0; i < indexers.size(); i++) {
                    CachedStringIndexer indexer = indexers.get(i);
                    if (indexer.find(subIndex) >= 0 && (indexer.find(subIndex) < found.getKey() || found.getKey() < 0)) {
                        found = Map.entry(indexer.find(subIndex), indexer);
                        nextFakeIndex = i;
                    }
                }

                if (found.getKey() < 0) break;
                if (found.getValue() == normalParameter) {
                    // Increase fake index by one as we found a ?
                    nextFakeIndex = fakeIndex++;
                } else {
                    // Replace the found variable with the ?
                    int targetLength = found.getValue().getTarget().length();
                    buffer.replace(found.getKey(), found.getKey() + targetLength, "?"); // Replace the found key in the cache
                    indexers.forEach(i -> i.adjustCache(-targetLength + 1)); // Adjust all caches
                }

                actualToFake.add(nextFakeIndex);
                subIndex = found.getKey() + 1; // Skip to after variable as we just replaced it
            }

            source = buffer.toString();

            AtomicInteger index = new AtomicInteger(1);
            actualToFake.forEach(i -> System.out.println(index.getAndIncrement() + " -> " + (i + 1)));
        }

        String trimmed = source.replaceAll(System.lineSeparator(), " ").replaceAll(" +", " ");
        return new ValidStatementPromise(trimmed, new int[0][0]);
    }
}
