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

package me.lynxplay.idonis.core.dialect.file;

import java.io.IOException;
import java.nio.file.Path;

/**
 * The file reader interface defines an object that is capable of reading the contents of a {@link java.nio.file.Path}
 */
public interface FileStringReader {

    /**
     * Reads the content of the file located at the given path. If there is no file, or the path is a directory, the
     * returned String will be null
     *
     * @param path the path the file that should be read is located under
     *
     * @return the content of the file as a string.
     *
     * @throws IOException if they file could not be read, even tho the file was judged readable by the {@link
     * FileStringReader}
     */
    String read(Path path) throws IOException;

}
