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

import me.lynxplay.idonis.dialect.SQLDialect;
import me.lynxplay.idonis.dialect.StatementKey;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * The idonis interface represents the main entry point for projects when using idonis
 */
public interface Idonis {

    /**
     * Returns the map representing the loaded dialect files. This will not cache any previously loaded dialect maps and
     * will always create a fresh copy. This will default all created {@link StatementKey} to the usage of {@link
     * Idonis#simpleStringPath(Path)}
     *
     * @param idonisFolder the idonis folder under which all of those statements lie
     * @param dialect the dialect
     *
     * @return the container instance.
     */
    default IdonisContainer forDialect(Path idonisFolder,
                                       SQLDialect dialect) {
        return this.forDialect(idonisFolder, dialect, p -> this.simpleStringPath(p.getFileName()));
    }

    /**
     * Returns the map representing the loaded dialect files. This will not cache any previously loaded dialect maps and
     * will always create a fresh copy. This method will created the {@link StatementKey} instances based on the
     * provided {@link Function}
     *
     * @param idonisFolder the idonis folder under which all of those statements lie
     * @param dialect the dialect
     * @param keyGenerator the key generator used to fill the keys.
     *
     * @return the container instance.
     */
    IdonisContainer forDialect(Path idonisFolder,
                               SQLDialect dialect,
                               Function<Path, StatementKey> keyGenerator);

    /**
     * Creates a simple {@link StatementKey} that is based on a sub path
     *
     * @param subPath the sub path
     *
     * @return the created {@link StatementKey} instance
     */
    StatementKey simpleStringPath(Path subPath);
}
