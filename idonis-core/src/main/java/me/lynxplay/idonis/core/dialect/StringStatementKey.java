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

import me.lynxplay.idonis.dialect.StatementKey;

import java.nio.file.Path;

/**
 * A simple string based {@link StatementKey} implementation
 */
public class StringStatementKey implements StatementKey {

    private Path subPath;

    /**
     * Creates a new instance of the {@link StringStatementKey} class
     *
     * @param subPath the sub path this key points at
     */
    public StringStatementKey(Path subPath) {
        this.subPath = subPath;
    }

    /**
     * Resolves the specific {@link Path} of the statement file
     *
     * @param dialectFolder the dialect folder against which this key will resolve it's file
     *
     * @return the path of the script file
     */
    @Override
    public Path resolveFile(Path dialectFolder) {
        return dialectFolder.resolve(this.subPath);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StringStatementKey) {
            return this.subPath.equals(((StringStatementKey) obj).subPath);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.subPath.hashCode();
    }

    @Override
    public String toString() {
        return String.format("StringStatementKey{path: %s}" , this.subPath);
    }
}
