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

package me.lynxplay.idonis.core.dialect;

import me.lynxplay.idonis.IdonisContainer;
import me.lynxplay.idonis.core.dialect.file.FileStringReader;
import me.lynxplay.idonis.dialect.StatementKey;
import me.lynxplay.idonis.dialect.StatementPromise;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class LazyLoadIdonisContainer implements IdonisContainer {

    private Map<StatementKey, StatementPromise> wrapped = new HashMap<>();
    private Path root;
    private Function<Path, StatementKey> keyGenerator;
    private FileStringReader fileStringReader;

    /**
     * Creates a new idonis map based on the root path
     *
     * @param root the root
     * @param keyGenerator the key generator
     */
    public LazyLoadIdonisContainer(Path root, Function<Path, StatementKey> keyGenerator) {
        this(root, keyGenerator, Files::readString);
    }

    /**
     * Creates a new idonis map based on the root path
     *
     * @param root the root
     * @param keyGenerator the key generator
     * @param fileReader the file reader function
     */
    public LazyLoadIdonisContainer(Path root, Function<Path, StatementKey> keyGenerator, FileStringReader fileReader) {
        this.root = root;
        this.keyGenerator = keyGenerator;
        this.fileStringReader = fileReader;
    }

    /**
     * Returns the {@link StatementPromise} instance for this key. If there is not value for the specific key, this
     * method will return an empty {@link StatementPromise} which will fail to execute on call.
     *
     * @param key the key to fetch against
     *
     * @return the cached instance
     */
    @Override
    public StatementPromise using(StatementKey key) {
        Path resolvedPath = key.resolveFile(root);
        return this.wrapped.computeIfAbsent(key, k -> this.read(resolvedPath)
                .<StatementPromise>map(ValidStatementPromise::new)
                .orElse(new EmptyStatementPromise(resolvedPath)));
    }

    /**
     * Returns the key generator of the container, that converts a path into a key
     *
     * @return the key function
     */
    @Override
    public Function<Path, StatementKey> keyGenerator() {
        return this.keyGenerator;
    }

    /**
     * Returns the path this container is pointing to
     *
     * @return the path instance
     */
    @Override
    public Path path() {
        return root;
    }

    /**
     * Reads the content of the file at this path, or else returns an empty {@link Optional}
     *
     * @param path the path to read
     *
     * @return the {@link Optional}
     */
    private Optional<String> read(Path path) {
        try {
            if (!Files.exists(path) || !Files.isRegularFile(path)) return Optional.empty();
            return Optional.of(this.fileStringReader.read(path));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not read file at path %s", path.toString()), e);
        }
    }
}
