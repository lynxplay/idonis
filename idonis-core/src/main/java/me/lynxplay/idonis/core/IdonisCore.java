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

package me.lynxplay.idonis.core;

import me.lynxplay.idonis.Idonis;
import me.lynxplay.idonis.IdonisContainer;
import me.lynxplay.idonis.core.dialect.LazyLoadIdonisContainer;
import me.lynxplay.idonis.core.dialect.StringStatementKey;
import me.lynxplay.idonis.core.dialect.promise.parser.ValidStatementParser;
import me.lynxplay.idonis.dialect.SQLDialect;
import me.lynxplay.idonis.dialect.StatementKey;
import me.lynxplay.idonis.dialect.promise.StatementPromise;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

public class IdonisCore implements Idonis {


    /**
     * Returns the map representing the loaded dialect files. This will not cache any previously loaded dialect maps and
     * will always create a fresh copy. This method will created the {@link StatementKey} instances based on the
     * provided {@link Function}.
     *
     * @param idonisFolder the idonis folder under which all of those statements lie
     * @param dialect the dialect
     * @param keyGenerator the key generator used to fill the keys.
     *
     * @return the container instance.
     */
    @Override
    public IdonisContainer forDialect(Path idonisFolder, SQLDialect dialect, Function<Path, StatementKey> keyGenerator) {
        return this.forDialect(idonisFolder, dialect, keyGenerator, new ValidStatementParser());
    }

    /**
     * Returns the map representing the loaded dialect files. This will not cache any previously loaded dialect maps and
     * will always create a fresh copy. This method will created the {@link StatementKey} instances based on the
     * provided {@link Function}. The container generates the given {@link StatementPromise} based on the provided
     * generator function.
     *
     * @param idonisFolder the idonis folder under which all of those statements lie
     * @param dialect the dialect
     * @param keyGenerator the key generator used to fill the keys.
     * @param statementParser the parser for the statements if the container generates one
     *
     * @return the container instance.
     */
    @Override
    public IdonisContainer forDialect(Path idonisFolder, SQLDialect dialect, Function<Path, StatementKey> keyGenerator,
                                      Function<String, StatementPromise> statementParser) {
        return new LazyLoadIdonisContainer(dialect.resolve(idonisFolder), keyGenerator, Files::readString , statementParser);
    }

    /**
     * Creates a simple {@link StatementKey} that is based on a sub path string
     *
     * @param subPath the sub path
     *
     * @return the created {@link StatementKey} instance
     */
    @Override
    public StatementKey simpleStringPath(Path subPath) {
        return new StringStatementKey(subPath);
    }
}
