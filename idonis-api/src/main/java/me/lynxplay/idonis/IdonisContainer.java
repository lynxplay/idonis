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

package me.lynxplay.idonis;

import me.lynxplay.idonis.dialect.StatementKey;
import me.lynxplay.idonis.dialect.StatementPromise;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * This interface defines a map like lookup which
 */
public interface IdonisContainer {

    /**
     * Returns the {@link StatementPromise} instance for this key. If there is not value for the specific key, this
     * method will return an empty {@link StatementPromise} which will fail to execute on call.
     *
     * @param key the key to fetch against
     *
     * @return the cached instance
     */
    default StatementPromise using(String key) {
        return this.using(keyGenerator().apply(Path.of(key)));
    }

    /**
     * Returns the {@link StatementPromise} instance for this key. If there is not value for the specific key, this
     * method will return an empty {@link StatementPromise} which will fail to execute on call.
     *
     * @param key the key to fetch against
     *
     * @return the cached instance
     */
    StatementPromise using(StatementKey key);

    /**
     * Returns the key generator of the container, that converts a path into a key
     *
     * @return the key function
     */
    Function<Path, StatementKey> keyGenerator();

    /**
     * Returns the path this container is pointing to
     *
     * @return the path instance
     */
    Path path();
}
